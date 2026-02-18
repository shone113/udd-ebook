package com.example.ddmdemo.service.interfaces;

import com.example.ddmdemo.indexmodel.DummyIndex;
import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ForensicSearchService {
    Page<ForensicReportIndex> simpleSearch(List<String> keywords, Pageable pageable, boolean isKNN);

    Page<ForensicReportIndex> advancedSearch(List<String> expression, Pageable pageable);
}
