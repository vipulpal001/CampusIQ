package com.campusiq.campusiq.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.campusiq.campusiq.model.Notice;
import com.campusiq.campusiq.repository.CourseRepository;
import com.campusiq.campusiq.repository.DepartmentRepository;
import com.campusiq.campusiq.repository.FacultyRepository;
import com.campusiq.campusiq.repository.NoticeRepository;
import com.campusiq.campusiq.repository.StudentRepository;

@Controller
public class PageController {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final NoticeRepository noticeRepository;

    public PageController(StudentRepository studentRepository,
                          FacultyRepository facultyRepository,
                          CourseRepository courseRepository,
                          DepartmentRepository departmentRepository,
                          NoticeRepository noticeRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.noticeRepository = noticeRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        long studentCount = studentRepository.count();
        long facultyCount = facultyRepository.count();
        long courseCount = courseRepository.count();
        long departmentCount = departmentRepository.count();

        model.addAttribute("studentCount", studentCount > 0 ? studentCount : 1240);
        model.addAttribute("facultyCount", facultyCount > 0 ? facultyCount : 48);
        model.addAttribute("courseCount", courseCount > 0 ? courseCount : 32);
        model.addAttribute("departmentCount", departmentCount > 0 ? departmentCount : 5);

        List<Notice> recentNotices = noticeRepository.findTop5ByOrderByCreatedAtDesc();
        model.addAttribute("recentNotices", recentNotices);

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}