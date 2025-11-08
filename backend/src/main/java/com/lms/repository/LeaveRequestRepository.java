package com.lms.repository;

import com.lms.entity.LeaveRequest;
import com.lms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByUser(User user);
    
    @Query("SELECT CASE WHEN COUNT(lr) > 0 THEN true ELSE false END " +
            "FROM LeaveRequest lr " +
            "WHERE lr.user = ?1 " +
            "AND lr.status IN ('PENDING', 'APPROVED') " +
            "AND lr.startDate <= ?3 " +
            "AND lr.endDate >= ?2")
     boolean findOverlappingLeaves(User user, LocalDate newStartDate, LocalDate newEndDate);
    
    
}