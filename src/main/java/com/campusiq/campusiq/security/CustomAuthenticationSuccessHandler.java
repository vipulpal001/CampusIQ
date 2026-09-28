package com.campusiq.campusiq.security;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * ============================================================================
 * [CAMPUSIQ ERP]: CustomAuthenticationSuccessHandler
 * Intelligently routes authenticated users to their designated role dashboards:
 * - ADMIN -> /admin/dashboard
 * - FACULTY -> /faculty/dashboard
 * - STUDENT -> /dashboard
 * ============================================================================
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority() != null ? authority.getAuthority().toUpperCase() : "";

            if (role.contains("ADMIN")) {
                response.sendRedirect("/admin/dashboard");
                return;
            } else if (role.contains("FACULTY") || role.contains("TEACHER")) {
                response.sendRedirect("/faculty/dashboard");
                return;
            } else if (role.contains("STUDENT")) {
                response.sendRedirect("/dashboard");
                return;
            }
        }

        // Default fallback
        response.sendRedirect("/dashboard");
    }
}
