package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.out.TaskDetailsDTO;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/{tenantId}/{propertyId}/tasks")
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<TaskDetailsDTO>>> getAllTasksOfToday(
        @PathVariable("tenantId") Long tenantId,
        @PathVariable("propertyId") Long propertyId
    ) {
        List<TaskDetailsDTO> tasks = taskService.getAllTasksOfToday(propertyId);
        return ApiResponse.<List<TaskDetailsDTO>>builder()
            .message("Tasks retrieved successfully")
            .data(tasks)
            .statusCode(HttpStatus.OK)
            .build()
            .send();
    }

    @GetMapping("/email")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<String>> sendEmail(
        @PathVariable("tenantId") Long tenantId,
        @PathVariable("propertyId") Long propertyId
    ) {
        taskService.sendSchedulingEmail(propertyId);
        return ApiResponse.<String>builder()
            .message("Email sent successfully")
            .data("Email sent successfully")
            .statusCode(HttpStatus.OK)
            .build()
            .send();
    }
}
