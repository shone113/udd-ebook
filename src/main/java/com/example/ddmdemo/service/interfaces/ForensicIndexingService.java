package com.example.ddmdemo.service.interfaces;

import com.example.ddmdemo.dto.ForensicReportIndexDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ForensicIndexingService {

    ForensicReportIndexDTO indexDocument(MultipartFile documentFile);
}
