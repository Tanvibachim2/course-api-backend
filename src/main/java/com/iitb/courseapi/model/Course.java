package com.iitb.courseapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courseId;
    private String title;
    private String description;

    @ManyToMany
    @JsonIgnoreProperties("prerequisites") // ✅ prevents infinite loop
    private List<Course> prerequisites;
}
