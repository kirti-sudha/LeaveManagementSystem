package com.lms.entity;

import com.lms.enums.LeaveType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class LeaveBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private int year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leaveType;

    @Column(name = "total_days")
    private double totalDays;

    @Column(name = "remaining_days")
    private double remainingDays;
}