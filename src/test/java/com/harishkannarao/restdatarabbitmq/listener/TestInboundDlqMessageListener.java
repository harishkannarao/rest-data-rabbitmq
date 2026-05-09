package com.harishkannarao.restdatarabbitmq.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TestInboundDlqMessageListener {

    public static final ConcurrentHashMap<UUID, String> HOLDER = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(TestInboundDlqMessageListener.class);

    private final boolean storeReceivedMessages;

    @Autowired
    public TestInboundDlqMessageListener(
            @Value("${test.store-received-messages}") boolean storeReceivedMessages) {
        this.storeReceivedMessages = storeReceivedMessages;
    }

    @RabbitListener(queues = "${messaging.message-processor.inbound-queue}.dlq", concurrency = "1")
    public void handleMessage(@Header("X-Correlation-ID") UUID correlationId, final String message) {
        try {
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
