package com.example.ddmdemo.controller;

import com.example.ddmdemo.dto.DummyDocumentFileDTO;
import com.example.ddmdemo.dto.DummyDocumentFileResponseDTO;
import com.example.ddmdemo.dto.ForensicReportIndexDTO;
import com.example.ddmdemo.service.interfaces.ForensicIndexingService;
import com.example.ddmdemo.service.interfaces.IndexingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
}
