package com.campusiq.campusiq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.campusiq.campusiq.model.Course;

/**
 * ============================================================================
 * [CAMPUSIQ ERP REPOSITORY]: CourseRepository
 * Spring Data JPA Repository for academic courses/subjects
 * ============================================================================
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseCode(String courseCode);

    boolean existsByCourseCode(String courseCode);

    List<Course> findByDepartmentId(Long departmentId);

    List<Course> findBySemester(Integer semester);
}
