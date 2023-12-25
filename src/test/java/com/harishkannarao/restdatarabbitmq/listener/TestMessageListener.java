package com.harishkannarao.restdatarabbitmq.listener;

import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TestMessageListener {

    public static final ConcurrentHashMap<UUID, SampleMessage> HOLDER = new ConcurrentHashMap<>();

    private final JsonConverter jsonConverter;

    @Autowired
    public TestMessageListener(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @RabbitListener(queues = "${messaging.message-processor.outbound-queue}")
    public void handleMessage(final String message) {
        List<SampleMessage> sampleMessages = Arrays.asList(jsonConverter.fromJson(message, SampleMessage[].class));
        sampleMessages.forEach(sampleMessage -> HOLDER.put(sampleMessage.getId(), sampleMessage));
    }
}
