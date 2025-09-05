package com.example.learning_app.ai.service;

import com.example.learning_app.document.model.Document;
import com.example.learning_app.document.repository.DocumentRepository;
import com.example.learning_app.document.service.DocumentIngestTrigger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentIngestionService implements DocumentIngestTrigger {
    private final VectorStore vectorStore;
    private final DocumentRepository documentRepository;

    @Override
    @Async
    @Transactional
    public void triggerIngestion(Document documentEntity) {
        try{
            FileSystemResource fileResource = new FileSystemResource(documentEntity.getFilePath());

            TikaDocumentReader  documentReader = new TikaDocumentReader(fileResource);
            List<org.springframework.ai.document.Document> rawDocs = documentReader.get();

            TextSplitter textSplitter = new TokenTextSplitter();
            List<org.springframework.ai.document.Document> chunks = textSplitter.apply(rawDocs);

            chunks.forEach(chunk -> {
                chunk.getMetadata().put("documentId", documentEntity.getId().toString());
                chunk.getMetadata().put("fileName", documentEntity.getFileName());
                chunk.getMetadata().put("subjectId", documentEntity.getSubject().getSubjectCode());
                chunk.getMetadata().put("subjectName", documentEntity.getSubject().getCourseName());
                chunk.getMetadata().put("semesterCode",
                        documentEntity.getSemester().getId().getYear().toString() +
                                documentEntity.getSemester().getId().getTerm().toString());
            });

            vectorStore.add(chunks);

        }
        catch (Exception e){
            log.error("[INGESTION-FAILED] An error occurred while processing document ID: {}. Reason: {}",
                    documentEntity.getId(), e.getMessage(), e);
        }
    }
}
