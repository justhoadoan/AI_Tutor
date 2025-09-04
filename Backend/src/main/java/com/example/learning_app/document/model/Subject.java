package com.example.learning_app.document.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subject")
public class Subject {
    @Id
    @Column(name = "subject_code")

    private String subjectCode;

    @Column(name = "course_name")
    private String courseName;


}
