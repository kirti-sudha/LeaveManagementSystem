package com.lms.entity;

import com.lms.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String employeeId; 

    @Column(unique = true, nullable = false)
    private String officialEmail; 

    @Column(nullable = false)
    private String password; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

 
    private String department;
    private String designation;
    private LocalDate dateOfJoining;


    private String profilePicturePath;
    private String phoneNumber;
    private String address;
    private String emergencyContact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporting_manager_id")
    private User reportingManager;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LeaveBalance> leaveBalances;
}