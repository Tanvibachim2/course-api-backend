package com.iitb.courseapi.repository;

import com.iitb.courseapi.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCourseId(String courseId);
    boolean existsByCourseId(String courseId);
    void deleteByCourseId(String courseId);
}

