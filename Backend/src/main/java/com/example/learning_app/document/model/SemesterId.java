package com.example.learning_app.document.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterId implements Serializable {

    private Long year;
    private Long term;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemesterId semesterId = (SemesterId) o;
        return year.equals(semesterId.year) && term.equals(semesterId.term);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, term);
    }
}
