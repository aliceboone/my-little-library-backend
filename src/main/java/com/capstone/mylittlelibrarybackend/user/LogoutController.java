package com.capstone.mylittlelibrarybackend.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/logout")
public class LogoutController {

    @PostMapping
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // Invalidate the current session if it exists
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // Clear authentication information
        SecurityContextHolder.clearContext();
        // Redirect to the login page with a query parameter indicating a successful logout
        try {
            response.sendRedirect("/login?logout=true");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
