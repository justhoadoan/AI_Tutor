package com.example.learning_app.ai.service;

import com.example.learning_app.ai.dto.ChatRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.observation.conventions.AiProvider;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;



import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {


    private final VectorStore vectorStore;
    private final ChatClient.Builder chatClientBuilder;

    public String getChatResponse(ChatRequest request) {

        log.info("Processing RAG chat request using RetrievalAugmentationAdvisor for context: documentId={}, subjectId={}",
                request.getDocumentId(), request.getSubjectId());

        VectorStoreDocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .topK(4)
                .similarityThreshold(0.75)
                .build();

        Advisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(retriever).build();

        ChatClient chatClient = chatClientBuilder.build();

        log.info("Executing chat call with RAG advisor...");

        String response = chatClient.prompt()
                .user(request.getMessage())
                .advisors(ragAdvisor)
                .advisors(a -> a.param(
                        VectorStoreDocumentRetriever.FILTER_EXPRESSION,
                        buildFilterExpression(request.getDocumentId(), String.valueOf(request.getSubjectId()))
                ))
                .call()
                .content();

        log.info("Received response from AI provider via RAG advisor.");
        return response;
    }



    private String buildFilterExpression(Long documentId, String subjectId) {
        if (documentId != null) {
            return "documentId == '" + documentId + "'";
        }
        if (subjectId != null) {
            return "subjectId == '" + subjectId + "'";
        }
        return "";
    }




}