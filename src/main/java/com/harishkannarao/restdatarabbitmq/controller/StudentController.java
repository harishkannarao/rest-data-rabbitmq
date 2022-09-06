package com.harishkannarao.restdatarabbitmq.controller;

import com.harishkannarao.restdatarabbitmq.dao.CourseDAO;
import com.harishkannarao.restdatarabbitmq.dao.StudentCourseDAO;
import com.harishkannarao.restdatarabbitmq.dao.StudentDAO;
import com.harishkannarao.restdatarabbitmq.dto.StudentWithCourseResponseDto;
import com.harishkannarao.restdatarabbitmq.entity.Course;
import com.harishkannarao.restdatarabbitmq.entity.StudentCourse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "student", produces = {MediaType.APPLICATION_JSON_VALUE})
public class StudentController {

    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final StudentCourseDAO studentCourseDAO;

    @Autowired
    public StudentController(StudentDAO studentDAO, CourseDAO courseDAO, StudentCourseDAO studentCourseDAO) {
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
        this.studentCourseDAO = studentCourseDAO;
    }

    @GetMapping("{id}")
    public ResponseEntity<StudentWithCourseResponseDto> getById(@PathVariable("id") UUID id) {
        return studentDAO.findById(id)
                .map(student -> {
                    List<UUID> courseIds = studentCourseDAO.findByStudentId(student.getId())
                            .stream()
                            .map(StudentCourse::getCourseId)
                            .toList();
                    List<String> courseNames = StreamSupport.stream(courseDAO.findAllById(courseIds).spliterator(), false)
                            .map(Course::getName)
                            .toList();
                    return StudentWithCourseResponseDto.builder()
                            .id(student.getId())
                            .name(student.getName())
                            .email(student.getEmail())
                            .registeredCourses(courseNames)
                            .build();
                })
                .map(studentWithCourseResponseDto -> ResponseEntity.ok().body(studentWithCourseResponseDto))
                .orElse(ResponseEntity.notFound().build());
    }
}
