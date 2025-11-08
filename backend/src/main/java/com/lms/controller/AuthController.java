package com.lms.controller;

import com.lms.dto.LoginRequest;
import com.lms.dto.LoginResponse;
import com.lms.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession httpSession;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // Invalidate the session, clearing all session attributes (like userId)
        httpSession.invalidate();
        // Return a success message
        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }
}