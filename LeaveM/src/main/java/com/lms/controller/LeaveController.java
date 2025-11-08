package com.lms.controller;

import com.lms.dto.LeaveRequestDto;
import com.lms.service.LeaveService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lms.enums.LeaveType; 
import org.springframework.web.multipart.MultipartFile; 
import java.time.LocalDate; 

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private HttpSession httpSession;

    private Long getUserIdFromSession() {
        Long userId = (Long) httpSession.getAttribute("userId");
        if (userId == null) {
            throw new SecurityException("Unauthorized. Please log in.");
        }
        return userId;
    }

    @PostMapping("/apply")
    public ResponseEntity<LeaveRequestDto> applyForLeave(
            @RequestParam("leaveType") LeaveType leaveType,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam("isHalfDay") boolean isHalfDay,
            @RequestParam("reason") String reason,
            @RequestParam(value = "document", required = false) MultipartFile document) {
        
        Long userId = getUserIdFromSession();

       
        LeaveRequestDto leaveRequestDto = new LeaveRequestDto();
        leaveRequestDto.setLeaveType(leaveType);
        leaveRequestDto.setStartDate(startDate);
        leaveRequestDto.setEndDate(endDate);
        leaveRequestDto.setHalfDay(isHalfDay);
        leaveRequestDto.setReason(reason);

        
        LeaveRequestDto createdLeave = leaveService.applyForLeave(userId, leaveRequestDto, document);
        
        return new ResponseEntity<>(createdLeave, HttpStatus.CREATED);
    }

    @GetMapping("/my-leaves")
    public ResponseEntity<List<LeaveRequestDto>> getMyLeaves() {
        Long userId = getUserIdFromSession();
        List<LeaveRequestDto> leaves = leaveService.getLeavesByUserId(userId);
        return ResponseEntity.ok(leaves);
    }

    @PutMapping("/{leaveId}/cancel")
    public ResponseEntity<LeaveRequestDto> cancelLeave(@PathVariable Long leaveId) {
        Long userId = getUserIdFromSession();
        LeaveRequestDto cancelledLeave = leaveService.cancelLeave(userId, leaveId);
        return ResponseEntity.ok(cancelledLeave);
    }


    @GetMapping("/{leaveId}")
    public ResponseEntity<LeaveRequestDto> getLeaveById(@PathVariable Long leaveId) {
        
        return ResponseEntity.ok(leaveService.getLeaveById(leaveId));
    }

    @PutMapping("/{leaveId}")
    public ResponseEntity<LeaveRequestDto> editLeave(
            @PathVariable Long leaveId,
            @RequestParam("leaveType") LeaveType leaveType,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam("isHalfDay") boolean isHalfDay,
            @RequestParam("reason") String reason,
            @RequestParam(value = "document", required = false) MultipartFile document) {

        Long userId = getUserIdFromSession();

        
        LeaveRequestDto leaveRequestDto = new LeaveRequestDto();
        leaveRequestDto.setLeaveType(leaveType);
        leaveRequestDto.setStartDate(startDate);
        leaveRequestDto.setEndDate(endDate);
        leaveRequestDto.setHalfDay(isHalfDay);
        leaveRequestDto.setReason(reason);

      
        LeaveRequestDto updatedLeave = leaveService.editLeave(userId, leaveId, leaveRequestDto, document);

        return ResponseEntity.ok(updatedLeave);
    }
    
}