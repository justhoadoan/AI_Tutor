package com.example.learning_app.document.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentResponse {

    private Long id;
    private String fileName;
    private LocalDateTime uploadDate;

    private String subjectName;
    private String semesterCode;

}
