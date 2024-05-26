package org.benaya.learn.polyglotwithvenv.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.benaya.learn.polyglotwithvenv.service.EmbeddingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/embeddings")
public class EmbeddingsController {
    private final EmbeddingService embeddingService;

    @PostMapping("/multiple_sentences")
    public List<List<Double>> getEmbeddingsForSentences(@RequestBody List<String> sentences) throws JsonProcessingException {
        return embeddingService.getEmbeddingsForSentences(sentences);
    }
}
