package com.harishkannarao.restdatarabbitmq.integration.publisher;

import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.integration.AbstractBaseIntegration;
import com.harishkannarao.restdatarabbitmq.listener.TestOutboundMessageListener;
import com.harishkannarao.restdatarabbitmq.publisher.MessagePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class MessagePublisherIT extends AbstractBaseIntegration {

    private MessagePublisher messagePublisher;

    @BeforeEach
    public void setUp() {
        messagePublisher = getBean(MessagePublisher.class);
    }

    @Override
    protected Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("test.store-received-messages", "true");
        return properties;
    }

    @Test
    public void successfully_publish_to_outbound_queue() {
        SampleMessage sampleMessage = SampleMessage.builder()
                .id(UUID.randomUUID())
                .value("Hello World" + UUID.randomUUID())
                .build();

        messagePublisher.sendToOutboundQueue(sampleMessage);

        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    Map<UUID, SampleMessage> publishedMessages = Map.copyOf(TestOutboundMessageListener.HOLDER);
                    assertThat(publishedMessages)
                            .hasEntrySatisfying(sampleMessage.getId(),
                                    actualMessage -> assertThat(actualMessage).isEqualTo(sampleMessage));
                });
    }
}
