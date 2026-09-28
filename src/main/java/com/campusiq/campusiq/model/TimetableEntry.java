package com.campusiq.campusiq.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * ============================================================================
 * [CAMPUSIQ ERP ENTITY]: TimetableEntry
 * Represents a lecture / lab period slot in the academic timetable
 * ============================================================================
 */
@Entity
@Table(name = "timetable_entries")
public class TimetableEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek; // MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY

    @Column(name = "time_slot", nullable = false)
    private String timeSlot; // e.g. "09:00 - 10:00", "10:00 - 11:00", "11:15 - 12:15", "12:15 - 01:15", "02:00 - 04:00"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "subject_label")
    private String subjectLabel; // Fallback or custom label e.g. "Data Structures Lab"

    @Column(name = "faculty_name", nullable = false)
    private String facultyName; // e.g. "Dr. Rajesh Sharma"

    @Column(name = "room_number", nullable = false)
    private String roomNumber; // e.g. "R301", "Lab 4"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(nullable = false)
    private Integer semester = 5;

    public TimetableEntry() {
    }

    public TimetableEntry(String dayOfWeek, String timeSlot, Course course, String subjectLabel,
                          String facultyName, String roomNumber, Department department, Integer semester) {
        this.dayOfWeek = dayOfWeek;
        this.timeSlot = timeSlot;
        this.course = course;
        this.subjectLabel = subjectLabel != null ? subjectLabel : (course != null ? course.getName() : "");
        this.facultyName = facultyName;
        this.roomNumber = roomNumber;
        this.department = department;
        this.semester = semester;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getSubjectLabel() {
        return subjectLabel != null ? subjectLabel : (course != null ? course.getName() : "");
    }

    public void setSubjectLabel(String subjectLabel) {
        this.subjectLabel = subjectLabel;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }
}
