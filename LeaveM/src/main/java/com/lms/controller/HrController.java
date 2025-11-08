package com.lms.controller;

import com.lms.dto.LeaveRequestDto;
import com.lms.dto.UserDto;
import com.lms.enums.Role;
import com.lms.service.HrService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hr")
public class HrController {

    @Autowired
    private HrService hrService;
    @Autowired
    private HttpSession httpSession;

    private void checkHrRole() {
        Role userRole = (Role) httpSession.getAttribute("userRole");
        if (userRole != Role.HR && userRole != Role.ADMIN) { // Admins can also do HR tasks
            throw new SecurityException("Access Denied. HR role required.");
        }
    }

    @GetMapping("/leaves")
    public ResponseEntity<List<LeaveRequestDto>> getAllLeaveRequests() {
        checkHrRole();
        return ResponseEntity.ok(hrService.getAllLeaveRequests());
    }

    @PutMapping("/leaves/{leaveId}/approve")
    public ResponseEntity<LeaveRequestDto> approveLeave(@PathVariable Long leaveId) {
        checkHrRole();
        return ResponseEntity.ok(hrService.approveLeaveRequest(leaveId));
    }

    @PutMapping("/leaves/{leaveId}/reject")
    public ResponseEntity<LeaveRequestDto> rejectLeave(@PathVariable Long leaveId) {
        checkHrRole();
        return ResponseEntity.ok(hrService.rejectLeaveRequest(leaveId));
    }
    
    @GetMapping("/employees")
    public ResponseEntity<List<UserDto>> getAllEmployeesAndManagers() {
        checkHrRole();
        return ResponseEntity.ok(hrService.getAllEmployeesAndManagers());
    }
}