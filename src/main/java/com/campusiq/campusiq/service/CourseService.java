package com.campusiq.campusiq.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.campusiq.campusiq.model.Course;
import com.campusiq.campusiq.repository.CourseRepository;

/**
 * ============================================================================
 * [CAMPUSIQ ERP SERVICE]: CourseService
 * Business logic for academic course/subject catalog management
 * ============================================================================
 */
@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public long count() {
        return courseRepository.count();
    }

    public List<Course> getCoursesByDepartment(Long departmentId) {
        return courseRepository.findByDepartmentId(departmentId);
    }
}
