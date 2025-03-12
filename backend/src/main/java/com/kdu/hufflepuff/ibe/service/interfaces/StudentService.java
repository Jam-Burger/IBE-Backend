package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.out.StudentDTO;

import java.util.List;

public interface StudentService {
    List<StudentDTO> getAllStudents();
    StudentDTO addStudent(StudentDTO studentDTO);
}
