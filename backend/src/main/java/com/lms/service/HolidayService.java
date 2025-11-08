package com.lms.service;

import com.lms.dto.HolidayDto; 
import com.lms.entity.Holiday;
import java.util.List;

public interface HolidayService {
    List<Holiday> getAllHolidays();
    Holiday createHoliday(HolidayDto holidayDto); 
    void deleteHoliday(Long holidayId);
}