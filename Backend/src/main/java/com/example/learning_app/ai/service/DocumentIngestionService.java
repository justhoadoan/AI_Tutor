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

            // Test search - SỬA LẠI PHẦN NÀY
            log.info("[INGESTION - Step 5.1] Testing similarity search...");
            SearchRequest testSearchRequest = SearchRequest.builder()
                    .query("quiz and SLMs")
                    .topK(4)
                    .build();

            List<org.springframework.ai.document.Document> testResults = vectorStore.similaritySearch(testSearchRequest);
            log.info("[INGESTION - Step 5.1] Test search returned {} results", testResults.size());

            // Test với filter
            if (documentEntity.getId() != null) {
                String testFilter = "documentId == '" + documentEntity.getId() + "'";
                SearchRequest filteredSearchRequest = SearchRequest.builder()
                        .query("quiz and SLMs")
                        .topK(4)
                        .filterExpression(testFilter)
                        .build();

                List<org.springframework.ai.document.Document> filteredResults = vectorStore.similaritySearch(filteredSearchRequest);
                log.info("[INGESTION - Step 5.2] Filtered search returned {} results with filter: {}",
                        filteredResults.size(), testFilter);
            }

            log.info("[INGESTION - Step 5 OK] Chunks successfully added to VectorStore.");
            log.info("[INGESTION SUCCESS] Successfully processed document ID: {}", documentEntity.getId());

        } catch (Exception e) {
            log.error("[INGESTION FAILED] CRITICAL ERROR during ingestion for document ID: {}. Reason: {}",
                    documentEntity.getId(), e.getMessage(), e);
        }
    }

}