package com.campusiq.campusiq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.campusiq.campusiq.model.User;
import com.campusiq.campusiq.repository.UserRepository;
import com.campusiq.campusiq.service.AuthService;

@Controller
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService,
                          UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    // ==========================================
    // SHOW REGISTRATION PAGE
    // ==========================================

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    // ==========================================
    // PROCESS REGISTRATION
    // ==========================================

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        try {

            authService.registerUser(
                    username,
                    email,
                    password
            );

            // Send user to OTP verification page
            model.addAttribute("email", email);

            return "verify-otp";

        } catch (RuntimeException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "register";
        }
    }

    // ==========================================
    // SHOW OTP VERIFICATION PAGE
    // ==========================================

    @GetMapping("/verify-otp")
    public String showVerifyOtpPage(
            @RequestParam(required = false) String email,
            Model model) {

        if (email != null && !email.isBlank()) {
            model.addAttribute("email", email);
        }

        return "verify-otp";
    }

    // ==========================================
    // VERIFY OTP
    // ==========================================

    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String email,
            @RequestParam String otp,
            Model model) {

        try {

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new RuntimeException("User not found")
                    );

            // Check whether OTP exists
            if (user.getOtp() == null ||
                user.getOtpExpiry() == null) {

                model.addAttribute(
                        "error",
                        "OTP not found. Please register again."
                );

                model.addAttribute("email", email);

                return "verify-otp";
            }

            // Check OTP expiry
            if (java.time.LocalDateTime.now()
                    .isAfter(user.getOtpExpiry())) {

                model.addAttribute(
                        "error",
                        "OTP has expired. Please register again."
                );

                model.addAttribute("email", email);

                return "verify-otp";
            }

            // Check OTP
            if (!user.getOtp().equals(otp)) {

                model.addAttribute(
                        "error",
                        "Invalid OTP. Please try again."
                );

                model.addAttribute("email", email);

                return "verify-otp";
            }

            // OTP correct
            user.setVerified(true);

            // Remove OTP after successful verification
            user.setOtp(null);
            user.setOtpExpiry(null);

            userRepository.save(user);

            model.addAttribute(
                    "success",
                    "Email verified successfully! You can now login."
            );

            return "login";

        } catch (RuntimeException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            model.addAttribute("email", email);

            return "verify-otp";
        }
    }
}