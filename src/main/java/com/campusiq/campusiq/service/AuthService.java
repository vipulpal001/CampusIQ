package com.campusiq.campusiq.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.campusiq.campusiq.model.Department;
import com.campusiq.campusiq.model.Faculty;
import com.campusiq.campusiq.model.Student;
import com.campusiq.campusiq.model.User;
import com.campusiq.campusiq.repository.DepartmentRepository;
import com.campusiq.campusiq.repository.FacultyRepository;
import com.campusiq.campusiq.repository.StudentRepository;
import com.campusiq.campusiq.repository.UserRepository;

/**
 * ============================================================================
 * [CAMPUSIQ ERP SERVICE]: AuthService
 * Handles institutional account creation with role separation (Student, Faculty, Admin),
 * 6-digit OTP generation and validation, and entity provisioning.
 * ============================================================================
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService,
                       StudentRepository studentRepository,
                       FacultyRepository facultyRepository,
                       DepartmentRepository departmentRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.departmentRepository = departmentRepository;
    }

    public User registerUser(String username,
                             String email,
                             String password) {
        return registerUser(username, email, password, "STUDENT", null, null, null);
    }

    public User registerUser(String username,
                             String email,
                             String password,
                             String role,
                             String fullName,
                             String adminKey,
                             Long departmentId) {

        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("Username or Institutional ID is required.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email address is required.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Password is required.");
        }

        username = username.trim();
        email = email.trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username '" + username + "' is already registered. Please choose another username.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email '" + email + "' is already registered. Please login or verify your email.");
        }

        // Determine sanitized role
        String normalizedRole = "STUDENT";
        if (role != null) {
            String r = role.trim().toUpperCase();
            if (r.contains("ADMIN")) {
                normalizedRole = "ADMIN";
                // Enforce / Validate Admin Passkey
                if (adminKey == null || adminKey.trim().isEmpty()) {
                    throw new RuntimeException("Admin Security Key is required to create an Administrator account.");
                }
                String cleanKey = adminKey.trim();
                if (!cleanKey.equals("ADMIN123") && !cleanKey.equals("ADMIN2026") && !cleanKey.equals("RKGIT2026") && !cleanKey.equalsIgnoreCase("RKGIT@ADMIN")) {
                    throw new RuntimeException("Invalid Admin Security Passkey. Please verify administrative credentials.");
                }
            } else if (r.contains("FACULTY") || r.contains("TEACHER") || r.contains("EMPLOYEE")) {
                normalizedRole = "FACULTY";
            }
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName != null && !fullName.trim().isEmpty() ? fullName.trim() : username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(normalizedRole);

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        user.setVerified(false);

        User savedUser = userRepository.save(user);

        // Send OTP email
        emailService.sendOtpEmail(email, otp);

        return savedUser;
    }

    public String resendOtp(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email address is required to resend OTP.");
        }
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("No account found registered with email: " + email));

        String newOtp = String.format("%06d", new Random().nextInt(1_000_000));
        user.setOtp(newOtp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), newOtp);
        return newOtp;
    }

    public User verifyAndActivateUser(String email, String enteredOtp, Long departmentId) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required.");
        }
        if (enteredOtp == null || enteredOtp.trim().isEmpty()) {
            throw new RuntimeException("Verification OTP is required.");
        }

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Account not found with email: " + email));

        if (user.getOtp() == null || user.getOtpExpiry() == null) {
            throw new RuntimeException("No active OTP found. Please request a new OTP or register again.");
        }

        if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            throw new RuntimeException("The OTP has expired. Please click 'Resend Code' to receive a new OTP.");
        }

        if (!user.getOtp().equals(enteredOtp.trim())) {
            throw new RuntimeException("Invalid OTP code. Please enter the 6-digit code sent to your email.");
        }

        user.setVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        User verifiedUser = userRepository.save(user);

        // Auto-provision corresponding Student or Faculty entity if not exists
        try {
            Department defaultDept = null;
            if (departmentId != null) {
                defaultDept = departmentRepository.findById(departmentId).orElse(null);
            }
            if (defaultDept == null) {
                defaultDept = departmentRepository.findAll().stream().findFirst().orElse(null);
            }

            if ("STUDENT".equalsIgnoreCase(verifiedUser.getRole())) {
                if (studentRepository.findByEmail(verifiedUser.getEmail()).isEmpty() &&
                    studentRepository.findByRollNumber(verifiedUser.getUsername()).isEmpty() &&
                    defaultDept != null) {

                    String name = verifiedUser.getFullName() != null ? verifiedUser.getFullName() : verifiedUser.getUsername();
                    String firstName = name;
                    String lastName = "";
                    if (name.contains(" ")) {
                        firstName = name.substring(0, name.lastIndexOf(" "));
                        lastName = name.substring(name.lastIndexOf(" ") + 1);
                    }
                    Student student = new Student();
                    student.setRollNumber(verifiedUser.getUsername());
                    student.setFirstName(firstName);
                    student.setLastName(lastName.isEmpty() ? "Student" : lastName);
                    student.setEmail(verifiedUser.getEmail());
                    student.setSemester(1);
                    student.setStatus("ACTIVE");
                    student.setDepartment(defaultDept);
                    studentRepository.save(student);
                }
            } else if ("FACULTY".equalsIgnoreCase(verifiedUser.getRole())) {
                if (facultyRepository.findByEmail(verifiedUser.getEmail()).isEmpty() &&
                    facultyRepository.findByEmployeeCode(verifiedUser.getUsername()).isEmpty() &&
                    defaultDept != null) {

                    String name = verifiedUser.getFullName() != null ? verifiedUser.getFullName() : verifiedUser.getUsername();
                    Faculty faculty = new Faculty(
                            verifiedUser.getUsername(),
                            name,
                            verifiedUser.getEmail(),
                            "",
                            "Assistant Professor",
                            defaultDept,
                            "Block A, Faculty Hall"
                    );
                    facultyRepository.save(faculty);
                }
            }
        } catch (Exception ex) {
            System.err.println("Warning: Could not auto-link student/faculty profile: " + ex.getMessage());
        }

        return verifiedUser;
    }
}