package com.harishkannarao.restdatarabbitmq.fixtures;

import com.harishkannarao.restdatarabbitmq.entity.Teacher;

import java.util.UUID;

public class TeacherFixtures {
    public static Teacher randomTeacher() {
        return Teacher.builder()
                .id(UUID.randomUUID())
                .name("some name")
                .email("some@example.com")
                .pictureURL("https://some.example.com")
                .build();
    }
}
