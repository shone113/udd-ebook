package com.example.ddmdemo.respository;

import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import com.example.ddmdemo.model.DummyTable;
import com.example.ddmdemo.model.ForensicReport;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForensicReportRepository extends JpaRepository<ForensicReport, Integer> {
}
