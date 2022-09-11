package com.harishkannarao.restdatarabbitmq.steps.holder;

import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class MessageListenerHolder {
    private Map<String, SampleMessage> sampleMessages = new HashMap<>();
}
