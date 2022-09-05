package com.harishkannarao.restdatarabbitmq.entity;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class SampleMessage {
    UUID id;
    String value;
}
