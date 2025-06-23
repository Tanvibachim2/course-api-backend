package com.iitb.courseapi.controller;

import com.iitb.courseapi.model.Course;
import com.iitb.courseapi.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.iitb.courseapi.model.CourseInstance;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import com.iitb.courseapi.repository.CourseInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@RestController
@RequestMapping("/api")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;
    private CourseInstanceRepository courseInstanceRepository;

    // 1. Create a course
    @PostMapping("/courses")
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        try {
            System.out.println("📥 Received Course: " + course);

            List<Course> validatedPrereqs = new ArrayList<>();
            if (course.getPrerequisites() != null) {
                for (Course prereq : course.getPrerequisites()) {
                    System.out.println("🔍 Checking prereq: " + prereq.getCourseId());
                    List<Course> matched = courseRepository.findByCourseId(prereq.getCourseId());
                    if (!matched.isEmpty()) {
                        validatedPrereqs.add(matched.get(0));
                    }
                }
            }

            course.setPrerequisites(validatedPrereqs);
            Course saved = courseRepository.save(course);

            System.out.println("✅ Saved Course: " + saved.getCourseId());
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); // 🔥 This will reveal any crash in the terminal
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error: " + e.getMessage());
        }
    }





    // 2. Get all courses
    @GetMapping("/courses")
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // 3. Get course by ID (courseId like "CS209")
    @GetMapping("/{courseId}")
    public ResponseEntity<?> getInstanceDetails(@PathVariable int academicYear,
                                                @PathVariable int semester,
                                                @PathVariable String courseId) {
        List<Course> courseList = courseRepository.findByCourseId(courseId);
        if (courseList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        Course course = courseList.get(0);  // Get first matching course

        Optional<CourseInstance> instanceOpt = courseInstanceRepository
                .findByAcademicYearAndSemesterAndCourse_Id(academicYear, semester, course.getId());

        if (instanceOpt.isPresent()) {
            return ResponseEntity.ok(instanceOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instance not found");
        }
    }

    // 4. Delete course by ID
    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable String courseId) {
        List<Course> courseList = courseRepository.findByCourseId(courseId);
        if (courseList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        // Check if any course lists this as a prerequisite
        List<Course> allCourses = courseRepository.findAll();
        for (Course c : allCourses) {
            if (c.getPrerequisites().stream()
                    .anyMatch(prereq -> prereq.getCourseId().equals(courseId))) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Cannot delete: Course is a prerequisite for " + c.getCourseId());
            }
        }

        courseRepository.deleteByCourseId(courseId);
        return ResponseEntity.ok("Course deleted");
    }
}
