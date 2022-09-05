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
@Table(name = "teacher")
public class Student {
    @Id
    @NonNull
    private UUID id;
    @NonNull
    private String name;
    @NonNull
    private String email;
    @ManyToMany(mappedBy = "students", fetch = FetchType.EAGER)
    @Builder.Default
    Set<Course> courses = Collections.emptySet();
}
