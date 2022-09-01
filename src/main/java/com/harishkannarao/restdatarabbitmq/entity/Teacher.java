package com.harishkannarao.restdatarabbitmq.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Teacher {
    @Id
    @Type(type = "pg-uuid")
    @NonNull
    private UUID id;
    @NonNull
    private String name;
    private String pictureURL;
    @NonNull
    private String email;
}
