package com.lms.repository;

import com.lms.entity.LeaveBalance;
import com.lms.entity.User;
import com.lms.enums.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
   
    List<LeaveBalance> findByUser(User user);

    
    Optional<LeaveBalance> findByUserAndLeaveTypeAndYear(User user, LeaveType leaveType, int year);
}