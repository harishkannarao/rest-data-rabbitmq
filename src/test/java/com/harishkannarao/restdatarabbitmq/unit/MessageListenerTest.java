package com.harishkannarao.restdatarabbitmq.unit;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.harishkannarao.restdatarabbitmq.domain.MessagePropertiesHolder;
import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import com.harishkannarao.restdatarabbitmq.listener.MessageListener;
import com.harishkannarao.restdatarabbitmq.logback.LogbackTestAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MessageListenerTest {

    private final LogbackTestAppender logbackTestAppender = new LogbackTestAppender(
            MessageListener.class.getName(),
            Level.INFO);
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

    @BeforeEach
    public void setUp() {
        logbackTestAppender.startLogsCapture();
    }

    @AfterEach
    public void tearDown() {
        logbackTestAppender.stopLogsCapture();
    }

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
        doNothing()
                .when(mockRabbitMessagingTemplate)
                .convertAndSend(
                        eq(props.outboundTopicExchange()),
                        eq(props.outboundRoutingKey()),
                        eq(outMsg),
                        argThat((Map<String, Object> map) -> {
                            assertThat(map.get("X-Correlation-ID")).isEqualTo(correlationId);
                            assertThat(map).hasSize(1);
                            return true;
                        })

                );

        messageListener.handleMessage(correlationId, null, null, inMsg);

        verify(mockRabbitMessagingTemplate, times(1))
                .convertAndSend(
                        eq(props.outboundTopicExchange()),
                        eq(props.outboundRoutingKey()),
                        any(),
                        anyMap());
    }

    @Test
    public void handleMessage_shouldPublishMessage_toRetryQueue_onExceptions_forNewMessage() {
        UUID correlationId = UUID.randomUUID();
        String inMsg = "inMsg";
        SampleMessage sampleMsg = SampleMessage.builder()
                .id(correlationId)
                .value("$$")
                .build();
        when(mockJsonConverter.fromJson(inMsg, SampleMessage[].class))
                .thenReturn(new SampleMessage[]{sampleMsg});
        doNothing()
                .when(mockRabbitMessagingTemplate)
                .convertAndSend(
                        eq(props.outboundTopicExchange()),
                        eq(props.outboundRoutingKey()),
                        eq(inMsg),
                        argThat((Map<String, Object> map) -> {
                            assertThat(map.get("X-Correlation-ID")).isEqualTo(correlationId);
                            assertThat(map.get("X-Count")).isEqualTo(2);
                            Object nextRetry = map.get("X-Message-Next-Retry");
                            assertThat(nextRetry).isInstanceOf(Instant.class);
                            Instant nextRetryInstant = (Instant) nextRetry;
                            assertThat(nextRetryInstant)
                                    .isAfterOrEqualTo(Instant.now().minusSeconds(2))
                                    .isBeforeOrEqualTo(Instant.now().plusSeconds(4));
                            Object expiry = map.get("X-Message-Expiry");
                            assertThat(expiry).isInstanceOf(Instant.class);
                            Instant expiryInstant = (Instant) expiry;
                            assertThat(expiryInstant)
                                    .isAfterOrEqualTo(Instant.now().plusSeconds(14))
                                    .isBeforeOrEqualTo(Instant.now().plusSeconds(15));
                            assertThat(map).hasSize(4);
                            return true;
                        })

                );

        messageListener.handleMessage(correlationId, null, null, inMsg);

        verify(mockRabbitMessagingTemplate, times(1))
                .convertAndSend(
                        eq(props.inboundRetryTopicExchange()),
                        eq(props.inboundRetryRoutingKey()),
                        any(),
                        anyMap());
    }

    @Test
    public void handleMessage_shouldPublishMessage_toRetryQueue_onExceptions_forRetryMessage() {
        UUID correlationId = UUID.randomUUID();
        String inMsg = "inMsg";
        SampleMessage sampleMsg = SampleMessage.builder()
                .id(correlationId)
                .value("$$")
                .build();
        Instant expiry = Instant.now().plusSeconds(15);
        int count = 2;
        when(mockJsonConverter.fromJson(inMsg, SampleMessage[].class))
                .thenReturn(new SampleMessage[]{sampleMsg});
        doNothing()
                .when(mockRabbitMessagingTemplate)
                .convertAndSend(
                        eq(props.outboundTopicExchange()),
                        eq(props.outboundRoutingKey()),
                        eq(inMsg),
                        argThat((Map<String, Object> map) -> {
                            assertThat(map.get("X-Correlation-ID")).isEqualTo(correlationId);
                            assertThat(map.get("X-Count")).isEqualTo(count + 1);
                            Object nextRetry = map.get("X-Message-Next-Retry");
                            assertThat(nextRetry).isInstanceOf(Instant.class);
                            Instant nextRetryInstant = (Instant) nextRetry;
                            assertThat(nextRetryInstant)
                                    .isAfterOrEqualTo(Instant.now().minusSeconds(4))
                                    .isBeforeOrEqualTo(Instant.now().plusSeconds(8));
                            assertThat(map.get("X-Message-Expiry")).isEqualTo(expiry);
                            assertThat(map).hasSize(4);
                            return true;
                        })

                );


        messageListener.handleMessage(correlationId, count, expiry, inMsg);

        verify(mockRabbitMessagingTemplate, times(1))
                .convertAndSend(
                        eq(props.inboundRetryTopicExchange()),
                        eq(props.inboundRetryRoutingKey()),
                        any(),
                        anyMap());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void handleMessage_shouldPublishMessage_toOutboundQueue_forRetryMessage() {
        UUID correlationId = UUID.randomUUID();
        String inMsg = "inMsg";
        String outMsg = "outMsg";
        SampleMessage sampleMsg = SampleMessage.builder()
                .id(correlationId)
                .value("$$")
                .build();
        Instant expiry = Instant.now().plusSeconds(15);
        int count = 3;
        when(mockJsonConverter.fromJson(inMsg, SampleMessage[].class))
                .thenReturn(new SampleMessage[]{sampleMsg});
        when(mockJsonConverter.toJson(eq(List.of(sampleMsg))))
                .thenReturn(outMsg);


        messageListener.handleMessage(correlationId, count, expiry, inMsg);

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
        assertThat(mapArgumentCaptor.getValue()).hasSize(1);

        verify(mockRabbitMessagingTemplate, times(0))
                .convertAndSend(
                        eq(props.inboundRetryTopicExchange()),
                        eq(props.inboundRetryRoutingKey()),
                        anyString(),
                        anyMap());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void handleRetry_shouldSendMessage_toRetryQueue_whenNextRetryTimeNotLapsed() {
        UUID correlationId = UUID.randomUUID();
        int count = 2;
        Instant msgExpiry = Instant.now().plusSeconds(5);
        Instant msgNextRetry = Instant.now().plusSeconds(2);
        String message = "inMsg";

        messageListener.handleRetry(
                correlationId,
                count,
                msgExpiry,
                msgNextRetry,
                message
        );

        ArgumentCaptor<Map<String, Object>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockRabbitMessagingTemplate, times(1))
                .convertAndSend(
                        eq(props.inboundRetryTopicExchange()),
                        eq(props.inboundRetryRoutingKey()),
                        eq(message),
                        mapArgumentCaptor.capture()
                );
        assertThat(mapArgumentCaptor.getValue().get("X-Correlation-ID")).isEqualTo(correlationId);
        assertThat(mapArgumentCaptor.getValue().get("X-Count")).isEqualTo(count);
        assertThat(mapArgumentCaptor.getValue().get("X-Message-Next-Retry")).isEqualTo(msgNextRetry);
        assertThat(mapArgumentCaptor.getValue().get("X-Message-Expiry")).isEqualTo(msgExpiry);
        assertThat(mapArgumentCaptor.getValue()).hasSize(4);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void handleRetry_shouldSendMessage_toMainQueue_whenNextRetryTimeLapsed() {
        UUID correlationId = UUID.randomUUID();
        int count = 2;
        Instant msgExpiry = Instant.now().plusSeconds(5);
        Instant msgNextRetry = Instant.now();
        String message = "inMsg";

        messageListener.handleRetry(
                correlationId,
                count,
                msgExpiry,
                msgNextRetry,
                message
        );

        ArgumentCaptor<Map<String, Object>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockRabbitMessagingTemplate, times(1))
                .convertAndSend(
                        eq(props.inboundTopicExchange()),
                        eq(props.inboundRoutingKey()),
                        eq(message),
                        mapArgumentCaptor.capture()
                );
        assertThat(mapArgumentCaptor.getValue().get("X-Correlation-ID")).isEqualTo(correlationId);
        assertThat(mapArgumentCaptor.getValue().get("X-Count")).isEqualTo(count);
        assertThat(mapArgumentCaptor.getValue().get("X-Message-Expiry")).isEqualTo(msgExpiry);
        assertThat(mapArgumentCaptor.getValue()).hasSize(3);
    }

    @Test
    public void handleRetry_shouldExpireMessage_whenExpiryTimeLapsed() {
        UUID correlationId = UUID.randomUUID();
        int count = 2;
        Instant msgExpiry = Instant.now();
        Instant msgNextRetry = Instant.now().plusSeconds(2);
        String message = "inMsg";

        messageListener.handleRetry(
                correlationId,
                count,
                msgExpiry,
                msgNextRetry,
                message
        );

        assertThat(logbackTestAppender.getLogs())
                .extracting(ILoggingEvent::getFormattedMessage)
                .anySatisfy(s -> assertThat(s).contains("Message expired:"));

        verify(mockRabbitMessagingTemplate, times(0))
                .convertAndSend(
                        anyString(),
                        anyString(),
                        anyString(),
                        anyMap()
                );
    }


}
