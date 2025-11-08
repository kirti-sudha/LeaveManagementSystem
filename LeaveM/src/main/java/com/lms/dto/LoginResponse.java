package com.lms.dto;

import com.lms.enums.Role;
import lombok.Data;

@Data
public class LoginResponse {
    private Long userId;
    private String name;
    private String employeeId;
    private Role role;
    private String message;
}