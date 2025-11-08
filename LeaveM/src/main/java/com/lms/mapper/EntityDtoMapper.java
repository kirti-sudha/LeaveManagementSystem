package com.lms.mapper;

import com.lms.dto.LeaveRequestDto;
import com.lms.dto.UserDto;
import com.lms.entity.LeaveRequest;
import com.lms.entity.User;
import org.springframework.stereotype.Component;

@Component
public class EntityDtoMapper {

    public UserDto mapUserToUserDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmployeeId(user.getEmployeeId());
        dto.setOfficialEmail(user.getOfficialEmail());
        dto.setRole(user.getRole());
        dto.setDepartment(user.getDepartment());
        dto.setDesignation(user.getDesignation());

        if (user.getReportingManager() != null) {
            dto.setReportingManagerName(user.getReportingManager().getName());
        } else {
            dto.setReportingManagerName("N/A");
        }

        return dto;
    }


    public LeaveRequestDto mapLeaveRequestToDto(LeaveRequest leaveRequest) {
        if (leaveRequest == null) {
            return null;
        }

        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setId(leaveRequest.getId());
        dto.setUserId(leaveRequest.getUser().getId());
        dto.setEmployeeName(leaveRequest.getUser().getName());
        dto.setLeaveType(leaveRequest.getLeaveType());
        dto.setStartDate(leaveRequest.getStartDate());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setHalfDay(leaveRequest.isHalfDay());
        dto.setReason(leaveRequest.getReason());
        dto.setStatus(leaveRequest.getStatus());
        dto.setDocumentPath(leaveRequest.getDocumentPath());

        return dto;
    }
}