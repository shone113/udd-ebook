package com.example.ddmdemo.service.impl;

import ai.djl.translate.TranslateException;
import com.example.ddmdemo.dto.ForensicReportIndexDTO;
import com.example.ddmdemo.exceptionhandling.exception.LoadingException;
import com.example.ddmdemo.exceptionhandling.exception.StorageException;
import com.example.ddmdemo.indexmodel.DummyIndex;
import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import com.example.ddmdemo.indexrepository.DummyIndexRepository;
import com.example.ddmdemo.indexrepository.ForensicReportIndexRepository;
import com.example.ddmdemo.model.DummyTable;
import com.example.ddmdemo.model.ForensicReport;
import com.example.ddmdemo.respository.DummyRepository;
import com.example.ddmdemo.respository.ForensicReportRepository;
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

    // Za Organizaciju (sve posle reči Organizacija)
    private static final Pattern ORG_PATTERN = Pattern.compile("Organizacija\\s+(.*)");

    private static final Pattern ADDRESS_LINE_PATTERN = Pattern.compile("([^,]+),\\s*(\\d+),\\s*([^\\d\\n\\r]+)");

    // Za Klasifikaciju (tekst između "Klasifikacija:" i zareza)
    private static final Pattern CLASS_PATTERN = Pattern.compile("Klasifikacija:\\s*([^,]+)");

    // Za Hash (SHA256 - tačno 64 heksadecimalna karaktera)
    private static final Pattern HASH_PATTERN = Pattern.compile("[a-fA-F0-9]{64}");

    // Za Malware naziv (reč pre tačke u rečenici o artefaktu)
    private static final Pattern MALWARE_NAME_PATTERN = Pattern.compile("ukazuje na\\s+([^.]+)\\.");

    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("(?s)Opis ponašanja malvera/pretnje:\\s*(.*?)(?=\\s+[A-Z][a-z]+)");

    @Override
    @Transactional
    public ForensicReportIndexDTO indexDocument(MultipartFile documentFile) {
        var newEntity = new ForensicReport();
        var newIndex = new ForensicReportIndex();

        var title = Objects.requireNonNull(documentFile.getOriginalFilename()).split("\\.")[0];
        newIndex.setFileName(title);
        newEntity.setFileName(title);

        var documentContent = extractDocumentContent(documentFile);
        System.out.println("SADRZAJ: " + documentContent);
        parseText(documentContent, newIndex, newEntity);

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
        // Čistimo tekst od lošeg encoding-a (eneva -> Ženeva)
        String cleanText = text.replace("", "Ž");

        Matcher m = ADDRESS_LINE_PATTERN.matcher(cleanText);
        if (m.find()) {
            String street = m.group(1).trim();      // Rue des
            String houseNumber = m.group(2).trim(); // 51
            String city = m.group(3).trim();        // Ženeva

            // Ovde sada možeš da pozoveš tvoj AddressService
            // Ili da spakuješ u DTO za OpenCage
            System.out.println("Ekstraktovana adresa: " + street + " " + houseNumber + ", " + city);

            // Ako u ForensicReportIndex imaš polje za adresu, možeš ga setovati
            // index.setAddress(street + " " + houseNumber + ", " + city);
        }
    }

    private String extractDocumentContent(MultipartFile multipartPdfFile) {
        String documentContent;
        try (var pdfFile = multipartPdfFile.getInputStream()) {
            var pdDocument = PDDocument.load(pdfFile);
            var textStripper = new PDFTextStripper();
            documentContent = textStripper.getText(pdDocument);
            pdDocument.close();
        } catch (IOException e) {
            throw new LoadingException("Error while trying to load PDF file content.");
        }

        return documentContent;
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
