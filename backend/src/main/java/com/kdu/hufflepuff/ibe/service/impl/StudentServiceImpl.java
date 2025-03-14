package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.InvalidRequestException;
import com.kdu.hufflepuff.ibe.exception.ResourceNotFoundException;
import com.kdu.hufflepuff.ibe.mapper.StudentMapper;
import com.kdu.hufflepuff.ibe.model.dto.out.StudentDTO;
import com.kdu.hufflepuff.ibe.repository.StudentRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.StudentService;
import com.kdu.hufflepuff.ibe.util.TranslationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;


@Service("studentService")
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final TranslationUtil translationUtil;

    public List<StudentDTO> getAllStudents() {
        List<StudentDTO> students = studentMapper.toDto(studentRepository.findAll());
        if (students.isEmpty()) {
            throw new ResourceNotFoundException("No students found");
        }

        // Get the requested language from the header
        String language = getCurrentLanguage();

        // If language is not English, translate all string attributes of each student
        if (!"en".equalsIgnoreCase(language)) {
            translationUtil.translateList(students, language);
        }

        return students;
    }

    public StudentDTO addStudent(StudentDTO studentDTO) {
        if (studentDTO.getName() == null || studentDTO.getName().isEmpty()) {
            throw new InvalidRequestException("Student name cannot be empty");
        }
        // Store data in English only
        return studentMapper.toDto(studentRepository.save(studentMapper.toEntity(studentDTO)));
    }

    private String getCurrentLanguage() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader("Accept-Language") != null ? request.getHeader("Accept-Language") : "en";
        }
        return "en"; // Default to English if header is missing
    }
}
