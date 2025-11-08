package com.lms.service;

import com.lms.dto.LoginRequest;
import com.lms.dto.LoginResponse;
import com.lms.dto.UserProfileDto; // Import this

public interface UserService {

    LoginResponse login(LoginRequest loginRequest);

  
    UserProfileDto getUserProfile(Long userId);


    UserProfileDto updateUserProfile(Long userId, UserProfileDto userProfileDto);
}