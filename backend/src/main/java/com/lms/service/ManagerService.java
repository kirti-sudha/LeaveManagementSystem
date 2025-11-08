package com.lms.service;

import com.lms.dto.LeaveRequestDto;
import java.util.List;

public interface ManagerService {

    List<LeaveRequestDto> getSubordinateLeaveRequests(Long managerId);

    List<LeaveRequestDto> getSubordinateLeaveHistory(Long managerId);

    LeaveRequestDto approveLeaveRequest(Long managerId, Long leaveId);
    LeaveRequestDto rejectLeaveRequest(Long managerId, Long leaveId);
}