package com.harishkannarao.restdatarabbitmq.dao;

import com.harishkannarao.restdatarabbitmq.entity.Student;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface StudentDAO extends ListCrudRepository<Student, UUID> {
}
