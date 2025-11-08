package com.lms.service.impl;

import com.lms.dto.HolidayDto;
import com.lms.entity.Holiday;
import com.lms.exception.ResourceNotFoundException;
import com.lms.repository.HolidayRepository;
import com.lms.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HolidayServiceImpl implements HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    @Override
    public List<Holiday> getAllHolidays() {
        return holidayRepository.findAll();
    }
    @Override
    public Holiday createHoliday(HolidayDto holidayDto) {
        if (holidayRepository.existsByDate(holidayDto.getDate())) {
            throw new IllegalArgumentException("A holiday on this date already exists.");
        }
        Holiday holiday = new Holiday();
        holiday.setName(holidayDto.getName());
        holiday.setDate(holidayDto.getDate());
        return holidayRepository.save(holiday);
    }
    @Override
    public void deleteHoliday(Long holidayId) {
        if (!holidayRepository.existsById(holidayId)) {
            throw new ResourceNotFoundException("Holiday not found with id: " + holidayId);
        }
        holidayRepository.deleteById(holidayId);
    }
}