package com.harishkannarao.restdatarabbitmq.integration;

import org.junit.jupiter.api.Test;

public class MessageReceiverIntegrationTest extends AbstractBaseIntegrationTest {

    @Test
    public void sendMessage_toMessageProcessorQueue() {
        var message = """
                [{"value":"Hello World"}]
                """;
        rabbitTemplate().convertAndSend("test-topic-exchange-1", "messageProcessor", message);
    }
}
