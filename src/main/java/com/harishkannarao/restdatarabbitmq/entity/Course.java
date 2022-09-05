package com.harishkannarao.restdatarabbitmq.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(name = "course")
public class Course {
    @Id
    @NonNull
    private UUID id;
    @NonNull
    private String name;
    private int workload;
    private short rate;
    @NonNull
    private UUID teacherId;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "student_course",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    @Builder.Default
    private Set<Student> students = Collections.emptySet();
}
