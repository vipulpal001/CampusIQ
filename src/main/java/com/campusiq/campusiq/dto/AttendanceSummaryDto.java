package com.campusiq.campusiq.dto;

import com.campusiq.campusiq.model.Course;

/**
 * ============================================================================
 * [CAMPUSIQ ERP DTO]: AttendanceSummaryDto
 * Data Transfer Object encapsulating attendance metrics, percentage, and eligibility
 * ============================================================================
 */
public class AttendanceSummaryDto {

    private Course course;
    private long totalLectures;
    private long attendedLectures;
    private double percentage;
    private boolean lowAttendance;

    public AttendanceSummaryDto() {
    }

    public AttendanceSummaryDto(long totalLectures, long attendedLectures) {
        this.totalLectures = totalLectures;
        this.attendedLectures = attendedLectures;
        this.percentage = totalLectures > 0 ? ((double) attendedLectures / totalLectures) * 100.0 : 100.0;
        this.lowAttendance = this.percentage < 75.0 && totalLectures > 0;
    }

    public AttendanceSummaryDto(Course course, long totalLectures, long attendedLectures) {
        this(totalLectures, attendedLectures);
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public long getTotalLectures() {
        return totalLectures;
    }

    public void setTotalLectures(long totalLectures) {
        this.totalLectures = totalLectures;
    }

    public long getAttendedLectures() {
        return attendedLectures;
    }

    public void setAttendedLectures(long attendedLectures) {
        this.attendedLectures = attendedLectures;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getFormattedPercentage() {
        return String.format("%.1f%%", percentage);
    }

    public String getAttendancePercentage() {
        return getFormattedPercentage();
    }

    public long getPresentLectures() {
        return attendedLectures;
    }

    public String getCourseName() {
        return course != null ? course.getName() : "General Attendance";
    }

    public String getCourseCode() {
        return course != null ? course.getCourseCode() : "";
    }

    public boolean isLowAttendance() {
        return lowAttendance;
    }

    public void setLowAttendance(boolean lowAttendance) {
        this.lowAttendance = lowAttendance;
    }
}
