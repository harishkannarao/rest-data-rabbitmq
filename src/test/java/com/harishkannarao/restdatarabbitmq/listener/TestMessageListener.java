package com.harishkannarao.restdatarabbitmq.listener;

import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TestMessageListener {

    public static final ConcurrentHashMap<UUID, SampleMessage> HOLDER = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(TestMessageListener.class);

    private final boolean storeReceivedMessages;
    private final JsonConverter jsonConverter;

    @Autowired
    public TestMessageListener(
            @Value("${test.store-received-messages}") boolean storeReceivedMessages,
            JsonConverter jsonConverter) {
        this.storeReceivedMessages = storeReceivedMessages;
        this.jsonConverter = jsonConverter;
    }

    @RabbitListener(queues = "${messaging.message-processor.outbound-queue}", concurrency = "${messaging.message-processor.outbound-queue-concurrency}")
    public void handleMessage(@Header("X-Correlation-ID") UUID correlationId, final String message) {
        logger.info("Received message: " + message);
        if (storeReceivedMessages) {
            List<SampleMessage> sampleMessages = Arrays.asList(jsonConverter.fromJson(message, SampleMessage[].class));
            sampleMessages.forEach(sampleMessage -> HOLDER.put(correlationId, sampleMessage));
        } else {
            logger.info("Not storing messages as test.store-received-messages is set to false");
        }
    }
}
