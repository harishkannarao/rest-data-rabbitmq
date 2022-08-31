package com.harishkannarao.restdatarabbitmq.integration;

import com.harishkannarao.restdatarabbitmq.listener.TestMessageListener;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class MessageListenerIntegrationTest extends AbstractBaseIntegrationTest {

    @Test
    public void sendAndReceiveMessages() {
        // given
        TestMessageListener.HOLDER.clear();
        var id = UUID.fromString("ee7f5aaa-293d-11ed-a261-0242ac120002");
        var message = """
                [
                    {"id": "$id", "value":"Hello World"}
                ]
                """
                .replace("$id", id.toString());

        // when
        rabbitTemplate().convertAndSend("test-topic-exchange-1", "messageProcessor1", message);

        // tjem
        await().atMost(Duration.ofSeconds(5)).until(() -> TestMessageListener.HOLDER.containsKey(id));
        var result = TestMessageListener.HOLDER.get(id);
        assertThat(result.getValue()).isEqualTo("Hello World");
    }
}
