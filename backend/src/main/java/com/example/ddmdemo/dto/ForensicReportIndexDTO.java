package com.example.ddmdemo.dto;

import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import com.example.ddmdemo.model.ForensicReport;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForensicReportIndexDTO {

    private List<String> analysts;

    private String organizationName;

    private String malwareName;

    private String malwareDescription;

    private String threatClassification;

    private String sampleHash;

    private String road;

    private String city;

    private String houseNumber;

    private String fileName;

    private String serverFilename;

    private String title;

    private Integer databaseId;

    private String content;

    public ForensicReportIndexDTO(ForensicReportIndex forensicReportIndex) {
        this.analysts = forensicReportIndex.getAnalysts();
        this.organizationName = forensicReportIndex.getOrganizationName();
        this.malwareName = forensicReportIndex.getMalwareName();
        this.malwareDescription = forensicReportIndex.getMalwareDescription();
        this.threatClassification = forensicReportIndex.getThreatClassification();
        this.sampleHash = forensicReportIndex.getSampleHash();
        this.road = forensicReportIndex.getRoad();
        this.city = forensicReportIndex.getCity();
        this.houseNumber = forensicReportIndex.getHouseNumber();
        this.fileName = forensicReportIndex.getFileName();
        this.serverFilename = forensicReportIndex.getServerFilename();
        this.title = forensicReportIndex.getTitle();
        this.databaseId = forensicReportIndex.getDatabaseId();
        this.content = forensicReportIndex.getContent();
    }
}
