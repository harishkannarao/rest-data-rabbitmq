package com.harishkannarao.restdatarabbitmq.listener;

import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {

    private final JsonConverter jsonConverter;

    @Autowired
    public MessageReceiver(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @RabbitListener(queues = "${messaging.message-processor.inbound-queue}")
    public void handleMessage(final String message) {
        System.out.println("message = " + jsonConverter.fromJson(message, SampleMessage.class));
    }
}
