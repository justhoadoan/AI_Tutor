package com.example.learning_app.document.repository;

import com.example.learning_app.document.model.Document;
import com.example.learning_app.document.model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Semester> findByYearAndTerm(Integer year, Integer term);
}
