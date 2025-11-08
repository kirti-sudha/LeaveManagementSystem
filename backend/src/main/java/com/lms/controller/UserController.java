package com.lms.controller;

import com.lms.dto.LeaveBalanceDto;
import com.lms.dto.UserProfileDto;
import com.lms.service.LeaveBalanceService;
import com.lms.service.UserService;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired

    private LeaveBalanceService leaveBalanceService; // <-- INJECT THE SERVICE


    @Autowired
    private HttpSession httpSession;

    /**
     * Helper method to check session and get user ID.
     * Throws an exception if the user is not logged in.
     */
    private Long getUserIdFromSession() {
        Long userId = (Long) httpSession.getAttribute("userId");
        if (userId == null) {
            throw new SecurityException("Unauthorized. Please log in.");
        }
        return userId;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile() {
        Long userId = getUserIdFromSession();
        UserProfileDto userProfile = userService.getUserProfile(userId);
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateUserProfile(@RequestBody UserProfileDto userProfileDto) {
        Long userId = getUserIdFromSession();
        UserProfileDto updatedProfile = userService.updateUserProfile(userId, userProfileDto);
        return ResponseEntity.ok(updatedProfile);
    }
    
    @GetMapping("/leave-balances")
    public ResponseEntity<List<LeaveBalanceDto>> getMyLeaveBalances() {
        Long userId = getUserIdFromSession();
        List<LeaveBalanceDto> balances = leaveBalanceService.getLeaveBalancesByUserId(userId);
        return ResponseEntity.ok(balances);
    }
}