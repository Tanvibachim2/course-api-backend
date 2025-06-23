package com.iitb.courseapi.controller;

import com.iitb.courseapi.model.Course;
import com.iitb.courseapi.model.CourseInstance;
import com.iitb.courseapi.repository.CourseRepository;
import com.iitb.courseapi.repository.CourseInstanceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CourseInstanceController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseInstanceRepository courseInstanceRepository;

    // ✅ 1. Create a course instance
    @PostMapping("/instances")
    public ResponseEntity<?> createInstance(@RequestBody CourseInstance instance) {
        if (instance.getCourse() == null || instance.getCourse().getCourseId() == null) {
            return ResponseEntity.badRequest().body("Missing courseId in request");
        }

        String courseId = instance.getCourse().getCourseId();
        List<Course> matchedCourses = courseRepository.findByCourseId(courseId);

        if (matchedCourses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        instance.setCourse(matchedCourses.get(0));
        CourseInstance saved = courseInstanceRepository.save(instance);

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // ✅ 2. Get all instances
    @GetMapping("/instances")
    public ResponseEntity<List<CourseInstance>> getAllInstances() {
        return ResponseEntity.ok(courseInstanceRepository.findAll());
    }

    // ✅ 3. Get a specific instance
    @GetMapping("/instances/{academicYear}/{semester}/{courseId}")
    public ResponseEntity<?> getInstanceDetails(@PathVariable int academicYear,
                                                @PathVariable int semester,
                                                @PathVariable String courseId) {
        List<Course> matched = courseRepository.findByCourseId(courseId);
        if (matched.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        return courseInstanceRepository
                .findByAcademicYearAndSemesterAndCourse_Id(academicYear, semester, matched.get(0).getId())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instance not found"));
    }

    // ✅ 4. Delete a course instance
    @DeleteMapping("/instances/{academicYear}/{semester}/{courseId}")
    public ResponseEntity<?> deleteInstance(@PathVariable int academicYear,
                                            @PathVariable int semester,
                                            @PathVariable String courseId) {
        List<Course> matched = courseRepository.findByCourseId(courseId);
        if (matched.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        int deleted = courseInstanceRepository
                .deleteByAcademicYearAndSemesterAndCourse_Id(academicYear, semester, matched.get(0).getId());

        if (deleted == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instance not found");
        }

        return ResponseEntity.ok("Course instance deleted");
    }
}
