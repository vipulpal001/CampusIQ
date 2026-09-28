package com.campusiq.campusiq.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.campusiq.campusiq.model.User;
import com.campusiq.campusiq.repository.FacultyRepository;
import com.campusiq.campusiq.repository.StudentRepository;
import com.campusiq.campusiq.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                    StudentRepository studentRepository,
                                    FacultyRepository facultyRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String input)
            throws UsernameNotFoundException {

        if (input == null || input.trim().isEmpty()) {
            throw new UsernameNotFoundException("Please provide a username, email, or institutional ID.");
        }

        String searchKey = input.trim();

        // 1. Direct Lookup by Username (case-insensitive)
        Optional<User> userOpt = userRepository.findByUsernameIgnoreCase(searchKey);

        // 2. Direct Lookup by Email (case-insensitive)
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmailIgnoreCase(searchKey);
        }

        // 3. Fallback: Lookup by Student Roll Number or Mobile Phone
        if (userOpt.isEmpty()) {
            userOpt = studentRepository.findByRollNumber(searchKey)
                    .flatMap(s -> userRepository.findByEmailIgnoreCase(s.getEmail()));
        }
        if (userOpt.isEmpty()) {
            userOpt = studentRepository.findByPhoneNumber(searchKey)
                    .flatMap(s -> userRepository.findByEmailIgnoreCase(s.getEmail()));
        }

        // 4. Fallback: Lookup by Faculty Employee Code or Mobile Phone
        if (userOpt.isEmpty()) {
            userOpt = facultyRepository.findByEmployeeCode(searchKey)
                    .flatMap(f -> userRepository.findByEmailIgnoreCase(f.getEmail()));
        }
        if (userOpt.isEmpty()) {
            userOpt = facultyRepository.findByPhoneNumber(searchKey)
                    .flatMap(f -> userRepository.findByEmailIgnoreCase(f.getEmail()));
        }

        User user = userOpt.orElseThrow(() ->
                new UsernameNotFoundException("Account not found for: " + searchKey)
        );

        // Standardize authority name (guarantees ROLE_ prefix without duplicates)
        String rawRole = (user.getRole() != null && !user.getRole().isBlank())
                ? user.getRole().trim().toUpperCase()
                : "STUDENT";
        if (rawRole.startsWith("ROLE_")) {
            rawRole = rawRole.substring(5);
        }
        String springSecurityRole = "ROLE_" + rawRole;
        GrantedAuthority authority = new SimpleGrantedAuthority(springSecurityRole);

        // Seed accounts & Admin users are exempt from email verification blocks
        boolean isExempt = "admin".equalsIgnoreCase(user.getUsername())
                || "faculty".equalsIgnoreCase(user.getUsername())
                || "student".equalsIgnoreCase(user.getUsername())
                || "ROLE_ADMIN".equals(springSecurityRole);

        boolean isEnabled = user.isVerified() || isExempt;

        // Return Spring Security user with proper enabled flag
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                isEnabled, // enabled
                true,      // accountNonExpired
                true,      // credentialsNonExpired
                true,      // accountNonLocked
                Collections.singletonList(authority)
        );
    }
}