package com.example.ddmdemo.service.impl;

import ai.djl.translate.TranslateException;
import com.example.ddmdemo.dto.AddressDTO;
import com.example.ddmdemo.dto.ForensicReportIndexDTO;
import com.example.ddmdemo.exceptionhandling.exception.LoadingException;
import com.example.ddmdemo.exceptionhandling.exception.StorageException;
import com.example.ddmdemo.indexmodel.DummyIndex;
import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import com.example.ddmdemo.indexrepository.DummyIndexRepository;
import com.example.ddmdemo.indexrepository.ForensicReportIndexRepository;
import com.example.ddmdemo.model.Address;
import com.example.ddmdemo.model.DummyTable;
import com.example.ddmdemo.model.ForensicReport;
import com.example.ddmdemo.respository.DummyRepository;
import com.example.ddmdemo.respository.ForensicReportRepository;
import com.example.ddmdemo.service.interfaces.AddressService;
import com.example.ddmdemo.service.interfaces.FileService;
import com.example.ddmdemo.service.interfaces.ForensicIndexingService;
import com.example.ddmdemo.util.VectorizationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.apache.tika.language.detect.LanguageDetector;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForensicIndexingServiceImpl implements ForensicIndexingService {

    private final ForensicReportIndexRepository forensicReportIndexRepository;

    private final ForensicReportRepository forensicReportRepository;

    private final FileService fileService;

    private final LanguageDetector languageDetector;

    private final AddressService addressService;

    // Za Organizaciju (sve posle reči Organizacija)
    private static final Pattern ORG_PATTERN = Pattern.compile("Organizacija\\s+(.*)");

    private static final Pattern ADDRESS_LINE_PATTERN = Pattern.compile(
            "(?<=Organizacija\\s+[^\\n]+\\s+)?([A-Za-zČčĆćŠšĐđŽž\\s\\.,'-]+?)\\s*,\\s*(\\d+[A-Za-z]?)\\s*,\\s*([A-Za-zČčĆćŠšĐđŽž\\s-]+)(?:\\.|\\s|$)",
            Pattern.CASE_INSENSITIVE
    );

    // Za Klasifikaciju (tekst između "Klasifikacija:" i zareza)
    private static final Pattern CLASS_PATTERN = Pattern.compile("Klasifikacija:\\s*([^,]+)");

    // Za Hash (SHA256 - tačno 64 heksadecimalna karaktera)
    private static final Pattern HASH_PATTERN = Pattern.compile("[a-fA-F0-9]{64}");

    // Za Malware naziv (reč pre tačke u rečenici o artefaktu)
    private static final Pattern MALWARE_NAME_PATTERN = Pattern.compile("ukazuje na\\s+([^.]+)\\.");

    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile(
            "(?s)Opis ponašanja malvera/pretnje:\\s*(.*?)(?=\\s*\\n\\s*\\n\\s*\\n)",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    @Transactional
    public ForensicReportIndexDTO indexDocument(MultipartFile documentFile) {
        var newEntity = new ForensicReport();
        var newIndex = new ForensicReportIndex();

        var title = Objects.requireNonNull(documentFile.getOriginalFilename()).split("\\.")[0];
        newIndex.setFileName(title);
        newEntity.setFileName(title);

//        var documentContent = extractDocumentContent(documentFile);
        var documentContent = extractDocumentContentWithTika(documentFile);
        System.out.println("SADRZAJ: " + documentContent);
        newIndex.setContent(documentContent);
        parseText(documentContent, newIndex, newEntity);
        parseAddress(documentContent, newIndex);

        var serverFilename = fileService.store(documentFile, UUID.randomUUID().toString());
        newIndex.setServerFilename(serverFilename);
        newEntity.setServerFilename(serverFilename);



        newEntity.setMimeType(detectMimeType(documentFile));
        var savedEntity = forensicReportRepository.save(newEntity);

        try {
            newIndex.setVectorizedContent(VectorizationUtil.getEmbedding(title));
        } catch (TranslateException e) {
            log.error("Could not calculate vector representation for document with ID: {}",
                    savedEntity.getId());
        }
        newIndex.setDatabaseId(savedEntity.getId());
        forensicReportIndexRepository.save(newIndex);

        var forensicReportIndex = new ForensicReportIndexDTO(newIndex);

        return forensicReportIndex;
    }

    private void parseText(String text, ForensicReportIndex index, ForensicReport entity) {
        Matcher m;

        // Organizacija
        m = ORG_PATTERN.matcher(text);
        if (m.find()) index.setOrganizationName(m.group(1).trim());

        // Klasifikacija
        m = CLASS_PATTERN.matcher(text);
        if (m.find()) index.setThreatClassification(m.group(1).trim());

        // Hash
        m = HASH_PATTERN.matcher(text);
        if (m.find()) index.setSampleHash(m.group(0));

        // Naziv Malvera (npr. Emotet)
        m = MALWARE_NAME_PATTERN.matcher(text);
        if (m.find()) index.setMalwareName(m.group(1).trim());

        m = DESCRIPTION_PATTERN.matcher(text);
        if (m.find()) index.setMalwareDescription(m.group(1).trim());
    }

    private void parseAddress(String text, ForensicReportIndex index) {
        String cleanText = text;

        System.out.println("--- DEBUG PARSIRANJE ADRESE ---");
        System.out.println("CISCENI TEKST: " + cleanText);

        Matcher orgMatcher = ORG_PATTERN.matcher(cleanText);
        String textForAddress = cleanText;
        if (orgMatcher.find()) {
            // Uzmi sve posle organizacije
            textForAddress = cleanText.substring(orgMatcher.end()).trim();
            System.out.println("TEKST ZA ADRESU: " + textForAddress);
        }

        // Sada traži adresu u ostatku teksta
        Matcher m2 = ADDRESS_LINE_PATTERN.matcher(textForAddress);
        if (m2.find()) {
            // Ispiši sve grupe da vidimo šta imamo
            System.out.println("GROUP 0: '" + m2.group(0) + "'"); // cela adresa
            System.out.println("GROUP 1: '" + m2.group(1) + "'"); // ulica
            System.out.println("GROUP 2: '" + m2.group(2) + "'"); // broj
            System.out.println("GROUP 3: '" + m2.group(3) + "'"); // grad

            processAddress(m2.group(1), m2.group(2), m2.group(3), index);
            return;
        }

        // Ako ništa ne radi, ispiši deo teksta za ručnu proveru
        System.out.println("ERROR: Address regex nije našao poklapanje!");
        System.out.println("PRVIH 500 KARAKTERA: " + cleanText.substring(0, Math.min(500, cleanText.length())));
        System.out.println("-------------------------------");
    }

    private void processAddress(String street, String houseNumber, String city, ForensicReportIndex index) {
        street = street.trim();
        houseNumber = houseNumber.trim();
        city = city.trim();

        System.out.println("PRONAĐENO: " + street + " " + houseNumber + " " + city);

//        AddressDTO testAddress = new AddressDTO("Mise Dimitrijevica", "25", "Novi Sad");
        Address address = addressService.createAddress(new AddressDTO(street, houseNumber, city));


        if (address != null && address.getLat() != null) {
            System.out.println("LOKACIJA DOBIJENA: " + address.getLat() + "," + address.getLon());
            index.setLocation(new GeoPoint(address.getLat(), address.getLon()));
        } else {
            System.out.println("WARNING: Address service nije vratio koordinate!");
        }
    }

    private String extractDocumentContentWithTika(MultipartFile multipartPdfFile) {
        try {
            // Naprednija Tika konfiguracija
            Tika tika = new Tika();

            // Detektuj encoding automatski
            String content = tika.parseToString(multipartPdfFile.getInputStream());

            // Ako Tika ne radi, probaj PDFBox sa UTF-8
            if (content.contains("�")) {
                content = extractWithPDFBoxAndFix(multipartPdfFile);
            }

            return content;

        } catch (Exception e) {
            throw new LoadingException("Error: " + e.getMessage());
        }
    }

    private String extractWithPDFBoxAndFix(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setLineSeparator("\n");
            String text = stripper.getText(document);

            // Jedino što RADIŠ je konverzija iz ISO-8859-1 u UTF-8
            return new String(text.getBytes("ISO-8859-1"), "UTF-8");
        }
    }

    private String detectMimeType(MultipartFile file) {
        var contentAnalyzer = new Tika();

        String trueMimeType;
        String specifiedMimeType;
        try {
            trueMimeType = contentAnalyzer.detect(file.getBytes());
            specifiedMimeType =
                    Files.probeContentType(Path.of(Objects.requireNonNull(file.getOriginalFilename())));
        } catch (IOException e) {
            throw new StorageException("Failed to detect mime type for file.");
        }

        if (!trueMimeType.equals(specifiedMimeType) &&
                !(trueMimeType.contains("zip") && specifiedMimeType.contains("zip"))) {
            throw new StorageException("True mime type is different from specified one, aborting.");
        }

        return trueMimeType;
    }
}
