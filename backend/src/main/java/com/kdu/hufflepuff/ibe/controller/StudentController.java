package com.kdu.hufflepuff.ibe.controller;
import com.kdu.hufflepuff.ibe.model.dto.out.StudentDTO;
import com.kdu.hufflepuff.ibe.service.interfaces.StudentService;
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
    public List<StudentDTO> getAllStudents() {
        return studentService.getAllStudents();
    }

    @PostMapping
    public StudentDTO addStudent(@RequestBody StudentDTO studentDTO) {
        return studentService.addStudent(studentDTO);
    }
}

