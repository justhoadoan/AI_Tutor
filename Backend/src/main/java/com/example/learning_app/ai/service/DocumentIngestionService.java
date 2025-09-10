package com.example.learning_app.ai.service;

import com.example.learning_app.document.model.Document;
 // Cần import để cập nhật status
import com.example.learning_app.document.repository.DocumentRepository;
import com.example.learning_app.document.service.DocumentIngestTrigger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
        log.info("[INGESTION START] Starting ingestion for document ID: {}, File: '{}'",
                documentEntity.getId(), documentEntity.getFileName());

        try {

            log.info("[INGESTION - Step 1/6] Reading file from path: {}", documentEntity.getFilePath());
            FileSystemResource fileResource = new FileSystemResource(documentEntity.getFilePath());
            if (!fileResource.exists()) {
                throw new IOException("File not found at path: " + documentEntity.getFilePath());
            }


            log.info("[INGESTION - Step 2/6] Extracting text with Tika...");
            TikaDocumentReader documentReader = new TikaDocumentReader(fileResource);
            List<org.springframework.ai.document.Document> rawDocs = documentReader.get();
            log.info("[INGESTION - Step 2 OK] Extracted {} parts/pages.", rawDocs.size());


            log.info("[INGESTION - Step 3/6] Splitting text into chunks...");
            TextSplitter textSplitter = new TokenTextSplitter();
            List<org.springframework.ai.document.Document> chunks = textSplitter.apply(rawDocs);
            log.info("[INGESTION - Step 3 OK] Split into {} chunks.", chunks.size());


            log.info("[INGESTION - Step 4/6] Enriching chunks with metadata...");
            chunks.forEach(chunk -> {
                chunk.getMetadata().put("documentId", documentEntity.getId().toString());
                chunk.getMetadata().put("fileName", documentEntity.getFileName());
                chunk.getMetadata().put("subjectId", documentEntity.getSubject().getSubjectCode());
                chunk.getMetadata().put("subjectName", documentEntity.getSubject().getCourseName());
                chunk.getMetadata().put("semesterCode",
                        documentEntity.getSemester().getId().getYear().toString() +
                                documentEntity.getSemester().getId().getTerm().toString());
            });
            log.info("[INGESTION - Step 4 OK] Metadata enriched for all chunks.");

            log.info("[INGESTION - Step 5/6] Adding chunks to VectorStore (calling AI model)...");

            vectorStore.add(chunks);
            List<org.springframework.ai.document.Document> results = this.vectorStore.similaritySearch(String.valueOf(SearchRequest.builder().query("phân tích tại sao SLMs là tương lai của Agentic AI").topK(4).build()));
           // log.info("TEST:{}", results);
            log.info("[INGESTION - Step 5 OK] Chunks successfully added to VectorStore.");
            log.info("[INGESTION - Step 6/6] Updating document s tatus to READY.");

            log.info("[INGESTION SUCCESS] Successfully processed document ID: {}", documentEntity.getId());

        } catch (Exception e) {

            log.error("[INGESTION FAILED] CRITICAL ERROR during ingestion for document ID: {}. Reason: {}",
                    documentEntity.getId(), e.getMessage(), e);

            log.warn("[INGESTION] Document ID {} status has been set to ERROR.", documentEntity.getId());

        }
    }
}