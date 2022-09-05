package com.harishkannarao.restdatarabbitmq.dao;

import com.harishkannarao.restdatarabbitmq.entity.StudentCourse;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface StudentCourseDAO extends PagingAndSortingRepository<StudentCourse, StudentCourse.StudentCourseId> {
    List<StudentCourse> findByStudentId(UUID studentId);
    List<StudentCourse> findByCourseId(UUID courseId);
}
