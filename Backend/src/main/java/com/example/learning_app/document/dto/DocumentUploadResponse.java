package com.example.learning_app.document.dto;

import com.example.learning_app.document.model.ProcessingStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentUploadResponse {
    private Long id;
    private String fileName;
    private ProcessingStatus status;
    private LocalDateTime uploadDate;

    private String subjectName;
    private String semesterCode;

}
