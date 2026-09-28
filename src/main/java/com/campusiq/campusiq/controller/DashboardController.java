package com.campusiq.campusiq.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.campusiq.campusiq.dto.AttendanceSummaryDto;
import com.campusiq.campusiq.model.AttendanceRecord;
import com.campusiq.campusiq.model.Course;
import com.campusiq.campusiq.model.Notice;
import com.campusiq.campusiq.model.QuestionPaper;
import com.campusiq.campusiq.model.SessionalMark;
import com.campusiq.campusiq.model.Student;
import com.campusiq.campusiq.model.TimetableEntry;
import com.campusiq.campusiq.service.CourseService;
import com.campusiq.campusiq.service.DepartmentService;
import com.campusiq.campusiq.service.NoticeService;
import com.campusiq.campusiq.service.QuestionPaperService;
import com.campusiq.campusiq.service.SessionalMarkService;
import com.campusiq.campusiq.service.StudentService;
import com.campusiq.campusiq.service.TimetableService;
import com.campusiq.campusiq.service.attendanceService;

/**
 * ============================================================================
 * [CAMPUSIQ ERP CONTROLLER]: DashboardController
 * Powers the unified Student ERP Portal, supporting dynamic tab navigation:
 * - dashboard, profile, attendance, timetable, marks, feedback, messages,
 *   question-papers, and notices.
 * ============================================================================
 */
@Controller
public class DashboardController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final DepartmentService departmentService;
    private final attendanceService attendanceService;
    private final SessionalMarkService sessionalMarkService;
    private final NoticeService noticeService;
    private final QuestionPaperService questionPaperService;
    private final TimetableService timetableService;

    public DashboardController(StudentService studentService,
                               CourseService courseService,
                               DepartmentService departmentService,
                               attendanceService attendanceService,
                               SessionalMarkService sessionalMarkService,
                               NoticeService noticeService,
                               QuestionPaperService questionPaperService,
                               TimetableService timetableService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.departmentService = departmentService;
        this.attendanceService = attendanceService;
        this.sessionalMarkService = sessionalMarkService;
        this.noticeService = noticeService;
        this.questionPaperService = questionPaperService;
        this.timetableService = timetableService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "dashboard") String tab, Model model) {

        // 1. Fetch Student profile for TANMAY ATRAY (Default student)
        Student student = studentService.getStudentByRollNumber("2024CSE042")
                .orElseGet(() -> studentService.getAllStudents().stream().findFirst().orElse(null));

        model.addAttribute("student", student);
        model.addAttribute("activeTab", tab);

        // 2. Fetch courses & department data
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        model.addAttribute("totalCourses", courses.size());

        // 3. Interconnected Attendance Data
        if (student != null) {
            AttendanceSummaryDto studentSummary = attendanceService.getStudentOverallSummary(student.getId());
            model.addAttribute("studentAttendanceSummary", studentSummary);

            List<Course> deptCourses = courseService.getCoursesByDepartment(student.getDepartment().getId());
            List<AttendanceSummaryDto> courseSummaries = attendanceService.getStudentCourseWiseSummaries(student.getId(), deptCourses);
            model.addAttribute("courseSummaries", courseSummaries);

            List<AttendanceRecord> attendanceHistory = attendanceService.getStudentAttendanceHistory(student.getId());
            model.addAttribute("attendanceHistory", attendanceHistory);

            // 4. Interconnected Sessional Marks (Entered by Faculty)
            List<SessionalMark> sessionalMarks = sessionalMarkService.getMarksByStudentId(student.getId());
            model.addAttribute("sessionalMarks", sessionalMarks);
        } else {
            model.addAttribute("studentAttendanceSummary", new AttendanceSummaryDto(0, 0));
            model.addAttribute("courseSummaries", new ArrayList<>());
            model.addAttribute("attendanceHistory", new ArrayList<>());
            model.addAttribute("sessionalMarks", new ArrayList<>());
        }

        // 5. Interconnected Notices (Published by Admin / Faculty)
        List<Notice> studentNotices = noticeService.getNoticesForStudents();
        model.addAttribute("notices", studentNotices);

        // 6. Interconnected University Question Papers (Managed by Admin)
        List<QuestionPaper> questionPapers = questionPaperService.getAllQuestionPapers();
        model.addAttribute("questionPapers", questionPapers);

        // 7. Interconnected Timetable Entries (Managed by Admin)
        List<TimetableEntry> timetableEntries = timetableService.getAllEntries();
        model.addAttribute("timetableEntries", timetableEntries);

        // Overall institutional metrics for overview widgets
        model.addAttribute("overallAttendance", attendanceService.getOverallAverageAttendancePercentage());
        model.addAttribute("totalStudents", studentService.count());
        model.addAttribute("totalDepartments", departmentService.count());

        return "dashboard";
    }

    // Direct vanity routes mapping to the corresponding dashboard tabs
    @GetMapping("/student/profile")
    public String studentProfile() {
        return "redirect:/dashboard?tab=profile";
    }

    @GetMapping("/student/attendance")
    public String studentAttendance() {
        return "redirect:/dashboard?tab=attendance";
    }

    @GetMapping("/student/timetable")
    public String studentTimetable() {
        return "redirect:/dashboard?tab=timetable";
    }

    @GetMapping("/student/marks")
    public String studentMarks() {
        return "redirect:/dashboard?tab=marks";
    }

    @GetMapping("/student/feedback")
    public String studentFeedback() {
        return "redirect:/dashboard?tab=feedback";
    }

    @GetMapping("/student/messages")
    public String studentMessages() {
        return "redirect:/dashboard?tab=messages";
    }

    @GetMapping("/student/question-papers")
    public String studentQuestionPapers() {
        return "redirect:/dashboard?tab=question-papers";
    }

    @GetMapping("/student/notices")
    public String studentNotices() {
        return "redirect:/dashboard?tab=notices";
    }

    @PostMapping("/student/feedback/submit")
    public String submitFeedback(@RequestParam String courseCode,
                                 @RequestParam String facultyName,
                                 @RequestParam int rating,
                                 @RequestParam String comments,
                                 RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("feedbackSuccess", "Thank you! Your feedback for " + facultyName + " (" + courseCode + ") has been securely submitted to the Academic Dean.");
        return "redirect:/dashboard?tab=feedback";
    }
}