package com.example.ddmdemo.controller;

import com.example.ddmdemo.dto.DynamicSummaryDTO;
import com.example.ddmdemo.dto.ForensicReportIndexDTO;
import com.example.ddmdemo.dto.SearchQueryDTO;
import com.example.ddmdemo.indexmodel.DummyIndex;
import com.example.ddmdemo.service.interfaces.ForensicSearchService;
import com.example.ddmdemo.service.interfaces.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final ForensicSearchService searchService;
//
//    @PostMapping("/simple")
//    public Page<DummyIndex> simpleSearch(@RequestParam Boolean isKnn,
//                                         @RequestBody SearchQueryDTO simpleSearchQuery,
//                                         Pageable pageable) {
//        return searchService.simpleSearch(simpleSearchQuery.keywords(), pageable, isKnn);
//    }
//
//    @PostMapping("/advanced")
//    public Page<DummyIndex> advancedSearch(@RequestBody SearchQueryDTO advancedSearchQuery,
//                                           Pageable pageable) {
//        return searchService.advancedSearch(advancedSearchQuery.keywords(), pageable);
//    }

    @GetMapping("/full-text")
    public List<DynamicSummaryDTO> fullTextSearch(@RequestParam String query) {
        return searchService.fullTextSearch(query);
    }

    @GetMapping("/knn")
    public List<DynamicSummaryDTO> semanticSearch(@RequestParam String query) {
        return searchService.knnSearch(query);
    }

    @GetMapping("/boolean")
    public List<DynamicSummaryDTO> search(@RequestParam String query) {
        // query može biti: "digitalna forenzika" AND NOT (viber OR whatsapp)
        return searchService.booleanSearch(query);
    }
}
