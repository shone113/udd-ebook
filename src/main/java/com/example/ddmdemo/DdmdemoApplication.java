package com.example.ddmdemo;

import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.elasticsearch.rest.ChunkedRestResponseBody.logger;

@SpringBootApplication
public class DdmdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DdmdemoApplication.class, args);

        System.out.println("REPORT_INDEXED SUCCESS organization=CERT-Serbia city=Belgrade analyst=Marko_Petrovic malwareName=Zeus classification=High file=report1.pdf");
        ForensicReportIndex report = new ForensicReportIndex();

//        logger.info("REPORT_INDEXED SUCCESS organization={} city={} analyst={} malware={} classification={}",
//                report.getOrganizationName(),
//                report.getAddress().getCity(),
//                report.getAnalystName() + " " + report.getAnalystSurname(),
//                report.getMalwareName(),
//                report.getThreatClassification()
//        );
    }

}
