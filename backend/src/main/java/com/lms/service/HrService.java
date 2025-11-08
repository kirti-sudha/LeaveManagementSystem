package com.lms.service;

import com.lms.dto.LeaveRequestDto;
import com.lms.dto.UserDto;
import java.util.List;

public interface HrService {

    List<LeaveRequestDto> getAllLeaveRequests();

    LeaveRequestDto approveLeaveRequest(Long leaveId);

    LeaveRequestDto rejectLeaveRequest(Long leaveId);

    List<UserDto> getAllEmployeesAndManagers();
}