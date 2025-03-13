package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.out.StudentDTO;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ApiResponse<List<StudentDTO>> getAllStudents() {
        return ApiResponse.<List<StudentDTO>>builder()
                .statusCode(HttpStatus.OK)
                .message("Students retrieved successfully")
                .data(studentService.getAllStudents())
                .build();
    }

    @PostMapping
    public ApiResponse<StudentDTO> addStudent(@RequestBody StudentDTO studentDTO) {
        return ApiResponse.<StudentDTO>builder()
                .statusCode(HttpStatus.CREATED)
                .message("Student added successfully")
                .data(studentService.addStudent(studentDTO))
                .build();
    }
}
