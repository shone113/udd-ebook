package com.example.ddmdemo.respository;

import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ForensicReportRepository extends ElasticsearchRepository<ForensicReportIndex, String> {

    @Query("""
    {
      "bool": {
        "must": [
          { "match": { "analystName": "?0" } },
          { "match": { "analystSurname": "?1" } },
          { "term":  { "hashValue": "?2" } },
          { "term":  { "threatClassification": "?3" } }
        ]
      }
    }
    """)
    List<ForensicReportIndex> searchByForensicAnalystAndThreat(
            String analystName,
            String analystSurname,
            String hashValue,
            String threatClassification
    );

    @Query("""
    {
      "bool": {
        "must": [
          { "match": { "organizationName": "?0" } },
          { "match": { "malwareName": "?1" } }
        ]
      }
    }
    """)
    List<ForensicReportIndex> searchByOrganizationAndMalware(
            String organizationName,
            String malwareName
    );

}
