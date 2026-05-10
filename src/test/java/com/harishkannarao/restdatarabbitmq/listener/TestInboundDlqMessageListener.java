package com.harishkannarao.restdatarabbitmq.listener;

import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Component
public class TestInboundDlqMessageListener {

    public static final ConcurrentHashMap<UUID, String> HOLDER = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(TestInboundDlqMessageListener.class);

    private final boolean storeReceivedMessages;
    private final JsonConverter jsonConverter;

    @Autowired
    public TestInboundDlqMessageListener(
            @Value("${test.store-received-messages}") boolean storeReceivedMessages,
            JsonConverter jsonConverter) {
        this.storeReceivedMessages = storeReceivedMessages;
        this.jsonConverter = jsonConverter;
    }

    @RabbitListener(queues = "${messaging.message-processor.inbound-queue}.dlq", concurrency = "1")
    public void handleMessage(@Headers Map<String, Object> headers, final String message) {
        try {
            final UUID correlationId;
            if (headers.containsKey("X-Correlation-ID") && headers.get("X-Correlation-ID") instanceof UUID) {
                correlationId = (UUID) headers.get("X-Correlation-ID");
            } else {
                correlationId  = Stream.of(jsonConverter.fromJson(message, SampleMessage[].class))
                        .findFirst()
                        .map(SampleMessage::getId)
                        .orElseGet(UUID::randomUUID);
            }
            MDC.put("X-Correlation-ID", correlationId.toString());
            logger.info("Received message in inbound DLQ: " + message);
            if (storeReceivedMessages) {
                HOLDER.put(correlationId, message);
            } else {
                logger.info("Not storing messages as test.store-received-messages is set to false");
            }
        } finally {
            MDC.clear();
        }
    }
}
