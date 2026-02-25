package com.example.ddmdemo.indexmodel;

import com.example.ddmdemo.dto.ForensicReportIndexDTO;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.elasticsearch.annotations.Similarity;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "forensic_report_index")
@Setting(settingPath = "configuration/serbian-analyzer-config.json")
public class ForensicReportIndex {

    @Id
    private String id;

    @Field(type = FieldType.Text, store = true, name = "fileName")
    private String fileName;

    @Field(type = FieldType.Text, store = true, name = "server_filename", index = false)
    private String serverFilename;

    @Field(type = FieldType.Text, store = true, name = "analysts", analyzer = "entity_analyzer")
    private List<String> analysts;

    @Field(type = FieldType.Text, store = true, name = "organizationName", analyzer = "entity_analyzer")
    private String organizationName;

    @Field(type = FieldType.Text, store = true, name = "malwareName")
    private String malwareName;

    @Field(type = FieldType.Text, store = true, name = "title")
    private String title;

    @Field(type = FieldType.Text,
            store = true,
            name = "malwareDescription",
            analyzer = "serbian_analyzer",
            searchAnalyzer = "serbian_analyzer")
    private String malwareDescription;

    @Field(type = FieldType.Keyword, store = true, name = "threatClassification")
    private String threatClassification;

    @Field(type = FieldType.Keyword, store = true, name = "sampleHash")
    private String sampleHash;

    @Field(type = FieldType.Integer, store = true, name = "databaseId")
    private Integer databaseId;

    @GeoPointField
    @Field(store = true, name = "location")
    private GeoPoint location;

    @Field(type = FieldType.Dense_Vector, dims = 384, index = true, similarity = "l2_norm")
    private float[] vectorizedContent;

    @Field(type = FieldType.Text,
            name = "content",
            analyzer = "serbian_analyzer",
            searchAnalyzer = "serbian_analyzer")
    private String content;

    @Field(type = FieldType.Text, store = true, name = "city", analyzer = "entity_analyzer")
    private String city;

    @Field(type = FieldType.Text, store = true, name = "road", analyzer = "entity_analyzer")
    private String road;

    @Field(type = FieldType.Keyword, store = true, name = "houseNumber")
    private String houseNumber;

    public ForensicReportIndex fromDtoToIndex(ForensicReportIndexDTO dto) {
        ForensicReportIndex index = new ForensicReportIndex();

        // Setovanje osnovnih polja
        index.setId(UUID.randomUUID().toString()); // Generisanje ID-a ako nije prisutan u DTO
        index.setFileName(dto.getFileName());
        index.setServerFilename(dto.getServerFilename());
        index.setAnalysts(dto.getAnalysts());
        index.setOrganizationName(dto.getOrganizationName());
        index.setMalwareName(dto.getMalwareName());
        index.setTitle(dto.getTitle());
        index.setMalwareDescription(dto.getMalwareDescription());
        index.setThreatClassification(dto.getThreatClassification());
        index.setSampleHash(dto.getSampleHash());
        index.setDatabaseId(dto.getDatabaseId());
        index.setContent(dto.getContent());
        index.setCity(dto.getCity());
        index.setRoad(dto.getRoad());
        index.setHouseNumber(dto.getHouseNumber());

        return index;
    }

}
