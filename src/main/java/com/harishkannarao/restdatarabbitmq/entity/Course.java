package com.harishkannarao.restdatarabbitmq.entity;

import lombok.*;

import javax.persistence.*;
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
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_course_teacher"))
    @NonNull
    private Teacher teacher;
}
