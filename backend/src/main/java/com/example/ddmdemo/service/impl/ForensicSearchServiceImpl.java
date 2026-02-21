package com.example.ddmdemo.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.KnnQuery;
import com.example.ddmdemo.dto.DynamicSummaryDTO;
import com.example.ddmdemo.dto.ForensicReportIndexDTO;
import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import com.example.ddmdemo.service.interfaces.EmbeddingService;
import com.example.ddmdemo.service.interfaces.ForensicSearchService;
import com.example.ddmdemo.util.BooleanQueryParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.stereotype.Service;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ForensicSearchServiceImpl implements ForensicSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final EmbeddingService embeddingService;
    private final BooleanQueryParser queryParser;

    public List<DynamicSummaryDTO> fullTextSearch(String searchTerms) {
        // 1. Definišemo parametre isticanja (tagovi i veličina isečka)
        HighlightParameters parameters = HighlightParameters.builder()
                .withPreTags("<strong>")
                .withPostTags("</strong>")
                .withFragmentSize(150)
                .withNumberOfFragments(1)
                .build();

        // 2. Definišemo na koje polje se highlight odnosi
        HighlightField field = new HighlightField("malwareDescription");

        // 3. Kreiramo Highlight objekat
        Highlight highlight = new Highlight(parameters, List.of(field));

        // 4. Gradimo NativeQuery sa HighlightQuery omotačem
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .match(m -> m
                                .field("malwareDescription")
                                .query(searchTerms)
                        )
                )
                .withHighlightQuery(new HighlightQuery(highlight, ForensicReportIndex.class)) // REŠENJE ZA ERROR
                .build();

        SearchHits<ForensicReportIndex> hits = elasticsearchOperations.search(query, ForensicReportIndex.class);

        return mapHitsToDynamicSummary(hits);
    }

    public List<DynamicSummaryDTO> knnSearch(String searchTerms) {
        // 1. Dobijanje vektora za uneti tekst
        float[] queryVector = embeddingService.getVector(searchTerms);

        // 2. Definisanje KNN upita
        KnnQuery knnQuery = new KnnQuery.Builder()
                .field("vectorizedContent")
                .queryVector(Arrays.asList(toObjectArray(queryVector)))
                .k(5)
                .numCandidates(100)
                .build();

        NativeQuery query = NativeQuery.builder()
                .withKnnQuery(knnQuery)
                .build();

        query.setSearchType(null);

        SearchHits<ForensicReportIndex> hits = elasticsearchOperations.search(query, ForensicReportIndex.class);

        return mapHitsToDynamicSummary(hits);
    }

    // Pomoćna metoda jer klijent nekad traži Float[] umesto float[]
    private Float[] toObjectArray(float[] primary) {
        Float[] result = new Float[primary.length];
        for (int i = 0; i < primary.length; i++) result[i] = primary[i];
        return result;
    }

    private List<DynamicSummaryDTO> mapHitsToDynamicSummary(SearchHits<ForensicReportIndex> hits) {
        return hits.stream()
                .map(hit -> {
                    ForensicReportIndex doc = hit.getContent();

                    // Uzimamo highlight isečak ako postoji
                    String summary = hit.getHighlightField("malwareDescription")
                            .stream()
                            .findFirst()
                            .orElse(truncate(doc.getMalwareDescription(), 150));

                    // Mapiramo na DynamicSummaryDTO (fileName kao title, isečak kao summary)
                    return new DynamicSummaryDTO(doc.getFileName(), summary);
                })
                .toList();
    }

    @Override
    public List<DynamicSummaryDTO> booleanSearch(String queryStr) {
        // 1. Dobiješ postfiks (npr. [napad, "sql injection", AND])
        List<String> postfix = queryParser.parse(queryStr);
        System.out.println("Postfixna notacija: " + postfix);

        // 2. Gradiš ES upit pomoću stack-a (metoda buildComplexQuery koju već imaš)
        Query esQuery = queryParser.buildComplexQuery(postfix);
        System.out.println("es query: " + esQuery);

        // 3. Izvršiš pretragu
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(esQuery)
                .build();

        SearchHits<ForensicReportIndex> hits = elasticsearchOperations.search(nativeQuery, ForensicReportIndex.class);

        System.out.println("Ovo su hits: " + hits);
        return mapHitsToDynamicSummary(hits);
    }

    private String truncate(String text, int length) {
        if (text == null || text.length() <= length) {
            return text;
        }
        return text.substring(0, length) + "...";
    }
}
