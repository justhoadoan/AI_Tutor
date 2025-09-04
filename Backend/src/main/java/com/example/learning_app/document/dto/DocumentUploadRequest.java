package com.example.learning_app.document.dto;

import lombok.Data;

@Data
public class DocumentUploadRequest {
    private String subjectCode;
    private String subjectName;
    private String semesterCode;
}
