package com.lms.service;

import com.lms.dto.LeaveBalanceDto;
import java.util.List;

public interface LeaveBalanceService {
    /**
     * Sets or updates the leave balance for a user.
     * @param leaveBalanceDto The details of the leave balance to set.
     * @return The updated LeaveBalanceDto.
     */
    LeaveBalanceDto setLeaveBalance(LeaveBalanceDto leaveBalanceDto);
    List<LeaveBalanceDto> getLeaveBalancesByUserId(Long userId);
}