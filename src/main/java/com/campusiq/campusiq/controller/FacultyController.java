package com.campusiq.campusiq.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.campusiq.campusiq.model.AttendanceRecord;
import com.campusiq.campusiq.model.Course;
import com.campusiq.campusiq.model.Notice;
import com.campusiq.campusiq.model.SessionalMark;
import com.campusiq.campusiq.model.Student;
import com.campusiq.campusiq.service.CourseService;
import com.campusiq.campusiq.service.DepartmentService;
import com.campusiq.campusiq.service.FacultyService;
import com.campusiq.campusiq.service.NoticeService;
import com.campusiq.campusiq.service.SessionalMarkService;
import com.campusiq.campusiq.service.StudentService;
import com.campusiq.campusiq.service.attendanceService;

/**
 * ============================================================================
 * [CAMPUSIQ ERP CONTROLLER]: FacultyController
 * Handles all faculty operations: classroom attendance marking, sessional marks
 * recording, timetable schedule, and class announcements.
 * ============================================================================
 */
@Controller
@RequestMapping("/faculty")
public class FacultyController {

    private final CourseService courseService;
    private final StudentService studentService;
    private final attendanceService attendanceService;
    private final SessionalMarkService sessionalMarkService;
    private final NoticeService noticeService;
    private final FacultyService facultyService;

    public FacultyController(CourseService courseService,
                             StudentService studentService,
                             attendanceService attendanceService,
                             SessionalMarkService sessionalMarkService,
                             NoticeService noticeService,
                             FacultyService facultyService) {
        this.courseService = courseService;
        this.studentService = studentService;
        this.attendanceService = attendanceService;
        this.sessionalMarkService = sessionalMarkService;
        this.noticeService = noticeService;
        this.facultyService = facultyService;
    }

    @GetMapping("/dashboard")
    public String facultyDashboard(Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        model.addAttribute("totalCoursesCount", courses.size());
        model.addAttribute("totalStudentsCount", studentService.count());
        model.addAttribute("overallAttendance", attendanceService.getOverallAverageAttendancePercentage());

        List<Notice> facultyNotices = noticeService.getNoticesForFaculty();
        model.addAttribute("notices", facultyNotices);

        return "faculty-dashboard";
    }

    // =========================================================================
    // FACULTY ATTENDANCE WORKSPACE
    // =========================================================================
    @GetMapping("/attendance")
    public String facultyAttendance(
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
                List<Student> enrolledStudents = studentService.searchStudents("", selectedCourse.getDepartment().getId());
                model.addAttribute("enrolledStudents", enrolledStudents);

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
        }

        return "faculty-attendance";
    }

    @PostMapping("/attendance/save")
    public String saveFacultyAttendance(
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
            redirectAttributes.addFlashAttribute("successMessage", "Classroom attendance recorded successfully for " + date + "!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error recording attendance: " + e.getMessage());
        }

        return "redirect:/faculty/attendance?courseId=" + courseId + "&date=" + date;
    }

    // =========================================================================
    // FACULTY SESSIONAL MARKS WORKSPACE
    // =========================================================================
    @GetMapping("/marks")
    public String facultyMarks(@RequestParam(required = false) Long courseId, Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        model.addAttribute("selectedCourseId", courseId);

        if (courseId != null && courseId > 0) {
            Course selectedCourse = courseService.getCourseById(courseId).orElse(null);
            model.addAttribute("selectedCourse", selectedCourse);

            if (selectedCourse != null) {
                List<Student> enrolledStudents = studentService.searchStudents("", selectedCourse.getDepartment().getId());
                model.addAttribute("enrolledStudents", enrolledStudents);

                List<SessionalMark> existingMarks = sessionalMarkService.getMarksByCourseId(courseId);
                Map<Long, SessionalMark> marksMap = new HashMap<>();
                for (SessionalMark mark : existingMarks) {
                    marksMap.put(mark.getStudent().getId(), mark);
                }
                model.addAttribute("marksMap", marksMap);
            }
        }

        return "faculty-marks";
    }

    @PostMapping("/marks/save")
    public String saveFacultyMarks(@RequestParam Long courseId,
                                  @RequestParam Map<String, String> allParams,
                                  RedirectAttributes redirectAttributes) {
        try {
            Map<Long, Double> s1Map = new HashMap<>();
            Map<Long, Double> s2Map = new HashMap<>();
            Map<Long, Double> assMap = new HashMap<>();
            Map<Long, Double> taMap = new HashMap<>();
            Map<Long, String> remarksMap = new HashMap<>();

            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue();

                if (key.startsWith("s1_")) {
                    Long studentId = Long.parseLong(key.substring("s1_".length()));
                    s1Map.put(studentId, val.isBlank() ? 0.0 : Double.parseDouble(val));
                } else if (key.startsWith("s2_")) {
                    Long studentId = Long.parseLong(key.substring("s2_".length()));
                    s2Map.put(studentId, val.isBlank() ? 0.0 : Double.parseDouble(val));
                } else if (key.startsWith("ass_")) {
                    Long studentId = Long.parseLong(key.substring("ass_".length()));
                    assMap.put(studentId, val.isBlank() ? 0.0 : Double.parseDouble(val));
                } else if (key.startsWith("ta_")) {
                    Long studentId = Long.parseLong(key.substring("ta_".length()));
                    taMap.put(studentId, val.isBlank() ? 0.0 : Double.parseDouble(val));
                } else if (key.startsWith("remarks_")) {
                    Long studentId = Long.parseLong(key.substring("remarks_".length()));
                    remarksMap.put(studentId, val);
                }
            }

            sessionalMarkService.batchRecordMarks(courseId, s1Map, s2Map, assMap, taMap, remarksMap);
            redirectAttributes.addFlashAttribute("successMessage", "Sessional marks recorded & updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving marks: " + e.getMessage());
        }

        return "redirect:/faculty/marks?courseId=" + courseId;
    }

    @PostMapping("/notices/new")
    public String postClassNotice(@RequestParam String title,
                                  @RequestParam String content,
                                  @RequestParam String category,
                                  @RequestParam(defaultValue = "false") boolean important,
                                  RedirectAttributes redirectAttributes) {
        try {
            noticeService.saveNotice(title, content, category, "Dr. Rajesh Sharma (Faculty)", "STUDENT", important);
            redirectAttributes.addFlashAttribute("successMessage", "Class notice broadcasted to students!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error broadcasting notice: " + e.getMessage());
        }
        return "redirect:/faculty/dashboard";
    }
}
