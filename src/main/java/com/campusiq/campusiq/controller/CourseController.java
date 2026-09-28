package com.campusiq.campusiq.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.campusiq.campusiq.model.Course;
import com.campusiq.campusiq.model.Department;
import com.campusiq.campusiq.service.CourseService;
import com.campusiq.campusiq.service.DepartmentService;

/**
 * ============================================================================
 * [CAMPUSIQ ERP CONTROLLER]: CourseController
 * Web controller handling Academic Course Catalog management routes:
 * listing courses by department, adding courses, and removing courses.
 * ============================================================================
 */
@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final DepartmentService departmentService;

    public CourseController(CourseService courseService, DepartmentService departmentService) {
        this.courseService = courseService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public String listCourses(@RequestParam(required = false) Long departmentId, Model model) {
        return "redirect:/admin/courses";
    }

    @PostMapping("/save")
    public String saveCourse(@ModelAttribute("newCourse") Course course, RedirectAttributes redirectAttributes) {
        try {
            courseService.saveCourse(course);
            redirectAttributes.addFlashAttribute("successMessage", "Course added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving course: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Course removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error removing course: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }
}
