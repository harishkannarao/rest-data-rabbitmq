package com.harishkannarao.restdatarabbitmq.controller;

import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

@RestController
@RequestMapping(value = "send-messages", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TestMessageSenderController {
    private static final Logger logger = LoggerFactory.getLogger(TestMessageSenderController.class);

    private final String inboundExchange;
    private final String inboundRoutingKey;
    private final RabbitMessagingTemplate rabbitMessagingTemplate;
    private final JsonConverter jsonConverter;

    @Autowired
    public TestMessageSenderController(
            @Value("${messaging.message-processor.inbound-topic-exchange}") String inboundExchange,
            @Value("${messaging.message-processor.inbound-routing-key}") String inboundRoutingKey,
            RabbitMessagingTemplate rabbitMessagingTemplate,
            JsonConverter jsonConverter) {
        this.inboundExchange = inboundExchange;
        this.inboundRoutingKey = inboundRoutingKey;
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.jsonConverter = jsonConverter;
    }

    @GetMapping("{count}")
    public ResponseEntity<Map<String, Integer>> getById(@PathVariable("count") Integer count) {
        logger.info("Sending sample messages");
        IntStream.range(0, count)
                .forEach(value -> {
                    SampleMessage sampleMessage = SampleMessage.builder()
                            .id(UUID.randomUUID())
                            .value("$Hello World " + value)
                            .build();
                    String message = jsonConverter.toJson(List.of(sampleMessage));
                    Map<String, Object> headers = Map.of("X-Correlation-ID", sampleMessage.getId());
                    rabbitMessagingTemplate.convertAndSend(inboundExchange, inboundRoutingKey, message, headers);
                });

        return ResponseEntity.ok(Map.of("count", count));
    }
}
