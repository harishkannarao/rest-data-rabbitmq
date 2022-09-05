package com.harishkannarao.restdatarabbitmq.fixtures;

import com.harishkannarao.restdatarabbitmq.entity.Student;

import java.util.UUID;

public class StudentFixtures {
    public static Student randomStudent() {
        return Student.builder()
                .id(UUID.randomUUID())
                .name("name-" + UUID.randomUUID())
                .email("email-" + UUID.randomUUID() + "@example.com")
                .build();
    }
}
