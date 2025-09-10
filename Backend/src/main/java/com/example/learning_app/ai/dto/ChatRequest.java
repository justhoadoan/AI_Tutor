package com.example.learning_app.ai.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private String message;
    private Long subjectId;
    private Long documentId;

}
