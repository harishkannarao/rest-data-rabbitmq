package com.harishkannarao.restdatarabbitmq.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(name = "course")
public class Course {
    @Id
    @JdbcType(VarcharJdbcType.class)
    @NonNull
    private UUID id;
    @NonNull
    private String name;
    private int workload;
    private short rate;
    @JdbcType(VarcharJdbcType.class)
    @NonNull
    private UUID teacherId;
}
