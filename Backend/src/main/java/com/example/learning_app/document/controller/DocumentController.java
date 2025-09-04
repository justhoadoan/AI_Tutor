package com.example.learning_app.document.controller;

import com.example.learning_app.document.dto.DocumentResponse;
import com.example.learning_app.document.dto.DocumentUploadRequest;
import com.example.learning_app.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("api/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {
    private final DocumentService documentService;

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAllDocument(){
        List<DocumentResponse> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file,
                                                 @RequestPart("request") DocumentUploadRequest request){
        if(file.isEmpty()){
            return ResponseEntity.badRequest().body("File is empty");
        }
        try
        {
            documentService.addDocument(file,request);
            return ResponseEntity.accepted().body("Document received and is being processed.");
        } catch (IOException e) {
            log.error("Failed to process uploaded file.", e);
            throw new RuntimeException("Could not process the uploaded file: " + e.getMessage(), e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentResponse> updateDocument(
            @PathVariable Long id,
            @RequestBody DocumentUploadRequest request){
        try {
            DocumentResponse updatedDocument = documentService.updateDocumentMetadata(id, request);
            return ResponseEntity.ok(updatedDocument);
        } catch (Exception e) {
            log.error("Failed to update document with ID: {}", id, e);
            throw new RuntimeException("Could not update document: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DocumentResponse> deleteDocument(@PathVariable Long id)
    {
        try{
            documentService.deleteDocument(id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
