package com.campusiq.campusiq.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.campusiq.campusiq.model.User;
import com.campusiq.campusiq.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public User registerUser(String username,
                             String email,
                             String password) {

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setUsername(username);
        user.setEmail(email);

        // Never store plain-text passwords
        user.setPassword(passwordEncoder.encode(password));

        // Default role
        user.setRole("STUDENT");

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(1000000));

        // Save OTP
        user.setOtp(otp);

        // OTP valid for 5 minutes
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        // User is not verified until OTP is correct
        user.setVerified(false);

        // Save user in database
        User savedUser = userRepository.save(user);

        // Send OTP to user's email
        emailService.sendOtpEmail(email, otp);

        return savedUser;
    }
}