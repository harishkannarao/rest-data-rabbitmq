package com.harishkannarao.restdatarabbitmq.entity;

import lombok.Builder;
import lombok.Value;
import org.hibernate.annotations.Type;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class SampleMessage {
    @Type(type = "uuid-char")
    UUID id;
    String value;
}
