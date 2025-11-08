package com.lms.dto;

import com.lms.enums.Role;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UserCreateDto {
    private String name;
    private String employeeId;
    private String officialEmail;
    private String password;
    private Role role;
    private String department;
    private String designation;
    private LocalDate dateOfJoining;
    private Long reportingManagerId; // Can be null if the user is an admin or HR
}