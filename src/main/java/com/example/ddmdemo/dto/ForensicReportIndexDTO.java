package com.example.ddmdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForensicReportIndexDTO {

    private String analystName;

    private String analystSurname;

    private String organizationName;

    private String malwareName;

    private String malwareDescription;

    private String threatClassification;

    private String sampleHash;

    private String address;

    private MultipartFile forensicReport;
}
