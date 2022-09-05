package com.harishkannarao.restdatarabbitmq.dao;

import com.harishkannarao.restdatarabbitmq.entity.Course;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface CourseDAO extends PagingAndSortingRepository<Course, UUID> {

    List<Course> findByTeacherId(UUID teacherId);
}
