package com.harishkannarao.restdatarabbitmq.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

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
    @JdbcType(VarcharJdbcType.class)
    private UUID studentId;
    @Id
    @NonNull
    @JdbcType(VarcharJdbcType.class)
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
