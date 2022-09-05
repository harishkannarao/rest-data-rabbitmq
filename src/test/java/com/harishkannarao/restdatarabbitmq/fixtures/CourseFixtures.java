package com.harishkannarao.restdatarabbitmq.fixtures;

import com.harishkannarao.restdatarabbitmq.entity.Course;
import com.harishkannarao.restdatarabbitmq.entity.Teacher;

import java.util.UUID;

public class CourseFixtures {
    public static Course randomCourse() {
        return Course.builder()
                .id(UUID.randomUUID())
                .rate(Short.parseShort("5"))
                .name("some-course-name")
                .workload(2)
                .teacher(TeacherFixtures.randomTeacher())
                .build();
    }

    public static Course randomCourse(Teacher teacher) {
        return randomCourse().toBuilder().teacher(teacher).build();
    }
}
