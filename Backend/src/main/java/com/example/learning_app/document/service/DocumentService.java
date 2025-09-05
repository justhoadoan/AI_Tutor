package com.example.learning_app.document.service;


import com.example.learning_app.document.dto.DocumentResponse;
import com.example.learning_app.document.dto.DocumentUploadRequest;
import com.example.learning_app.document.mapper.DocumentMapper;
import com.example.learning_app.document.model.Document;
import com.example.learning_app.document.model.Semester;
import com.example.learning_app.document.model.SemesterId;
import com.example.learning_app.document.model.Subject;
import com.example.learning_app.document.repository.DocumentRepository;
import com.example.learning_app.document.repository.SemesterRepository;
import com.example.learning_app.document.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    private final DocumentMapper documentMapper;
    private final StorageService storageService;
    private final DocumentIngestTrigger ingestTrigger;

    @Value("${application.storage.local-path}")
    private String storagePath;

    public List<DocumentResponse> getAllDocuments() {

        List<Document> documents = documentRepository.findAll();
        return documentMapper.toDocumentResponseList(documents);
    }

   @Transactional
    public void addDocument(MultipartFile multipartFile, DocumentUploadRequest request) throws IOException {

        String semesterCode = request.getSemesterCode();
        if (semesterCode == null || semesterCode.length() < 5)
        {
            throw new IllegalArgumentException("Invalid  semester code");
        }

        Integer year = Integer.parseInt(semesterCode.substring(0, 4));
        Integer term = Integer.parseInt(semesterCode.substring(4));

        Subject subject = findOrCreateSubject(request.getSubjectCode(), request.getSubjectName());
        Semester semester = findOrCreateSemester(year.longValue(), term.longValue());

        String filePath = storageService.save(multipartFile,semesterCode, subject.getCourseName());
        log.info("Physical file saved at: {}", filePath);

        Document document = new Document();
        document.setFileName(multipartFile.getOriginalFilename());
        document.setFilePath(filePath);
        document.setSubject(subject);
        document.setSemester(semester);
        Document savedDocument = documentRepository.save(document);

        ingestTrigger.triggerIngestion(savedDocument);
        log.info("Document metadata saved with ID: {}", savedDocument.getId());

    }

    @Transactional
    public DocumentResponse  updateDocumentMetadata(Long documentId, DocumentUploadRequest request) throws IOException {

        Document document = documentRepository.findById(documentId).
        orElseThrow(()->new RuntimeException("Document not found"));

        String oldFilePath = document.getFilePath();

        Integer newYear = Integer.parseInt(request.getSemesterCode().substring(0, 4));
        Integer newTerm = Integer.parseInt(request.getSemesterCode().substring(4));
        Subject newSubject = findOrCreateSubject(request.getSemesterCode(), request.getSubjectName());
        Semester newSemester = findOrCreateSemester(newYear.longValue(), newTerm.longValue());

        String newFilePath = storageService.move(oldFilePath, newSubject.getCourseName(), newSubject.getCourseName());

        document.setFilePath(newFilePath);
        document.setSubject(newSubject);
        document.setSemester(newSemester);
        Document updatedDocument = documentRepository.save(document);

        return documentMapper.toDocumentResponse(updatedDocument);
    }

    @Transactional
    public void deleteDocument(Long id) throws IOException{

        Optional<Document> document = documentRepository.findById(id);
        if (document.isPresent()) {
            documentRepository.deleteById(id);
            storageService.delete(document.get().getFilePath());
        }
    }

    private Subject findOrCreateSubject(String code, String subjectName) {

        if(code == null || code.isBlank() || subjectName == null || subjectName.isBlank())
       {
           throw new IllegalArgumentException("Invalid code");
       }

        Optional<Subject> existingSubjectOpt = subjectRepository.findById(code);

        if(existingSubjectOpt.isPresent()) {
         return existingSubjectOpt.get();
        }
        else
        {
                log.info("Subject not found");
                Subject subject = new Subject(code, subjectName);
                return subjectRepository.save(subject);
        }
    }

    private Semester findOrCreateSemester(Long year, Long term) {

        SemesterId semesterIdToFind = new SemesterId(year, term);

        return semesterRepository.findById(semesterIdToFind)
                .orElseGet(() -> {
                    log.info("Semester not found for {}. Creating a new one.", semesterIdToFind);
                    Semester newSemester = new Semester(semesterIdToFind);
                    return semesterRepository.save(newSemester);
                });
    }
}
