package com.example.ddmdemo.dto;

import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import com.example.ddmdemo.model.ForensicReport;
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

    public ForensicReportIndexDTO(ForensicReportIndex forensicReportIndex) {
        this.analystName = forensicReportIndex.getAnalystName();
        this.analystSurname = forensicReportIndex.getAnalystSurname();
        this.organizationName = forensicReportIndex.getOrganizationName();
        this.malwareName = forensicReportIndex.getMalwareName();
        this.malwareDescription = forensicReportIndex.getMalwareDescription();
        this.threatClassification = forensicReportIndex.getThreatClassification();
        this.sampleHash = forensicReportIndex.getSampleHash();
    }
}
