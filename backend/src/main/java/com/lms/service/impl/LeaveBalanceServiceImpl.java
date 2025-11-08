package com.lms.service.impl;

import com.lms.dto.LeaveBalanceDto;
import com.lms.entity.LeaveBalance;
import com.lms.entity.LeaveRequest;
import com.lms.entity.User;
import com.lms.exception.ResourceNotFoundException;
import com.lms.repository.LeaveBalanceRepository;
import com.lms.repository.UserRepository;
import com.lms.service.LeaveBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public LeaveBalanceDto setLeaveBalance(LeaveBalanceDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LeaveBalance leaveBalance = leaveBalanceRepository
                .findByUserAndLeaveTypeAndYear(user, dto.getLeaveType(), dto.getYear())
                .orElse(new LeaveBalance()); // Create new if not exists

        leaveBalance.setUser(user);
        leaveBalance.setLeaveType(dto.getLeaveType());
        leaveBalance.setYear(dto.getYear());
        leaveBalance.setTotalDays(dto.getTotalDays());

        leaveBalance.setRemainingDays(dto.getTotalDays());

        LeaveBalance savedBalance = leaveBalanceRepository.save(leaveBalance);
        return mapToDto(savedBalance);
    }

    @Override
    public List<LeaveBalanceDto> getLeaveBalancesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return leaveBalanceRepository.findByUser(user)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private LeaveBalanceDto mapToDto(LeaveBalance leaveBalance) {
        LeaveBalanceDto dto = new LeaveBalanceDto();
        dto.setUserId(leaveBalance.getUser().getId());
        dto.setLeaveType(leaveBalance.getLeaveType());
        dto.setYear(leaveBalance.getYear());
        dto.setTotalDays(leaveBalance.getTotalDays());
        dto.setRemainingDays(leaveBalance.getRemainingDays()); // <-- MAKE SURE THIS LINE IS HERE
        return dto;
    }
    

    
    
}