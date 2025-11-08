package com.lms.service.impl;

import com.lms.dto.LeaveRequestDto;
import com.lms.entity.Holiday;
import com.lms.entity.LeaveRequest;
import com.lms.entity.User;
import com.lms.enums.LeaveStatus;
import com.lms.exception.ResourceNotFoundException;
import com.lms.mapper.EntityDtoMapper;
import com.lms.repository.HolidayRepository;
import com.lms.repository.LeaveRequestRepository;
import com.lms.repository.UserRepository;
import com.lms.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveServiceImpl implements LeaveService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HolidayRepository holidayRepository;
    @Autowired
    private EntityDtoMapper mapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public LeaveRequestDto applyForLeave(Long userId, LeaveRequestDto leaveRequestDto, MultipartFile document) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateLeaveRequest(leaveRequestDto, userId);

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setUser(user);
        leaveRequest.setLeaveType(leaveRequestDto.getLeaveType());
        leaveRequest.setStartDate(leaveRequestDto.getStartDate());
        leaveRequest.setEndDate(leaveRequestDto.getEndDate());
        leaveRequest.setReason(leaveRequestDto.getReason());
        leaveRequest.setHalfDay(leaveRequestDto.isHalfDay());
        leaveRequest.setStatus(LeaveStatus.PENDING);

        if (document != null && !document.isEmpty()) {
            handleFileUpload(document, user, leaveRequest);
        }

        LeaveRequest savedLeave = leaveRequestRepository.save(leaveRequest);
        return mapper.mapLeaveRequestToDto(savedLeave);
    }

    @Override
    @Transactional
    public LeaveRequestDto editLeave(Long userId, Long leaveId, LeaveRequestDto leaveRequestDto, MultipartFile document) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));

        if (!leaveRequest.getUser().getId().equals(userId)) {
            throw new SecurityException("You are not authorized to edit this leave request.");
        }
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Only pending leave requests can be edited.");
        }

        validateLeaveRequest(leaveRequestDto, userId);

        leaveRequest.setLeaveType(leaveRequestDto.getLeaveType());
        leaveRequest.setStartDate(leaveRequestDto.getStartDate());
        leaveRequest.setEndDate(leaveRequestDto.getEndDate());
        leaveRequest.setReason(leaveRequestDto.getReason());
        leaveRequest.setHalfDay(leaveRequestDto.isHalfDay());

        if (document != null && !document.isEmpty()) {
            handleFileUpload(document, leaveRequest.getUser(), leaveRequest);
        }

        LeaveRequest updatedLeave = leaveRequestRepository.save(leaveRequest);
        return mapper.mapLeaveRequestToDto(updatedLeave);
    }

    @Override
    @Transactional
    public LeaveRequestDto cancelLeave(Long userId, Long leaveId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));

        if (!leaveRequest.getUser().getId().equals(userId)) {
            throw new SecurityException("You are not authorized to cancel this leave request.");
        }
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Leave request cannot be cancelled as it has already been " + leaveRequest.getStatus().toString().toLowerCase());
        }

        leaveRequest.setStatus(LeaveStatus.CANCELLED);
        LeaveRequest updatedLeave = leaveRequestRepository.save(leaveRequest);
        return mapper.mapLeaveRequestToDto(updatedLeave);
    }

    @Override
    public LeaveRequestDto getLeaveById(Long leaveId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + leaveId));
        return mapper.mapLeaveRequestToDto(leaveRequest);
    }

    @Override
    public List<LeaveRequestDto> getLeavesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return leaveRequestRepository.findByUser(user)
                .stream()
                .map(mapper::mapLeaveRequestToDto)
                .collect(Collectors.toList());
    }
    


    private void validateLeaveRequest(LeaveRequestDto dto, Long userId) {
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();

        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before the start date.");
        }
        if (dto.isHalfDay() && !startDate.equals(endDate)) {
            throw new IllegalArgumentException("Half-day leave can only be applied for a single day.");
        }

        List<LocalDate> holidays = holidayRepository.findAll().stream().map(Holiday::getDate).toList();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            DayOfWeek day = currentDate.getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                throw new IllegalArgumentException("Leave cannot be applied on a weekend: " + currentDate);
            }
            if (holidays.contains(currentDate)) {
                throw new IllegalArgumentException("Leave cannot be applied on a public holiday: " + currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean isOverlapping = leaveRequestRepository.findOverlappingLeaves(user, startDate, endDate);

        if (isOverlapping) {
            throw new IllegalStateException("The selected dates overlap with an existing leave request.");
        }
    }

    private void handleFileUpload(MultipartFile document, User user, LeaveRequest leaveRequest) {
        try {
            String fileName = user.getId() + "_" + System.currentTimeMillis() + "_" + document.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.createDirectories(filePath.getParent());
            Files.copy(document.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            leaveRequest.setDocumentPath(fileName);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
    
    public long calculateLeaveDuration(LocalDate start, LocalDate end) {
        long totalDays = 0;
        LocalDate currentDate = start;
        while (!currentDate.isAfter(end)) {
            DayOfWeek day = currentDate.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                totalDays++;
            }
            currentDate = currentDate.plusDays(1);
        }
        return totalDays;
    }
}