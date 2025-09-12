package com.example.learning_app.ai.service;

import com.example.learning_app.ai.dto.ChatRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
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
        return cleanAndFormatResponse(response) ;
    }

    public String getChatResponseWithDocumentId(ChatRequest request) {
        log.info("Processing RAG chat request: documentId={}, subjectId={}, query='{}'",
                request.getDocumentId(), request.getSubjectId(), request.getMessage());

        String filterExpression = buildFilterExpression(request.getDocumentId(), String.valueOf(request.getSubjectId()));
        log.info("Using filter expression: {}", filterExpression);

        try {
            SearchRequest debugSearch = SearchRequest.builder()
                    .query(request.getMessage())
                    .topK(4)
                    .similarityThreshold(0.5)
                    .build();

            List<org.springframework.ai.document.Document> debugResults = vectorStore.similaritySearch(debugSearch);

            if (!debugResults.isEmpty()) {
                log.info("=== METADATA DEBUG ===");
                for (int i = 0; i < Math.min(3, debugResults.size()); i++) {
                    Map<String, Object> metadata = debugResults.get(i).getMetadata();
                    log.info("Document {}: Full metadata = {}", i + 1, metadata);
                    log.info("  - documentId value: '{}' (type: {})",
                            metadata.get("documentId"),
                            metadata.get("documentId") != null ? metadata.get("documentId").getClass().getSimpleName() : "null");
                    log.info("  - subjectId value: '{}' (type: {})",
                            metadata.get("subjectId"),
                            metadata.get("subjectId") != null ? metadata.get("subjectId").getClass().getSimpleName() : "null");
                }
            }



        } catch (Exception e) {
            log.error("Error in metadata debug: {}", e.getMessage());
        }

        VectorStoreDocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .topK(4)
                .similarityThreshold(0.5)
                .build();

        Advisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(retriever)
                .build();

        ChatClient chatClient = chatClientBuilder.build();

        String response = chatClient.prompt()
                .user(request.getMessage())
                .advisors(ragAdvisor)
                .advisors(a -> {
                    if (!filterExpression.isEmpty()) {
                        a.param(VectorStoreDocumentRetriever.FILTER_EXPRESSION, filterExpression);
                    }
                })
                .call()
                .content();

        return response;
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
    private String cleanAndFormatResponse(String rawAiResponse) {
        String cleanedResponse = rawAiResponse;

        cleanedResponse = cleanedResponse.replaceAll("(?s)<think>.*</think>", "").trim();

        return cleanedResponse;
    }

}