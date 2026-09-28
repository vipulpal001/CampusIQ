package com.campusiq.campusiq.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.campusiq.campusiq.dto.AttendanceSummaryDto;
import com.campusiq.campusiq.model.AttendanceRecord;
import com.campusiq.campusiq.model.Course;
import com.campusiq.campusiq.model.Student;
import com.campusiq.campusiq.service.attendanceService;
import com.campusiq.campusiq.service.CourseService;
import com.campusiq.campusiq.service.StudentService;

/**
 * ============================================================================
 * [CAMPUSIQ ERP CONTROLLER]: AttendanceController
 * Web controller handling daily attendance marking, course rosters, and student reports
 * ============================================================================
 */
@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    private final attendanceService attendanceService;
    private final CourseService courseService;
    private final StudentService studentService;

    public AttendanceController(attendanceService attendanceService,
                                CourseService courseService,
                                StudentService studentService) {
        this.attendanceService = attendanceService;
        this.courseService = courseService;
        this.studentService = studentService;
    }

    @GetMapping
    public String attendanceWorkspace(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);

        if (date == null) {
            date = LocalDate.now();
        }
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedCourseId", courseId);

        if (courseId != null && courseId > 0) {
            Course selectedCourse = courseService.getCourseById(courseId).orElse(null);
            model.addAttribute("selectedCourse", selectedCourse);

            if (selectedCourse != null) {
                // Fetch students matching course's department and semester
                List<Student> enrolledStudents = studentService.searchStudents("", selectedCourse.getDepartment().getId());
                model.addAttribute("enrolledStudents", enrolledStudents);

                // Check existing attendance for this date
                List<AttendanceRecord> existingRecords = attendanceService.getAttendanceByCourseAndDate(courseId, date);
                Map<Long, String> statusMap = new HashMap<>();
                Map<Long, String> remarksMap = new HashMap<>();
                for (AttendanceRecord record : existingRecords) {
                    statusMap.put(record.getStudent().getId(), record.getStatus());
                    remarksMap.put(record.getStudent().getId(), record.getRemarks());
                }
                model.addAttribute("statusMap", statusMap);
                model.addAttribute("remarksMap", remarksMap);
                model.addAttribute("hasExistingRecords", !existingRecords.isEmpty());
            }
        } else {
            // If no course selected, display recent overall attendance records
            List<AttendanceRecord> recentRecords = attendanceService.getRecentRecords();
            model.addAttribute("recentRecords", recentRecords);
        }

        model.addAttribute("overallAttendance", attendanceService.getOverallAverageAttendancePercentage());

        return "attendance";
    }

    @PostMapping("/save")
    public String saveAttendance(
            @RequestParam Long courseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes) {

        try {
            Map<Long, String> statusMap = new HashMap<>();
            Map<Long, String> remarksMap = new HashMap<>();

            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("status_")) {
                    Long studentId = Long.parseLong(key.substring("status_".length()));
                    statusMap.put(studentId, entry.getValue());
                } else if (key.startsWith("remarks_")) {
                    Long studentId = Long.parseLong(key.substring("remarks_".length()));
                    remarksMap.put(studentId, entry.getValue());
                }
            }

            attendanceService.markAttendanceBatch(courseId, date, statusMap, remarksMap);
            redirectAttributes.addFlashAttribute("successMessage", "Attendance successfully recorded for " + date + "!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving attendance: " + e.getMessage());
        }

        return "redirect:/attendance?courseId=" + courseId + "&date=" + date;
    }

    @GetMapping("/student/{id}")
    public String studentAttendanceReport(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return studentService.getStudentById(id)
                .map(student -> {
                    model.addAttribute("student", student);
                    AttendanceSummaryDto overallSummary = attendanceService.getStudentOverallSummary(id);
                    model.addAttribute("overallSummary", overallSummary);

                    List<Course> deptCourses = courseService.getCoursesByDepartment(student.getDepartment().getId());
                    List<AttendanceSummaryDto> courseSummaries = attendanceService.getStudentCourseWiseSummaries(id, deptCourses);
                    model.addAttribute("courseSummaries", courseSummaries);

                    List<AttendanceRecord> history = attendanceService.getStudentAttendanceHistory(id);
                    model.addAttribute("history", history);

                    return "student-attendance";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Student not found!");
                    return "redirect:/students";
                });
    }
}
