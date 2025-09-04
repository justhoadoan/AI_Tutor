package com.example.learning_app.document.mapper;

import com.example.learning_app.document.dto.DocumentResponse;
import com.example.learning_app.document.model.Document;
import com.example.learning_app.document.model.Semester;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(source = "subject.courseName", target = "subjectName")
    @Mapping(source = "semester", target = "semesterCode", qualifiedByName = "semesterToCode")
    DocumentResponse toDocumentResponse(Document document);

    List<DocumentResponse> toDocumentResponseList(List<Document> documents);

    @Named("semesterToCode")
    default String semesterToCode(Semester semester) {
        if (semester == null || semester.getId() == null ||
                semester.getId().getYear() == null || semester.getId().getTerm() == null) {
            return "";
        }
        return semester.getId().getYear().toString() + semester.getId().getTerm().toString();
    }


}
