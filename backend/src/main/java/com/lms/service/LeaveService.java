package com.lms.service;

import com.lms.dto.LeaveRequestDto;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface LeaveService {

    LeaveRequestDto applyForLeave(Long userId, LeaveRequestDto leaveRequestDto, MultipartFile document);
    
    List<LeaveRequestDto> getLeavesByUserId(Long userId);

    LeaveRequestDto cancelLeave(Long userId, Long leaveId);
    LeaveRequestDto editLeave(Long userId, Long leaveId, LeaveRequestDto leaveRequestDto, MultipartFile document);

    LeaveRequestDto getLeaveById(Long leaveId);
    
    
    
}