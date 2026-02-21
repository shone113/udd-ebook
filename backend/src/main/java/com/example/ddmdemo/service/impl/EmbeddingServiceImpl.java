package com.example.ddmdemo.service.impl;

import com.example.ddmdemo.service.interfaces.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingService {
    private final RestTemplate restTemplate = new RestTemplate();

    // Besplatan model koji daje 384 dimenzije
    private final String API_URL = "https://router.huggingface.co/hf-inference/models/sentence-transformers/all-MiniLM-L6-v2/pipeline/feature-extraction";

    // Tvoj token (registruj se na Hugging Face i uzmi besplatan Write token)
    @Value("${hf.api.token}")
    private String authToken;

    private final String AUTH_TOKEN = "Bearer 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";

    public float[] getVector(String text) {
        if (text == null || text.isBlank()) return createSafeFallbackVector();

        // Skrati tekst na razumnu meru (npr. 500 karaktera) jer HF ima limite
        String cleanText = text.length() > 500 ? text.substring(0, 500) : text;

        try {
            RestTemplate rt = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("Authorization", AUTH_TOKEN);

            // Najjednostavniji mogući body
            Map<String, Object> body = Map.of("inputs", cleanText);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // Pozivamo kao Object.class da vidimo šta nam stvarno vraća
            ResponseEntity<Object> responseEntity = rt.exchange(
                    API_URL,
                    HttpMethod.POST,
                    entity,
                    Object.class
            );

            Object response = responseEntity.getBody();

            // Ako vrati listu (što je standard za ovaj model)
            if (response instanceof List<?> list) {
                // Ako je matrica (List of Lists)
                if (!list.isEmpty() && list.get(0) instanceof List<?> firstRow) {
                    float[] vector = new float[firstRow.size()];
                    for (int i = 0; i < firstRow.size(); i++) {
                        vector[i] = ((Number) firstRow.get(i)).floatValue();
                    }
                    return vector;
                }
                // Ako je običan niz (List of Numbers)
                else if (!list.isEmpty() && list.get(0) instanceof Number) {
                    float[] vector = new float[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        vector[i] = ((Number) list.get(i)).floatValue();
                    }
                    return vector;
                }
            }
        } catch (Exception e) {
            // Ovde ćeš sada u konzoli videti detaljnije šta nije valjalo
            System.err.println("HF API ERROR DETAILED: " + e.getMessage());
        }
        return createSafeFallbackVector();
    }

    private float[] createSafeFallbackVector() {
        float[] fallback = new float[384];
        // Stavi bilo šta osim nule na prvi indeks
        fallback[0] = 0.5f;
        return fallback;
    }
}
