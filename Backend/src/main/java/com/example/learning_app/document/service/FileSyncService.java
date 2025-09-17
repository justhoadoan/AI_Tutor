package com.example.learning_app.document.service;

import com.example.learning_app.document.model.Document;
import com.example.learning_app.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileSyncService implements ApplicationRunner {
    private final DocumentRepository documentRepository;
    private final DocumentService documentService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Document> allDocumentInDb = documentRepository.findAll();

        for(Document document : allDocumentInDb) {
            if(document.getFilePath() == null && document.getFilePath().isBlank())
            {
                continue;
            }
            boolean fileExists = Files.exists(Paths.get(document.getFilePath()));
            if(!fileExists) {
                try{
                    documentService.deleteDocument(document.getId());
                }
                catch(Exception e) {
                    log.error("Error while deleting document", e);
                }
            }
        }
    }
}
