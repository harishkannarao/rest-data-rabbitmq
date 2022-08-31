package com.harishkannarao.restdatarabbitmq.listener;

import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiver.class);
    private final JsonConverter jsonConverter;

    @Autowired
    public MessageReceiver(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @RabbitListener(queues = "${messaging.message-processor.inbound-queue}")
    public void handleMessage(final String message) {
        List<SampleMessage> sampleMessages = Arrays.asList(jsonConverter.fromJson(message, SampleMessage[].class));
        LOGGER.info("message = " + sampleMessages);
    }
}
