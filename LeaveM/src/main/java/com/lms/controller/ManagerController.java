package com.lms.controller;

import com.lms.dto.LeaveRequestDto;
import com.lms.enums.Role;
import com.lms.service.ManagerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    @Autowired
    private ManagerService managerService;
    @Autowired
    private HttpSession httpSession;

    private void checkManagerRole() {
        Long userId = (Long) httpSession.getAttribute("userId");
        Role userRole = (Role) httpSession.getAttribute("userRole");
        if (userId == null || (userRole != Role.MANAGER && userRole != Role.ADMIN)) { // Admins can also manage
            throw new SecurityException("Access Denied. Manager role required.");
        }
    }

    @GetMapping("/leave-requests")
    public ResponseEntity<List<LeaveRequestDto>> getSubordinateLeaveRequests() {
        checkManagerRole();
        Long managerId = (Long) httpSession.getAttribute("userId");
        return ResponseEntity.ok(managerService.getSubordinateLeaveRequests(managerId));
    }
    
    @GetMapping("/team-leave-history")
    public ResponseEntity<List<LeaveRequestDto>> getSubordinateLeaveHistory() {
        checkManagerRole();
        Long managerId = (Long) httpSession.getAttribute("userId");
        return ResponseEntity.ok(managerService.getSubordinateLeaveHistory(managerId));
    }

    @PutMapping("/leave-requests/{leaveId}/approve")
    public ResponseEntity<LeaveRequestDto> approveLeaveRequest(@PathVariable Long leaveId) {
        checkManagerRole();
        Long managerId = (Long) httpSession.getAttribute("userId");
        return ResponseEntity.ok(managerService.approveLeaveRequest(managerId, leaveId));
    }

    @PutMapping("/leave-requests/{leaveId}/reject")
    public ResponseEntity<LeaveRequestDto> rejectLeaveRequest(@PathVariable Long leaveId) {
        checkManagerRole();
        Long managerId = (Long) httpSession.getAttribute("userId");
        return ResponseEntity.ok(managerService.rejectLeaveRequest(managerId, leaveId));
    }
}