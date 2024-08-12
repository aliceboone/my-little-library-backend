package com.capstone.mylittlelibrarybackend.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String redirectUrl;

        // Determine the redirect URL based on the environment or request origin
        String requestOrigin = request.getHeader("Origin");
        if ("http://localhost:5173".equals(requestOrigin)) {
            redirectUrl = "http://localhost:5173";
        } else {
            redirectUrl = "https://secure-harbor-00383-fe62da28a4d4.herokuapp.com";
        }
        response.sendRedirect(redirectUrl);
    }
}
