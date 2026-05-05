package com.harishkannarao.restdatarabbitmq.steps.holder;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

@Data
@NoArgsConstructor
public class StudentApiHolder {
    private EntityExchangeResult<String> getByIdResponse;
}
