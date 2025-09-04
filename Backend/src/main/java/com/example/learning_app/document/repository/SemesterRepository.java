package com.example.learning_app.document.repository;

import com.example.learning_app.document.model.Semester;
import com.example.learning_app.document.model.SemesterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {



    Optional<Semester> findById(SemesterId semesterIdToFind);
}
