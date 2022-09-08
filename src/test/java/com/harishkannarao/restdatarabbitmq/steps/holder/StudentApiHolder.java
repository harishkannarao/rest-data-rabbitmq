package com.harishkannarao.restdatarabbitmq.steps.holder;

import com.harishkannarao.restdatarabbitmq.dto.StudentWithCourseResponseDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Data
@NoArgsConstructor
public class StudentApiHolder {
    private ResponseEntity<String> response;
    private StudentWithCourseResponseDto entity;
}
