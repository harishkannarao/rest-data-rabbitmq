package com.harishkannarao.restdatarabbitmq.fixtures;

import com.harishkannarao.restdatarabbitmq.entity.Course;

import java.util.UUID;

public class CourseFixtures {
    public static Course randomCourse() {
        return Course.builder()
                .id(UUID.randomUUID())
                .rate(Short.parseShort("5"))
                .name("some-course-name")
                .workload(2)
                .teacherId(UUID.randomUUID())
                .build();
    }

    public static Course randomCourse(UUID teacherId) {
        return randomCourse().toBuilder().teacherId(teacherId).build();
    }
}
