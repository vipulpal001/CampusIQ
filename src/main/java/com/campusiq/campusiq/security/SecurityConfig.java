package com.campusiq.campusiq.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.campusiq.campusiq.service.CustomUserDetailsService;

/**
 * ============================================================================
 * [CAMPUSIQ ERP SECURITY]: SecurityConfig
 * Enforces strict role-based access control (RBAC):
 * - /admin/**     -> Strictly ROLE_ADMIN
 * - /faculty/**   -> ROLE_FACULTY & ROLE_ADMIN
 * - /dashboard/** -> ROLE_STUDENT, ROLE_FACULTY, ROLE_ADMIN
 * ============================================================================
 */
@Configuration
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomAuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   DaoAuthenticationProvider authenticationProvider) throws Exception {

        http
            .csrf(org.springframework.security.config.Customizer.withDefaults())
            .authenticationProvider(authenticationProvider)
            .authorizeHttpRequests(auth -> auth
                // Public Static Assets
                .requestMatchers(
                    "/style.css",
                    "/script.js",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/Img Assist/**",
                    "/Img%20Assist/**",
                    "/Models/**",
                    "/models/**",
                    "/favicon.ico"
                ).permitAll()

                // Public Registration & Login Endpoints
                .requestMatchers(
                    "/",
                    "/login",
                    "/register",
                    "/verify-otp",
                    "/resend-otp",
                    "/access-denied"
                ).permitAll()

                // ============================================================
                // STRICT ROLE-BASED ACCESS CONTROL (RBAC)
                // ============================================================
                // 1. ADMIN ONLY: Students and Faculty CANNOT access
                .requestMatchers("/admin/**", "/courses/**", "/students/**").hasRole("ADMIN")

                // 2. FACULTY & ADMIN: Students CANNOT access
                .requestMatchers("/faculty/**").hasAnyRole("FACULTY", "ADMIN")

                // 3. STUDENT PORTAL & ATTENDANCE
                .requestMatchers("/dashboard/**", "/student/**", "/attendance/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN")

                // Require authentication for any other endpoint
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )

            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
            );

        return http.build();
    }
}