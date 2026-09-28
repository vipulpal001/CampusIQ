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

import com.campusiq.campusiq.model.Department;
import com.campusiq.campusiq.model.Student;
import com.campusiq.campusiq.service.DepartmentService;
import com.campusiq.campusiq.service.StudentService;

/**
 * ============================================================================
 * [CAMPUSIQ ERP CONTROLLER]: StudentController
 * Web controller handling Student Information System (SIS) CRUD routes:
 * list, search, enroll (new), edit, and remove.
 * ============================================================================
 */
@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final DepartmentService departmentService;

    public StudentController(StudentService studentService, DepartmentService departmentService) {
        this.studentService = studentService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public String listStudents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId,
            Model model) {

        List<Student> students = studentService.searchStudents(search, departmentId);
        List<Department> departments = departmentService.getAllDepartments();

        model.addAttribute("students", students);
        model.addAttribute("departments", departments);
        model.addAttribute("search", search);
        model.addAttribute("selectedDepartmentId", departmentId);
        model.addAttribute("totalStudentsCount", studentService.count());
        model.addAttribute("activeStudentsCount", studentService.countActiveStudents());

        return "students";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Student student = new Student();
        List<Department> departments = departmentService.getAllDepartments();

        model.addAttribute("student", student);
        model.addAttribute("departments", departments);
        model.addAttribute("isEdit", false);

        return "student-form";
    }

    @PostMapping("/save")
    public String saveStudent(@ModelAttribute("student") Student student, RedirectAttributes redirectAttributes) {
        try {
            studentService.saveStudent(student);
            redirectAttributes.addFlashAttribute("successMessage", "Student saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving student: " + e.getMessage());
            return "redirect:/students/new";
        }
        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return studentService.getStudentById(id)
                .map(student -> {
                    List<Department> departments = departmentService.getAllDepartments();
                    model.addAttribute("student", student);
                    model.addAttribute("departments", departments);
                    model.addAttribute("isEdit", true);
                    return "student-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Student not found!");
                    return "redirect:/students";
                });
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Student removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete student: " + e.getMessage());
        }
        return "redirect:/students";
    }
}
