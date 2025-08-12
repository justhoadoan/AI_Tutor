package com.example.learning_app.document;


import com.example.learning_app.document.mapper.DocumentMapper;
import com.example.learning_app.document.model.Document;
import com.example.learning_app.document.repository.DocumentRepository;
import com.example.learning_app.document.repository.SemesterRepository;
import com.example.learning_app.document.repository.SubjectRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    private final DocumentMapper documentMapper;


}
