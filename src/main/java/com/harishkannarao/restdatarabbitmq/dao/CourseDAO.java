package com.harishkannarao.restdatarabbitmq.dao;

import com.harishkannarao.restdatarabbitmq.entity.Course;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface CourseDAO extends PagingAndSortingRepository<Course, UUID> {
}
