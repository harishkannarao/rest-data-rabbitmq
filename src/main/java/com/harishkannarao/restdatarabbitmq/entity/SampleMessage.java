package com.harishkannarao.restdatarabbitmq.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class SampleMessage {
    @JdbcType(VarcharJdbcType.class)
    UUID id;
    String value;
}
