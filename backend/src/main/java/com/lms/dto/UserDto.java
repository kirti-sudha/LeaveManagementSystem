package com.lms.dto;

import com.lms.enums.Role;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String employeeId;
    private String officialEmail;
    private Role role;
    private String department;
    private String designation;
    private String reportingManagerName;
}