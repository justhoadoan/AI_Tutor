package com.example.learning_app.ai.controller;


import com.example.learning_app.ai.dto.ChatRequest;
import com.example.learning_app.ai.dto.ChatResponse;
import com.example.learning_app.ai.service.RagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {
    private final RagService ragService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest chatRequest) {
        String aiMessage = ragService.getChatResponseWithDocumentId(chatRequest);
        return ResponseEntity.ok(new ChatResponse(aiMessage));
    }
    @PostMapping("/chat/all")
    public ResponseEntity<ChatResponse> chatWithDocument(@RequestBody ChatRequest request) {
        log.info("POST /api/ai/chat - Received RAG chat request. Context: docId={}, subjectId={}",
                request.getDocumentId(), request.getSubjectId());
        String aiMessage = ragService.getChatResponse(request);

        return ResponseEntity.ok(new ChatResponse(aiMessage));
    }

}
