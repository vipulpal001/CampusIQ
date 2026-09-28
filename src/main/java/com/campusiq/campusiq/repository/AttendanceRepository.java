package com.campusiq.campusiq.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campusiq.campusiq.model.AttendanceRecord;

/**
 * ============================================================================
 * [CAMPUSIQ ERP REPOSITORY]: AttendanceRepository
 * Spring Data JPA Repository for managing and querying student attendance records
 * ============================================================================
 */
@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findByCourseIdAndDate(Long courseId, LocalDate date);

    List<AttendanceRecord> findByStudentIdOrderByDateDesc(Long studentId);

    List<AttendanceRecord> findByStudentIdAndCourseIdOrderByDateDesc(Long studentId, Long courseId);

    Optional<AttendanceRecord> findByStudentIdAndCourseIdAndDate(Long studentId, Long courseId, LocalDate date);

    long countByStudentId(Long studentId);

    long countByStudentIdAndStatus(Long studentId, String status);

    long countByStatus(String status);

    List<AttendanceRecord> findTop20ByOrderByDateDescRecordedAtDesc();

    @Query("SELECT DISTINCT a.date FROM AttendanceRecord a WHERE a.course.id = :courseId ORDER BY a.date DESC")
    List<LocalDate> findDistinctDatesByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.student.id = :studentId AND a.course.id = :courseId")
    long countByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.student.id = :studentId AND a.course.id = :courseId AND a.status = :status")
    long countByStudentIdAndCourseIdAndStatus(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("status") String status);
}
