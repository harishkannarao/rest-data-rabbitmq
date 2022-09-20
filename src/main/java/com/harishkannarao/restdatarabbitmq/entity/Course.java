package com.harishkannarao.restdatarabbitmq.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(name = "course")
public class Course {
    @Id
    @Type(type = "uuid-char")
    @NonNull
    private UUID id;
    @NonNull
    private String name;
    private int workload;
    private short rate;
    @Type(type = "uuid-char")
    @NonNull
    private UUID teacherId;
}
