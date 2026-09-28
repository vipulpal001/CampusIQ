package com.campusiq.campusiq.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * ============================================================================
 * [CAMPUSIQ ERP ENTITY]: QuestionPaper
 * Represents university / AKTU past semester examination question papers
 * ============================================================================
 */
@Entity
@Table(name = "question_papers")
public class QuestionPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_code", nullable = false)
    private String courseCode; // e.g. CS101, CS201

    @Column(name = "subject_name", nullable = false)
    private String subjectName; // e.g. Data Structures & Algorithms

    @Column(name = "paper_code", nullable = false)
    private String paperCode; // e.g. KCS-301

    @Column(nullable = false)
    private Integer semester = 3;

    @Column(name = "exam_year", nullable = false)
    private Integer examYear = 2024;

    @Column(name = "exam_type", nullable = false)
    private String examType = "End Semester Examination"; // End Semester, Carry Over, Sessional

    @Column(name = "download_url")
    private String downloadUrl = "#";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public QuestionPaper() {
    }

    public QuestionPaper(String courseCode, String subjectName, String paperCode, Integer semester, Integer examYear, String examType, String downloadUrl) {
        this.courseCode = courseCode;
        this.subjectName = subjectName;
        this.paperCode = paperCode;
        this.semester = semester;
        this.examYear = examYear;
        this.examType = examType;
        this.downloadUrl = downloadUrl != null && !downloadUrl.isBlank() ? downloadUrl : "#";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getPaperCode() {
        return paperCode;
    }

    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Integer getExamYear() {
        return examYear;
    }

    public void setExamYear(Integer examYear) {
        this.examYear = examYear;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
