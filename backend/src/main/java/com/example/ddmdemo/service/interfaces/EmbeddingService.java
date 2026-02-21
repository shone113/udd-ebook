package com.example.ddmdemo.service.interfaces;

import org.springframework.stereotype.Service;

@Service
public interface EmbeddingService {
    public float[] getVector(String text);
}
