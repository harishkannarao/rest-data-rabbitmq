package com.harishkannarao.restdatarabbitmq.json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class JsonConverter {
    private final ObjectMapper objectMapper;

    @Autowired
    public JsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJson(Object value) {
        return objectMapper.writeValueAsString(value);
    }

    public <T> T fromJson(String value, Class<T> valueType) {
        return objectMapper.readValue(value, valueType);
    }
}
