package com.lms.controller;

import com.lms.dto.*;
import com.lms.entity.Holiday;
import com.lms.enums.Role;
import com.lms.service.AdminService;
import com.lms.service.HolidayService;
import com.lms.service.LeaveBalanceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private HolidayService holidayService;
    @Autowired
    private LeaveBalanceService leaveBalanceService;
    @Autowired
    private HttpSession httpSession;

    private void checkAdminRole() {
        Role userRole = (Role) httpSession.getAttribute("userRole");
        if (userRole != Role.ADMIN) {
            throw new SecurityException("Access Denied: ADMIN role required.");
        }
    }

    // User Management
    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@RequestBody UserCreateDto userCreateDto) {
        checkAdminRole();
        return new ResponseEntity<>(adminService.createUser(userCreateDto), HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        checkAdminRole();
        return ResponseEntity.ok(adminService.getAllUsers());
    }
    
    @PutMapping("/users/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserCreateDto userUpdateDto) {
        checkAdminRole();
        UserDto updatedUser = adminService.updateUser(userId, userUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        checkAdminRole();
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build(); // Standard response for successful DELETE
    }

    // Holiday Management
    @PostMapping("/holidays")
    public ResponseEntity<Holiday> createHoliday(@RequestBody HolidayDto holidayDto) {
        checkAdminRole();
        return new ResponseEntity<>(holidayService.createHoliday(holidayDto), HttpStatus.CREATED);
    }

    @GetMapping("/holidays")
    public ResponseEntity<List<Holiday>> getAllHolidays() {
        checkAdminRole();
        return ResponseEntity.ok(holidayService.getAllHolidays());
    }
    
    @DeleteMapping("/holidays/{holidayId}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable Long holidayId) {
        checkAdminRole();
        holidayService.deleteHoliday(holidayId);
        return ResponseEntity.noContent().build();
    }

    // Leave Balance Management
    @PutMapping("/leave-balances")
    public ResponseEntity<LeaveBalanceDto> setLeaveBalance(@RequestBody LeaveBalanceDto leaveBalanceDto) {
        checkAdminRole();
        return ResponseEntity.ok(leaveBalanceService.setLeaveBalance(leaveBalanceDto));
    }

    @GetMapping("/users/{userId}/leave-balances")
    public ResponseEntity<List<LeaveBalanceDto>> getLeaveBalances(@PathVariable Long userId) {
        checkAdminRole();
        return ResponseEntity.ok(leaveBalanceService.getLeaveBalancesByUserId(userId));
    }

    // View All Leave Requests
    @GetMapping("/leaves")
    public ResponseEntity<List<LeaveRequestDto>> getAllLeaveRequests() {
        checkAdminRole();
        return ResponseEntity.ok(adminService.getAllLeaveRequests());
    }
}