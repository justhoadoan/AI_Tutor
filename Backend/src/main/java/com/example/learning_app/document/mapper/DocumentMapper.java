package com.example.learning_app.document.mapper;

import com.example.learning_app.document.dto.DocumentResponse;
import com.example.learning_app.document.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    @Mapping(source = "subject.subjectName", target = "subjectName")
    @Mapping(source = "semester", target = "semesterCode")
    DocumentResponse toDocumentResponse(Document document);
    List<DocumentResponse> toDocumentResponseList(List<Document> documents);


}
