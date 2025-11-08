package com.lms.dto;

import com.lms.enums.LeaveType;
import lombok.Data;

@Data
public class LeaveBalanceDto {
    private Long userId;
    private LeaveType leaveType;
    private int year;
    private double totalDays;
    private double remainingDays;
}