package com.harishkannarao.restdatarabbitmq.steps.holder;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Data
@NoArgsConstructor
public class StudentApiHolder {
    private ResponseEntity<String> getByIdResponse;
}
