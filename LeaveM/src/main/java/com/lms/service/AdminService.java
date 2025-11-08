package com.lms.service;

import com.lms.dto.LeaveRequestDto;
import com.lms.dto.UserCreateDto;
import com.lms.dto.UserDto;
import com.lms.entity.User;

import java.util.List;

public interface AdminService {
    UserDto createUser(UserCreateDto userCreateDto);
    List<UserDto> getAllUsers();
    List<LeaveRequestDto> getAllLeaveRequests();
    void deleteUser(Long userId);
    UserDto updateUser(Long userId, UserCreateDto userUpdateDto);
}