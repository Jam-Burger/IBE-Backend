package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.out.TaskDetailsDTO;

import java.util.List;

public interface TaskService {
    List<TaskDetailsDTO> getAllTasksOfToday(Long propertyId);

    void sendSchedulingEmail(Long propertyId);
}
