package com.campusiq.campusiq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campusiq.campusiq.model.Student;

/**
 * ============================================================================
 * [CAMPUSIQ ERP REPOSITORY]: StudentRepository
 * Spring Data JPA Repository for student entities with search & filter capabilities
 * ============================================================================
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByRollNumber(String rollNumber);

    Optional<Student> findByEmail(String email);

    Optional<Student> findByPhoneNumber(String phoneNumber);

    boolean existsByRollNumber(String rollNumber);

    boolean existsByEmail(String email);

    List<Student> findTop5ByOrderByCreatedAtDesc();

    List<Student> findByDepartmentId(Long departmentId);

    long countByStatus(String status);

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.rollNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Student> searchStudents(@Param("keyword") String keyword);

    @Query("SELECT s FROM Student s WHERE s.department.id = :deptId AND (" +
           "LOWER(s.rollNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Student> searchStudentsByDepartment(@Param("deptId") Long deptId, @Param("keyword") String keyword);
}
