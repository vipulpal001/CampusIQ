package com.campusiq.campusiq.service;

import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.campusiq.campusiq.model.User;
import com.campusiq.campusiq.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + username
                        )
                );

        // ==========================================
        // EMAIL VERIFICATION CHECK
        // ==========================================

        if (!user.isVerified()) {
            throw new UsernameNotFoundException(
                    "Please verify your email before login."
            );
        }

        // ==========================================
        // USER ROLE
        // ==========================================

        GrantedAuthority authority =
                new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole()
                );

        // ==========================================
        // SPRING SECURITY USER
        // ==========================================

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
}