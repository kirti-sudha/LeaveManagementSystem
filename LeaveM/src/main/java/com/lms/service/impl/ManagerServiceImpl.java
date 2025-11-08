package com.lms.service.impl;

import com.lms.dto.LeaveRequestDto;
import com.lms.entity.LeaveBalance;
import com.lms.entity.LeaveRequest;
import com.lms.entity.User;
import com.lms.enums.LeaveStatus;
import com.lms.enums.LeaveType;
import com.lms.exception.ResourceNotFoundException;
import com.lms.mapper.EntityDtoMapper;
import com.lms.repository.LeaveBalanceRepository;
import com.lms.repository.LeaveRequestRepository;
import com.lms.repository.UserRepository;
import com.lms.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;
    @Autowired
    private LeaveServiceImpl leaveService;
    @Autowired
    private EntityDtoMapper mapper;

    @Override
    public List<LeaveRequestDto> getSubordinateLeaveRequests(Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        List<User> subordinates = userRepository.findAll().stream()
                .filter(user -> user.getReportingManager() != null && user.getReportingManager().getId().equals(managerId))
                .toList();

        return leaveRequestRepository.findAll().stream()
                .filter(lr -> subordinates.contains(lr.getUser()) && lr.getStatus() == LeaveStatus.PENDING)
                .map(mapper::mapLeaveRequestToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveRequestDto> getSubordinateLeaveHistory(Long managerId) {
        User manager = userRepository.findById(managerId)
            .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        List<User> subordinates = userRepository.findAll().stream()
            .filter(user -> user.getReportingManager() != null && user.getReportingManager().getId().equals(managerId))
            .toList();

        return leaveRequestRepository.findAll().stream()
            .filter(lr -> subordinates.contains(lr.getUser()))
            .map(mapper::mapLeaveRequestToDto)
            .collect(Collectors.toList());
    }

    @Override
    public LeaveRequestDto approveLeaveRequest(Long managerId, Long leaveId) {
        return updateLeaveStatus(managerId, leaveId, LeaveStatus.APPROVED);
    }

    @Override
    public LeaveRequestDto rejectLeaveRequest(Long managerId, Long leaveId) {
        return updateLeaveStatus(managerId, leaveId, LeaveStatus.REJECTED);
    }

    @Transactional
    private LeaveRequestDto updateLeaveStatus(Long managerId, Long leaveId, LeaveStatus newStatus) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));

        if (leaveRequest.getUser().getReportingManager() == null || !leaveRequest.getUser().getReportingManager().getId().equals(managerId)) {
            throw new SecurityException("You are not authorized to action this leave request.");
        }
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