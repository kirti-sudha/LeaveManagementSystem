package com.lms.service.impl;

import com.lms.dto.LeaveRequestDto;

import com.lms.dto.UserCreateDto;
import com.lms.dto.UserDto;
import com.lms.entity.User;
import com.lms.exception.ResourceNotFoundException;
import com.lms.mapper.EntityDtoMapper;
import com.lms.repository.LeaveRequestRepository;
import com.lms.repository.UserRepository;
import com.lms.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.lms.entity.LeaveBalance; // <-- Add import
import com.lms.entity.LeaveRequest;
import com.lms.enums.LeaveType; // <-- Add import
import com.lms.repository.LeaveBalanceRepository; // <-- Add import
import java.time.LocalDate; // <-- Add import
import java.util.Arrays; // <-- Add import
import org.springframework.transaction.annotation.Transactional;
import com.lms.dto.UserDto;
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    
    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private EntityDtoMapper mapper; 

    @Override
    public UserDto createUser(UserCreateDto userCreateDto) {
        User user = new User();
        user.setName(userCreateDto.getName());
        user.setEmployeeId(userCreateDto.getEmployeeId());
        user.setOfficialEmail(userCreateDto.getOfficialEmail());
        user.setPassword(userCreateDto.getPassword());
        user.setRole(userCreateDto.getRole());
        user.setDepartment(userCreateDto.getDepartment());
        user.setDesignation(userCreateDto.getDesignation());
        user.setDateOfJoining(userCreateDto.getDateOfJoining());

        if (userCreateDto.getReportingManagerId() != null) {
            User manager = userRepository.findById(userCreateDto.getReportingManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reporting Manager not found"));
            user.setReportingManager(manager);
        }

        User savedUser = userRepository.save(user);
        initializeLeaveBalancesForNewUser(savedUser);

        return mapper.mapUserToUserDto(savedUser); // Used the central mapper
    }
    
    private void initializeLeaveBalancesForNewUser(User user) {
        int currentYear = LocalDate.now().getYear();

        Arrays.stream(LeaveType.values()).forEach(leaveType -> {
            
            boolean balanceExists = leaveBalanceRepository.findByUserAndLeaveTypeAndYear(user, leaveType, currentYear).isPresent();

            if (!balanceExists) {
                LeaveBalance balance = new LeaveBalance();
                balance.setUser(user);
                balance.setLeaveType(leaveType);
                balance.setYear(currentYear);

                switch (leaveType) {
                    case CASUAL: balance.setTotalDays(12); break;
                    case SICK: balance.setTotalDays(10); break;
                    case EARNED: balance.setTotalDays(15); break;
                    default: balance.setTotalDays(0); break;
                }
                balance.setRemainingDays(balance.getTotalDays());
                leaveBalanceRepository.save(balance);
            }
        });
    }
    
    @Override
    @Transactional 
    public void deleteUser(Long userId) {
        User userToDelete = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        
        List<LeaveRequest> userLeaveRequests = leaveRequestRepository.findByUser(userToDelete);
        if (!userLeaveRequests.isEmpty()) {
            leaveRequestRepository.deleteAll(userLeaveRequests);
        }
        
        
        List<LeaveBalance> userLeaveBalances = leaveBalanceRepository.findByUser(userToDelete);
        if(!userLeaveBalances.isEmpty()){
            leaveBalanceRepository.deleteAll(userLeaveBalances);
        }

        
        List<User> subordinates = userRepository.findAll().stream()
            .filter(u -> u.getReportingManager() != null && u.getReportingManager().getId().equals(userId))
            .toList();

        if (!subordinates.isEmpty()) {
            for (User subordinate : subordinates) {
                subordinate.setReportingManager(null);
            }
            userRepository.saveAll(subordinates);
        }

        
        userRepository.delete(userToDelete);
    }

 
    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserCreateDto userUpdateDto) {
        User userToUpdate = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));


        userToUpdate.setName(userUpdateDto.getName());
        userToUpdate.setEmployeeId(userUpdateDto.getEmployeeId());
        userToUpdate.setOfficialEmail(userUpdateDto.getOfficialEmail());
        userToUpdate.setRole(userUpdateDto.getRole());
        userToUpdate.setDepartment(userUpdateDto.getDepartment());
        userToUpdate.setDesignation(userUpdateDto.getDesignation());
        userToUpdate.setDateOfJoining(userUpdateDto.getDateOfJoining());


        if (userUpdateDto.getPassword() != null && !userUpdateDto.getPassword().isEmpty()) {
            userToUpdate.setPassword(userUpdateDto.getPassword());
        }


        if (userUpdateDto.getReportingManagerId() != null) {
            User manager = userRepository.findById(userUpdateDto.getReportingManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Reporting Manager not found"));
            userToUpdate.setReportingManager(manager);
        } else {
            userToUpdate.setReportingManager(null);
        }

        User updatedUser = userRepository.save(userToUpdate);
        return mapper.mapUserToUserDto(updatedUser);
    }
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(mapper::mapUserToUserDto) // Used the central mapper
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveRequestDto> getAllLeaveRequests() {
         return leaveRequestRepository.findAll()
                .stream()
                .map(mapper::mapLeaveRequestToDto) // Used the central mapper
                .collect(Collectors.toList());
    }
}