package com.example.ddmdemo.service.interfaces;

import com.example.ddmdemo.dto.ForensicReportIndexDTO;
import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ForensicIndexingService {

    public ForensicReportIndexDTO indexDocument(MultipartFile documentFile);
    public ForensicReportIndexDTO confirmAndSaveIndex(ForensicReportIndexDTO index);
}
