package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.InvalidRequestException;
import com.kdu.hufflepuff.ibe.exception.ResourceNotFoundException;
import com.kdu.hufflepuff.ibe.mapper.StudentMapper;
import com.kdu.hufflepuff.ibe.model.dto.out.StudentDTO;
import com.kdu.hufflepuff.ibe.repository.StudentRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("studentService")
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public List<StudentDTO> getAllStudents() {
        List<StudentDTO> students = studentMapper.toDto(studentRepository.findAll());
        if (students.isEmpty()) {
            throw new ResourceNotFoundException("No students found");
        }
        return students;
    }

    public StudentDTO addStudent(StudentDTO studentDTO) {

        if (studentDTO.getName() == null || studentDTO.getName().isEmpty()) {

            throw new InvalidRequestException("Student name cannot be empty");
        }
        return studentMapper.toDto(studentRepository.save(studentMapper.toEntity(studentDTO)));
    }
}

