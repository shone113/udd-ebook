package com.example.ddmdemo.service.interfaces;

import com.example.ddmdemo.dto.DynamicSummaryDTO;
import com.example.ddmdemo.dto.ForensicReportIndexDTO;
import com.example.ddmdemo.indexmodel.DummyIndex;
import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ForensicSearchService {
    public List<DynamicSummaryDTO> searchByOrgAndMalware(String orgName, String malwareName);
    public List<DynamicSummaryDTO> searchByMetadata(String analyst, String hash, String classification);
    public List<DynamicSummaryDTO> fullTextSearch(String searchTerms);
    public List<DynamicSummaryDTO> knnSearch(String searchTerms);
    public List<DynamicSummaryDTO> booleanSearch(String queryStr);
}
