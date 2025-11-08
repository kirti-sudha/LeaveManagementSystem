package com.lms.dto;

import com.lms.enums.Role;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UserProfileDto {
    // Non-editable fields (for display)
    private String employeeId;
    private String department;
    private String designation;
    private String reportingManagerName;
    private Role role;
    private String officialEmail;
    private LocalDate dateOfJoining;

    // Editable fields
    private String name; // Name should be editable
    private String password; // For changing password
    private String phoneNumber;
    private String address;
    private String emergencyContact;
    // We will handle profile picture uploads separately
}