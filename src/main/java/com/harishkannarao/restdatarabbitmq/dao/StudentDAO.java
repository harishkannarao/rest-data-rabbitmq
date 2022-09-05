package com.harishkannarao.restdatarabbitmq.dao;

import com.harishkannarao.restdatarabbitmq.entity.Student;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface StudentDAO extends PagingAndSortingRepository<Student, UUID> {
}
