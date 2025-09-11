package com.example.learning_app.ai.service;

import com.example.learning_app.ai.dto.ChatRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {


    private final VectorStore vectorStore;
    private final ChatClient.Builder chatClientBuilder;


    public String getChatResponse(ChatRequest request) {

        VectorStoreDocumentRetriever retriever = VectorStoreDocumentRetriever
                .builder()
                .vectorStore(vectorStore)
                .topK(4)
                .similarityThreshold(0.5).
                build();

        Advisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(retriever)
                .build();

        ChatClient chatClient = chatClientBuilder.build();

        log.info("Executing chat call with RAG advisor (no filter)...");


        String response = chatClient.prompt()
                .user(request.getMessage())
                .advisors(ragAdvisor)
                .call()
                .content();

        log.info("Received response from AI provider.");
        return response;
    }

    public String getChatResponseWithDocumentId(ChatRequest request) {
        return "";
    }
    private String buildFilterExpression(Long documentId, String subjectId) {
        List<String> conditions = new ArrayList<>();

        if (documentId != null) {
            conditions.add("documentId == '" + documentId + "'");
        }
        if (subjectId != null && !"null".equals(subjectId)) {
            conditions.add("subjectId == '" + subjectId + "'");
        }

        if (conditions.isEmpty()) {
            return "";
        }


        return String.join(" && ", conditions);
    }

}