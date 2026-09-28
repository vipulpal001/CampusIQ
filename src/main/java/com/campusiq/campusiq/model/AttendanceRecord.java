package com.campusiq.campusiq.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * ============================================================================
 * [CAMPUSIQ ERP ENTITY]: AttendanceRecord
 * Represents a lecture-wise / daily attendance entry for a student in a course
 * ============================================================================
 */
@Entity
@Table(
    name = "attendance_records",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_student_course_date",
            columnNames = {"student_id", "course_id", "date"}
        )
    }
)
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String status = "PRESENT"; // PRESENT, ABSENT, LATE, EXCUSED

    @Column(length = 255)
    private String remarks;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @PrePersist
    protected void onCreate() {
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "PRESENT";
        }
    }

    public AttendanceRecord() {
    }

    public AttendanceRecord(Student student, Course course, LocalDate date, String status, String remarks) {
        this.student = student;
        this.course = course;
        this.date = date;
        this.status = status;
        this.remarks = remarks;
        this.recordedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
