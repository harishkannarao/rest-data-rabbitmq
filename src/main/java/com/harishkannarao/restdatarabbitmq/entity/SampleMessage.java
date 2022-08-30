package com.harishkannarao.restdatarabbitmq.entity;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder(toBuilder = true)
@Jacksonized
public class SampleMessage {
    private String value;
}
