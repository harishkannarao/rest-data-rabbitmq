package com.harishkannarao.restdatarabbitmq.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(name = "student_course")
@IdClass(StudentCourse.StudentCourseId.class)
public class StudentCourse {
    @Id
    @NonNull
    private UUID studentId;
    @Id
    @NonNull
    private UUID courseId;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class StudentCourseId implements Serializable {
        private UUID studentId;
        private UUID courseId;
    }
}
