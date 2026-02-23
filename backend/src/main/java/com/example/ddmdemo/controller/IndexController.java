package com.example.ddmdemo.controller;

import com.example.ddmdemo.dto.DummyDocumentFileDTO;
import com.example.ddmdemo.dto.DummyDocumentFileResponseDTO;
import com.example.ddmdemo.dto.ForensicReportIndexDTO;
import com.example.ddmdemo.service.interfaces.ForensicIndexingService;
import com.example.ddmdemo.service.interfaces.IndexingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/index")
@RequiredArgsConstructor
public class IndexController {

    private final IndexingService indexingService;

    private final ForensicIndexingService forensicIndexingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ForensicReportIndexDTO addDocumentFile(
        @ModelAttribute DummyDocumentFileDTO documentFile) {
        return forensicIndexingService.indexDocument(documentFile.file());
    }

    @PostMapping("/confirm")
    @ResponseStatus(HttpStatus.OK)
    public ForensicReportIndexDTO confirmIndexing(@RequestBody ForensicReportIndexDTO reportDTO) {
        System.out.println("Primljen DTO: " + reportDTO);
        System.out.println("Analysts: " + reportDTO.getAnalysts());
        System.out.println("Organization: " + reportDTO.getOrganizationName());
        // ... ostala polja
        return forensicIndexingService.confirmAndSaveIndex(reportDTO);
    }
}
