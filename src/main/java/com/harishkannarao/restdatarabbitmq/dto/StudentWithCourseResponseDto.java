package com.harishkannarao.restdatarabbitmq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
@AllArgsConstructor
public class StudentWithCourseResponseDto {
    @NonNull UUID id;
    @NonNull String name;
    @NonNull String email;
    @NonNull
    @Builder.Default
    List<String> registeredCourses = Collections.emptyList();
}
