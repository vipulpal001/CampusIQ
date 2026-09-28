package com.campusiq.campusiq.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.campusiq.campusiq.model.Course;
import com.campusiq.campusiq.model.Department;
import com.campusiq.campusiq.model.Faculty;
import com.campusiq.campusiq.model.Notice;
import com.campusiq.campusiq.model.QuestionPaper;
import com.campusiq.campusiq.model.Student;
import com.campusiq.campusiq.model.TimetableEntry;
import com.campusiq.campusiq.service.CourseService;
import com.campusiq.campusiq.service.DepartmentService;
import com.campusiq.campusiq.service.FacultyService;
import com.campusiq.campusiq.service.NoticeService;
import com.campusiq.campusiq.service.QuestionPaperService;
import com.campusiq.campusiq.service.StudentService;
import com.campusiq.campusiq.service.TimetableService;
import com.campusiq.campusiq.service.attendanceService;

/**
 * ============================================================================
 * [CAMPUSIQ ERP CONTROLLER]: AdminController
 * Comprehensive administrator governance: KPIs, faculty, courses (subjects),
 * weekly timetable editing, university question papers, and notices broadcasting.
 * ============================================================================
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentService studentService;
    private final FacultyService facultyService;
    private final CourseService courseService;
    private final DepartmentService departmentService;
    private final attendanceService attendanceService;
    private final NoticeService noticeService;
    private final QuestionPaperService questionPaperService;
    private final TimetableService timetableService;

    public AdminController(StudentService studentService,
                           FacultyService facultyService,
                           CourseService courseService,
                           DepartmentService departmentService,
                           attendanceService attendanceService,
                           NoticeService noticeService,
                           QuestionPaperService questionPaperService,
                           TimetableService timetableService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.courseService = courseService;
        this.departmentService = departmentService;
        this.attendanceService = attendanceService;
        this.noticeService = noticeService;
        this.questionPaperService = questionPaperService;
        this.timetableService = timetableService;
    }

    // =========================================================================
    // 1. ADMIN SYSTEM DASHBOARD
    // =========================================================================
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("totalStudents", studentService.count());
        model.addAttribute("totalFaculty", facultyService.count());
        model.addAttribute("totalCourses", courseService.count());
        model.addAttribute("totalDepartments", departmentService.count());
        model.addAttribute("totalPapers", questionPaperService.count());
        model.addAttribute("totalSlots", timetableService.count());
        model.addAttribute("overallAttendance", attendanceService.getOverallAverageAttendancePercentage());

        List<Student> recentStudents = studentService.getRecentStudents();
        model.addAttribute("recentStudents", recentStudents);

        List<Faculty> faculties = facultyService.getAllFaculty();
        model.addAttribute("faculties", faculties);

        List<Notice> notices = noticeService.getAllNotices();
        model.addAttribute("notices", notices);

        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);

        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);

        List<TimetableEntry> timetable = timetableService.getAllEntries();
        model.addAttribute("timetable", timetable);

        List<QuestionPaper> questionPapers = questionPaperService.getAllQuestionPapers();
        model.addAttribute("questionPapers", questionPapers);

        return "admin-dashboard";
    }

    // =========================================================================
    // 2. FACULTY MANAGEMENT
    // =========================================================================
    @GetMapping("/faculty")
    public String manageFaculty(Model model) {
        List<Faculty> faculties = facultyService.getAllFaculty();
        List<Department> departments = departmentService.getAllDepartments();

        model.addAttribute("faculties", faculties);
        model.addAttribute("departments", departments);
        return "admin-faculty";
    }

    @PostMapping("/faculty/new")
    public String addFaculty(@RequestParam String employeeCode,
                             @RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam String phoneNumber,
                             @RequestParam String designation,
                             @RequestParam Long departmentId,
                             @RequestParam String cabinLocation,
                             RedirectAttributes redirectAttributes) {
        try {
            Department department = departmentService.getDepartmentById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid department ID: " + departmentId));

            Faculty faculty = new Faculty(employeeCode, fullName, email, phoneNumber, designation, department, cabinLocation);
            facultyService.saveFaculty(faculty);
            redirectAttributes.addFlashAttribute("successMessage", "Faculty member successfully added!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding faculty: " + e.getMessage());
        }
        return "redirect:/admin/faculty";
    }

    @PostMapping("/faculty/delete/{id}")
    public String deleteFaculty(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            facultyService.deleteFaculty(id);
            redirectAttributes.addFlashAttribute("successMessage", "Faculty member removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error removing faculty: " + e.getMessage());
        }
        return "redirect:/admin/faculty";
    }

    // =========================================================================
    // 3. SUBJECT / COURSE MANAGEMENT (ADD, EDIT, DELETE)
    // =========================================================================
    @GetMapping("/courses")
    public String manageCourses(Model model) {
        List<Course> courses = courseService.getAllCourses();
        List<Department> departments = departmentService.getAllDepartments();

        model.addAttribute("courses", courses);
        model.addAttribute("departments", departments);
        return "admin-courses";
    }

    @PostMapping("/courses/new")
    public String addCourse(@RequestParam String courseCode,
                            @RequestParam String name,
                            @RequestParam Integer credits,
                            @RequestParam Integer semester,
                            @RequestParam Long departmentId,
                            RedirectAttributes redirectAttributes) {
        try {
            Department department = departmentService.getDepartmentById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid department ID: " + departmentId));

            Course course = new Course(courseCode, name, credits, semester, department);
            courseService.saveCourse(course);
            redirectAttributes.addFlashAttribute("successMessage", "Subject / Course added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding subject: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    @PostMapping("/courses/edit/{id}")
    public String editCourse(@PathVariable Long id,
                             @RequestParam String courseCode,
                             @RequestParam String name,
                             @RequestParam Integer credits,
                             @RequestParam Integer semester,
                             @RequestParam Long departmentId,
                             RedirectAttributes redirectAttributes) {
        try {
            Course course = courseService.getCourseById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid course ID: " + id));
            Department department = departmentService.getDepartmentById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid department ID: " + departmentId));

            course.setCourseCode(courseCode);
            course.setName(name);
            course.setCredits(credits);
            course.setSemester(semester);
            course.setDepartment(department);

            courseService.saveCourse(course);
            redirectAttributes.addFlashAttribute("successMessage", "Subject / Course updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating subject: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    @PostMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Course removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error removing course: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    // =========================================================================
    // 4. TIME-TABLE MANAGEMENT (ADD, EDIT, DELETE)
    // =========================================================================
    @GetMapping("/timetable")
    public String manageTimetable(Model model) {
        List<TimetableEntry> entries = timetableService.getAllEntries();
        List<Course> courses = courseService.getAllCourses();
        List<Department> departments = departmentService.getAllDepartments();

        model.addAttribute("entries", entries);
        model.addAttribute("courses", courses);
        model.addAttribute("departments", departments);
        return "admin-timetable";
    }

    @PostMapping("/timetable/new")
    public String addTimetableSlot(@RequestParam String dayOfWeek,
                                   @RequestParam String timeSlot,
                                   @RequestParam(required = false) Long courseId,
                                   @RequestParam(required = false) String subjectLabel,
                                   @RequestParam String facultyName,
                                   @RequestParam String roomNumber,
                                   @RequestParam Long departmentId,
                                   @RequestParam Integer semester,
                                   RedirectAttributes redirectAttributes) {
        try {
            Department department = departmentService.getDepartmentById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid department ID: " + departmentId));

            Course course = courseId != null && courseId > 0 ? courseService.getCourseById(courseId).orElse(null) : null;

            TimetableEntry entry = new TimetableEntry(dayOfWeek, timeSlot, course, subjectLabel, facultyName, roomNumber, department, semester);
            timetableService.saveEntry(entry);
            redirectAttributes.addFlashAttribute("successMessage", "Timetable lecture slot added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving timetable slot: " + e.getMessage());
        }
        return "redirect:/admin/timetable";
    }

    @PostMapping("/timetable/edit/{id}")
    public String editTimetableSlot(@PathVariable Long id,
                                    @RequestParam String dayOfWeek,
                                    @RequestParam String timeSlot,
                                    @RequestParam(required = false) Long courseId,
                                    @RequestParam(required = false) String subjectLabel,
                                    @RequestParam String facultyName,
                                    @RequestParam String roomNumber,
                                    @RequestParam Long departmentId,
                                    @RequestParam Integer semester,
                                    RedirectAttributes redirectAttributes) {
        try {
            TimetableEntry entry = timetableService.getEntryById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid slot ID: " + id));
            Department department = departmentService.getDepartmentById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid department ID: " + departmentId));

            Course course = courseId != null && courseId > 0 ? courseService.getCourseById(courseId).orElse(null) : null;

            entry.setDayOfWeek(dayOfWeek);
            entry.setTimeSlot(timeSlot);
            entry.setCourse(course);
            entry.setSubjectLabel(subjectLabel);
            entry.setFacultyName(facultyName);
            entry.setRoomNumber(roomNumber);
            entry.setDepartment(department);
            entry.setSemester(semester);

            timetableService.saveEntry(entry);
            redirectAttributes.addFlashAttribute("successMessage", "Timetable lecture slot updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating timetable slot: " + e.getMessage());
        }
        return "redirect:/admin/timetable";
    }

    @PostMapping("/timetable/delete/{id}")
    public String deleteTimetableSlot(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            timetableService.deleteEntry(id);
            redirectAttributes.addFlashAttribute("successMessage", "Timetable slot deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting slot: " + e.getMessage());
        }
        return "redirect:/admin/timetable";
    }

    // =========================================================================
    // 5. UNIVERSITY QUESTION PAPERS (ADD, EDIT, DELETE)
    // =========================================================================
    @GetMapping("/question-papers")
    public String manageQuestionPapers(Model model) {
        List<QuestionPaper> papers = questionPaperService.getAllQuestionPapers();
        List<Course> courses = courseService.getAllCourses();

        model.addAttribute("papers", papers);
        model.addAttribute("courses", courses);
        return "admin-question-papers";
    }

    @PostMapping("/question-papers/new")
    public String addQuestionPaper(@RequestParam String courseCode,
                                   @RequestParam String subjectName,
                                   @RequestParam String paperCode,
                                   @RequestParam Integer semester,
                                   @RequestParam Integer examYear,
                                   @RequestParam String examType,
                                   @RequestParam(required = false) String downloadUrl,
                                   RedirectAttributes redirectAttributes) {
        try {
            QuestionPaper paper = new QuestionPaper(courseCode, subjectName, paperCode, semester, examYear, examType, downloadUrl);
            questionPaperService.saveQuestionPaper(paper);
            redirectAttributes.addFlashAttribute("successMessage", "University question paper published successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error publishing question paper: " + e.getMessage());
        }
        return "redirect:/admin/question-papers";
    }

    @PostMapping("/question-papers/edit/{id}")
    public String editQuestionPaper(@PathVariable Long id,
                                    @RequestParam String courseCode,
                                    @RequestParam String subjectName,
                                    @RequestParam String paperCode,
                                    @RequestParam Integer semester,
                                    @RequestParam Integer examYear,
                                    @RequestParam String examType,
                                    @RequestParam(required = false) String downloadUrl,
                                    RedirectAttributes redirectAttributes) {
        try {
            QuestionPaper paper = questionPaperService.getQuestionPaperById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid paper ID: " + id));

            paper.setCourseCode(courseCode);
            paper.setSubjectName(subjectName);
            paper.setPaperCode(paperCode);
            paper.setSemester(semester);
            paper.setExamYear(examYear);
            paper.setExamType(examType);
            paper.setDownloadUrl(downloadUrl);

            questionPaperService.saveQuestionPaper(paper);
            redirectAttributes.addFlashAttribute("successMessage", "University question paper updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating question paper: " + e.getMessage());
        }
        return "redirect:/admin/question-papers";
    }

    @PostMapping("/question-papers/delete/{id}")
    public String deleteQuestionPaper(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            questionPaperService.deleteQuestionPaper(id);
            redirectAttributes.addFlashAttribute("successMessage", "Question paper removed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting paper: " + e.getMessage());
        }
        return "redirect:/admin/question-papers";
    }

    // =========================================================================
    // 6. NOTICES MANAGEMENT (ADD, EDIT, DELETE)
    // =========================================================================
    @PostMapping("/notices/new")
    public String publishNotice(@RequestParam String title,
                                @RequestParam String content,
                                @RequestParam String category,
                                @RequestParam String postedBy,
                                @RequestParam String targetRole,
                                @RequestParam(defaultValue = "false") boolean important,
                                RedirectAttributes redirectAttributes) {
        try {
            noticeService.saveNotice(title, content, category, postedBy, targetRole, important);
            redirectAttributes.addFlashAttribute("successMessage", "Official notice circular published successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to publish notice: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/notices/delete/{id}")
    public String deleteNotice(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            noticeService.deleteNotice(id);
            redirectAttributes.addFlashAttribute("successMessage", "Notice removed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting notice: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}
