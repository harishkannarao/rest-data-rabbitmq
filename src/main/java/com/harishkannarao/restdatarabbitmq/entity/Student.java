package com.harishkannarao.restdatarabbitmq.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(name = "student")
public class Student {
    @Id
    @NonNull
    private UUID id;
    @NonNull
    private String name;
    @NonNull
    private String email;
}
