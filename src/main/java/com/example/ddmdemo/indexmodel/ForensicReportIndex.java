package com.example.ddmdemo.indexmodel;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "forensicReport")
public class ForensicReportIndex {

    @Id
    private String id;

    @Field(type = FieldType.Text, store = true, name = "fileName")
    private String fileName;

    @Field(type = FieldType.Text, store = true, name = "analystName")
    private String analystName;

    @Field(type = FieldType.Text, store = true, name = "analystSurname")
    private String analystSurname;

    @Field(type = FieldType.Text, store = true, name = "organizationName")
    private String organizationName;

    @Field(type = FieldType.Text, store = true, name = "malwareName")
    private String malwareName;

    @Field(type = FieldType.Text, store = true, name = "malwareDescription")
    private String malwareDescription;

    @Field(type = FieldType.Keyword, store = true, name = "threatClassification")
    private String threatClassification;

    @Field(type = FieldType.Keyword, store = true, name = "sampleHash")
    private String sampleHash;

    @Field(type = FieldType.Integer, store = true, name = "databaseId")
    private Integer databaseId;

    @GeoPointField
    @Field(store = true, name = "location")
    private GeoPointField location;

    @Field(type = FieldType.Dense_Vector, dims = 384, similarity = "cosine")
    private float[] vectorizedContent;

}
