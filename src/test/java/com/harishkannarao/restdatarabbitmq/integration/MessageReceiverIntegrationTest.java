package com.harishkannarao.restdatarabbitmq.integration;

import org.junit.jupiter.api.Test;

public class MessageReceiverIntegrationTest extends AbstractBaseIntegrationTest {

    @Test
    public void sendMessage_toMessageProcessorQueue() {
        rabbitTemplate().convertAndSend("test-topic-exchange-1", "messageProcessor", "Hello World !!!");
    }
}
