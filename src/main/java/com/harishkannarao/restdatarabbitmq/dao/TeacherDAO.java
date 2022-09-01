package com.harishkannarao.restdatarabbitmq.dao;

import com.harishkannarao.restdatarabbitmq.entity.Teacher;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface TeacherDAO extends PagingAndSortingRepository<Teacher, UUID> {
}
