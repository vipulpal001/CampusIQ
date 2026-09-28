package com.campusiq.campusiq.model;

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
 * [CAMPUSIQ ERP ENTITY]: SessionalMark
 * Represents internal / sessional examination marks recorded by Faculty for a Student
 * ============================================================================
 */
@Entity
@Table(
    name = "sessional_marks",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_student_course_mark",
            columnNames = {"student_id", "course_id"}
        )
    }
)
public class SessionalMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Sessional 1 marks (Out of 30)
    @Column(name = "sessional_one")
    private Double sessionalOne = 0.0;

    // Sessional 2 marks (Out of 30)
    @Column(name = "sessional_two")
    private Double sessionalTwo = 0.0;

    // Internal assignment / Quiz (Out of 20)
    @Column(name = "assignment_marks")
    private Double assignmentMarks = 0.0;

    // Teacher Assessment / Attendance weightage (Out of 20)
    @Column(name = "teacher_assessment")
    private Double teacherAssessment = 0.0;

    // Maximum possible marks (default 100)
    @Column(name = "max_marks", nullable = false)
    private Double maxMarks = 100.0;

    // Total calculated marks
    @Column(name = "total_marks")
    private Double totalMarks = 0.0;

    @Column(nullable = false)
    private String status = "PASS"; // PASS, FAIL, PENDING

    @Column
    private String remarks;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onSave() {
        calculateTotal();
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    public void calculateTotal() {
        double s1 = (sessionalOne != null) ? sessionalOne : 0.0;
        double s2 = (sessionalTwo != null) ? sessionalTwo : 0.0;
        double ass = (assignmentMarks != null) ? assignmentMarks : 0.0;
        double ta = (teacherAssessment != null) ? teacherAssessment : 0.0;

        this.totalMarks = s1 + s2 + ass + ta;
        if (this.totalMarks >= 40.0) {
            this.status = "PASS";
        } else {
            this.status = "FAIL";
        }
        this.updatedAt = LocalDateTime.now();
    }

    public SessionalMark() {
    }

    public SessionalMark(Student student, Course course, Double sessionalOne, Double sessionalTwo,
                         Double assignmentMarks, Double teacherAssessment, String remarks) {
        this.student = student;
        this.course = course;
        this.sessionalOne = sessionalOne;
        this.sessionalTwo = sessionalTwo;
        this.assignmentMarks = assignmentMarks;
        this.teacherAssessment = teacherAssessment;
        this.remarks = remarks;
        calculateTotal();
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

    public Double getSessionalOne() {
        return sessionalOne;
    }

    public void setSessionalOne(Double sessionalOne) {
        this.sessionalOne = sessionalOne;
        calculateTotal();
    }

    public Double getSessionalTwo() {
        return sessionalTwo;
    }

    public void setSessionalTwo(Double sessionalTwo) {
        this.sessionalTwo = sessionalTwo;
        calculateTotal();
    }

    public Double getAssignmentMarks() {
        return assignmentMarks;
    }

    public void setAssignmentMarks(Double assignmentMarks) {
        this.assignmentMarks = assignmentMarks;
        calculateTotal();
    }

    public Double getTeacherAssessment() {
        return teacherAssessment;
    }

    public void setTeacherAssessment(Double teacherAssessment) {
        this.teacherAssessment = teacherAssessment;
        calculateTotal();
    }

    public Double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public Double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Double totalMarks) {
        this.totalMarks = totalMarks;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
