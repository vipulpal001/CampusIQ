package com.campusiq.campusiq.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.campusiq.campusiq.model.Department;
import com.campusiq.campusiq.model.User;
import com.campusiq.campusiq.service.AuthService;
import com.campusiq.campusiq.service.DepartmentService;

/**
 * ============================================================================
 * [CAMPUSIQ ERP CONTROLLER]: AuthController
 * Web controller handling registration with role separation (Student, Faculty, Admin),
 * 6-digit OTP delivery, verification, and resending.
 * ============================================================================
 */
@Controller
public class AuthController {

    private final AuthService authService;
    private final DepartmentService departmentService;

    public AuthController(AuthService authService, DepartmentService departmentService) {
        this.authService = authService;
        this.departmentService = departmentService;
    }

    // ==========================================
    // 1. SHOW REGISTRATION PAGE
    // ==========================================
    @GetMapping("/register")
    public String registerPage(@RequestParam(required = false, defaultValue = "STUDENT") String role,
                               Model model) {
        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);
        model.addAttribute("selectedRole", role.toUpperCase());
        return "register";
    }

    // ==========================================
    // 2. PROCESS REGISTRATION
    // ==========================================
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String confirmPassword,
            @RequestParam(required = false, defaultValue = "STUDENT") String role,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String adminKey,
            @RequestParam(required = false) Long departmentId,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            if (confirmPassword != null && !confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
                throw new RuntimeException("Passwords do not match. Please verify your passwords.");
            }

            User user = authService.registerUser(
                    username,
                    email,
                    password,
                    role,
                    fullName,
                    adminKey,
                    departmentId
            );

            // Send info to OTP verification page
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("role", user.getRole());
            redirectAttributes.addFlashAttribute("fullName", user.getFullName());
            redirectAttributes.addFlashAttribute("departmentId", departmentId);
            redirectAttributes.addFlashAttribute("success",
                    "Registration initiated! A 6-digit verification code has been dispatched to " + email);

            return "redirect:/verify-otp?email=" + email + "&role=" + user.getRole();

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("fullName", fullName);
            model.addAttribute("selectedRole", role != null ? role.toUpperCase() : "STUDENT");
            model.addAttribute("adminKey", adminKey);
            model.addAttribute("departmentId", departmentId);
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "register";
        }
    }

    // ==========================================
    // 3. SHOW OTP VERIFICATION PAGE
    // ==========================================
    @GetMapping("/verify-otp")
    public String verifyOtpPage(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            Model model) {

        if (email != null && !email.isBlank()) {
            model.addAttribute("email", email);
        }
        if (role != null && !role.isBlank()) {
            model.addAttribute("role", role.toUpperCase());
        }

        return "verify-otp";
    }

    // ==========================================
    // 4. RESEND OTP
    // ==========================================
    @PostMapping("/resend-otp")
    public String resendOtp(
            @RequestParam String email,
            @RequestParam(required = false) String role,
            RedirectAttributes redirectAttributes) {

        try {
            authService.resendOtp(email);
            redirectAttributes.addFlashAttribute("success", "A fresh 6-digit OTP code has been dispatched to " + email);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error resending OTP: " + e.getMessage());
        }

        redirectAttributes.addFlashAttribute("email", email);
        if (role != null) {
            redirectAttributes.addFlashAttribute("role", role.toUpperCase());
        }

        return "redirect:/verify-otp?email=" + email + (role != null ? "&role=" + role.toUpperCase() : "");
    }

    // ==========================================
    // 5. VERIFY OTP
    // ==========================================
    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam(required = false) Long departmentId,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            User user = authService.verifyAndActivateUser(email, otp, departmentId);

            redirectAttributes.addFlashAttribute("verifiedMessage",
                    "Account activated successfully! You may now log in to your " + user.getRole() + " account.");

            return "redirect:/login?verified=true&role=" + user.getRole();

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "verify-otp";
        }
    }
}