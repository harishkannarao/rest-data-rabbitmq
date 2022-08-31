package com.harishkannarao.restdatarabbitmq.entity;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class SampleMessage {
    UUID id;
    String value;
}
