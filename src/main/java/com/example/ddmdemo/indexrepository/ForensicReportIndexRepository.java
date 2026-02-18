package com.example.ddmdemo.indexrepository;

import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ForensicReportIndexRepository extends ElasticsearchRepository<ForensicReportIndex, String> {

}
