package com.lms.service.impl;

import com.lms.dto.LeaveRequestDto;
import com.lms.dto.UserDto;
import com.lms.entity.LeaveBalance;
import com.lms.entity.LeaveRequest;
import com.lms.entity.User;
import com.lms.enums.LeaveStatus;
import com.lms.enums.LeaveType;
import com.lms.enums.Role;
import com.lms.exception.ResourceNotFoundException;
import com.lms.mapper.EntityDtoMapper;
import com.lms.repository.LeaveBalanceRepository;
import com.lms.repository.LeaveRequestRepository;
import com.lms.repository.UserRepository;
import com.lms.service.HrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HrServiceImpl implements HrService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;
    @Autowired
    private LeaveServiceImpl leaveService;
    @Autowired
    private EntityDtoMapper mapper;

    @Override
    public List<LeaveRequestDto> getAllLeaveRequests() {
        return leaveRequestRepository.findAll().stream()
                .filter(lr -> lr.getUser().getRole() != Role.ADMIN)
                .map(mapper::mapLeaveRequestToDto)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveRequestDto approveLeaveRequest(Long leaveId) {
        return updateLeaveStatus(leaveId, LeaveStatus.APPROVED);
    }

    @Override
    public LeaveRequestDto rejectLeaveRequest(Long leaveId) {
        return updateLeaveStatus(leaveId, LeaveStatus.REJECTED);
    }

    @Override
    public List<UserDto> getAllEmployeesAndManagers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.EMPLOYEE || user.getRole() == Role.MANAGER)
                .map(mapper::mapUserToUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    private LeaveRequestDto updateLeaveStatus(Long leaveId, LeaveStatus newStatus) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("This leave request has already been actioned.");
        }

        if (newStatus == LeaveStatus.APPROVED) {
            User user = leaveRequest.getUser();
            LeaveType leaveType = leaveRequest.getLeaveType();
            int year = leaveRequest.getStartDate().getYear();
            LeaveBalance balance = leaveBalanceRepository.findByUserAndLeaveTypeAndYear(user, leaveType, year)
                .orElseThrow(() -> new IllegalStateException("Leave balance not configured for this user and leave type."));

            double durationToDeduct;
            if (leaveRequest.isHalfDay()) {
                durationToDeduct = 0.5;
            } else {
                durationToDeduct = leaveService.calculateLeaveDuration(leaveRequest.getStartDate(), leaveRequest.getEndDate());
            }

            if (balance.getRemainingDays() < durationToDeduct) {
                throw new IllegalStateException("Insufficient leave balance.");
            }

            balance.setRemainingDays(balance.getRemainingDays() - durationToDeduct);
            leaveBalanceRepository.save(balance);
        }

        leaveRequest.setStatus(newStatus);
        LeaveRequest updatedRequest = leaveRequestRepository.save(leaveRequest);
        return mapper.mapLeaveRequestToDto(updatedRequest);
    }
}