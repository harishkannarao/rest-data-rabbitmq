package com.harishkannarao.restdatarabbitmq.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Course {
    @Id
    @Type(type = "pg-uuid")
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
