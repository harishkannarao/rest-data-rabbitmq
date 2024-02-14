package com.harishkannarao.restdatarabbitmq.unit;

import com.harishkannarao.restdatarabbitmq.domain.MessagePropertiesHolder;
import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import com.harishkannarao.restdatarabbitmq.listener.MessageListener;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MessageListenerTest {
    private final JsonConverter mockJsonConverter = Mockito.mock(JsonConverter.class);
    private final RabbitMessagingTemplate mockRabbitMessagingTemplate = Mockito.mock(RabbitMessagingTemplate.class);

    private final MessagePropertiesHolder props = new MessagePropertiesHolder(
            "outTopic",
            "outKey",
            "inTopic",
            "inKey",
            "inRetryTopic",
            "inRetryKey",
            Duration.parse("PT2S"),
            Duration.parse("PT15S"),
            "2"
    );
    private final MessageListener messageListener = new MessageListener(mockJsonConverter, mockRabbitMessagingTemplate, props);

    @SuppressWarnings("unchecked")
    @Test
    public void handleMessage_shouldPublishMessage_toOutboundQueue() {
        UUID correlationId = UUID.randomUUID();
        String inMsg = "inMsg";
        String outMsg = "outMsg";
        SampleMessage sampleMsg = SampleMessage.builder()
                .id(correlationId)
                .value("someValue")
                .build();
        when(mockJsonConverter.fromJson(inMsg, SampleMessage[].class))
                .thenReturn(new SampleMessage[]{sampleMsg});
        when(mockJsonConverter.toJson(eq(List.of(sampleMsg))))
                .thenReturn(outMsg);

        messageListener.handleMessage(correlationId, null, null, inMsg);

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map<String, Object>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockRabbitMessagingTemplate, times(1))
                .convertAndSend(
                        eq(props.outboundTopicExchange()),
                        eq(props.outboundRoutingKey()),
                        stringArgumentCaptor.capture(),
                        mapArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getValue()).isEqualTo(outMsg);
        assertThat(mapArgumentCaptor.getValue().get("X-Correlation-ID")).isEqualTo(correlationId);
    }
}
