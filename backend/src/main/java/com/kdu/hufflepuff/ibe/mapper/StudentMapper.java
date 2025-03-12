package com.kdu.hufflepuff.ibe.mapper;

import com.kdu.hufflepuff.ibe.model.dto.out.StudentDTO;
import com.kdu.hufflepuff.ibe.model.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentMapper {
    StudentDTO toDto(Student student);

    List<StudentDTO> toDto(List<Student> student);

    Student toEntity(StudentDTO studentDTO);
}

