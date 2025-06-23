package com.iitb.courseapi.repository;

import com.iitb.courseapi.model.CourseInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseInstanceRepository extends JpaRepository<CourseInstance, Long> {
    List<CourseInstance> findByAcademicYearAndSemester(int academicYear, int semester);

    Optional<CourseInstance> findByAcademicYearAndSemesterAndCourse_Id(int academicYear, int semester, Long courseId);

    int deleteByAcademicYearAndSemesterAndCourse_Id(int academicYear, int semester, Long courseId);
}
