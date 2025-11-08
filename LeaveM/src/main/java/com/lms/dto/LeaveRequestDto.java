package com.lms.dto;

import com.lms.enums.LeaveStatus;
import com.lms.enums.LeaveType;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LeaveRequestDto {
    private Long id;
    private Long userId;
    private String employeeName;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isHalfDay;
    private String reason;
    private LeaveStatus status;
    private String documentPath;
}