package com.lms.service.impl;

import com.lms.dto.LoginRequest;
import com.lms.dto.LoginResponse;
import com.lms.entity.User;
import com.lms.exception.ResourceNotFoundException;
import com.lms.repository.UserRepository;
import com.lms.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lms.dto.UserProfileDto;
import com.lms.entity.User;
import org.springframework.stereotype.Service;
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession httpSession;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        User user = userRepository.findByOfficialEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginRequest.getEmail()));


        if (!user.getPassword().equals(loginRequest.getPassword())) {
            
            throw new RuntimeException("Invalid password");
        }

        httpSession.setAttribute("userId", user.getId());
        httpSession.setAttribute("userRole", user.getRole());


        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmployeeId(user.getEmployeeId());
        response.setRole(user.getRole());
        response.setMessage("Login successful!");

        return response;
    }
    
    @Override
    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return mapUserToUserProfileDto(user);
    }

    @Override
    public UserProfileDto updateUserProfile(Long userId, UserProfileDto userProfileDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));


        user.setName(userProfileDto.getName());
        user.setPhoneNumber(userProfileDto.getPhoneNumber());
        user.setAddress(userProfileDto.getAddress());
        user.setEmergencyContact(userProfileDto.getEmergencyContact());


        if (userProfileDto.getPassword() != null && !userProfileDto.getPassword().isEmpty()) {
            user.setPassword(userProfileDto.getPassword());
        }

        User updatedUser = userRepository.save(user);
        return mapUserToUserProfileDto(updatedUser);
    }


    private UserProfileDto mapUserToUserProfileDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setEmployeeId(user.getEmployeeId());
        dto.setOfficialEmail(user.getOfficialEmail());
        dto.setName(user.getName());
        dto.setRole(user.getRole());
        dto.setDepartment(user.getDepartment());
        dto.setDesignation(user.getDesignation());
        dto.setDateOfJoining(user.getDateOfJoining());
        

        if (user.getReportingManager() != null) {
            dto.setReportingManagerName(user.getReportingManager().getName());
        } else {
            dto.setReportingManagerName("N/A");
        }

        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setEmergencyContact(user.getEmergencyContact());
        
        return dto;
    }
}
