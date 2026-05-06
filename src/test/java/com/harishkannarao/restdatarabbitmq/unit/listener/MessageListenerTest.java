package com.harishkannarao.restdatarabbitmq.unit.listener;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.harishkannarao.restdatarabbitmq.domain.MessagePropertiesHolder;
import com.harishkannarao.restdatarabbitmq.entity.SampleMessage;
import com.harishkannarao.restdatarabbitmq.json.JsonConverter;
import com.harishkannarao.restdatarabbitmq.listener.MessageListener;
import com.harishkannarao.restdatarabbitmq.logback.LogbackTestAppender;
import com.harishkannarao.restdatarabbitmq.publisher.MessagePublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MessageListenerTest {

    private final LogbackTestAppender logbackTestAppender = new LogbackTestAppender(
            MessageListener.class.getName(),
            Level.INFO);
    private final JsonConverter mockJsonConverter = Mockito.mock(JsonConverter.class);
    private final MessagePublisher messagePublisher = Mockito.mock(MessagePublisher.class);

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
    private final MessageListener messageListener = new MessageListener(mockJsonConverter, messagePublisher, props);

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
                .when(messagePublisher)
                .sendToOutboundQueue(assertArg(actual -> assertThat(actual).isEqualTo(sampleMsg)));

        messageListener.handleMessage(correlationId, null, null, inMsg);

        verify(messagePublisher, times(1))
                .sendToOutboundQueue(assertArg(actual -> assertThat(actual).isEqualTo(sampleMsg)));
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

        messageListener.handleMessage(correlationId, null, null, inMsg);

        verify(messagePublisher, times(1))
                .sendToRetryQueue(
                        eq(correlationId),
                        eq(2),
                        assertArg(expiry -> assertThat(expiry)
                                .isAfterOrEqualTo(Instant.now().plusSeconds(14))
                                .isBeforeOrEqualTo(Instant.now().plusSeconds(15))),
                        assertArg(nextRetry -> assertThat(nextRetry)
                                .isAfterOrEqualTo(Instant.now().minusSeconds(2))
                                .isBeforeOrEqualTo(Instant.now().plusSeconds(4))),
                        eq(inMsg));
    }

    @Test
    public void handleMessage_shouldPublishMessage_toRetryQueue_onExceptions_forRetryMessage() {
        UUID correlationId = UUID.randomUUID();
        String inMsg = "inMsg";
        SampleMessage sampleMsg = SampleMessage.builder()
                .id(correlationId)
                .value("$$")
                .build();
        Instant expectedExpiry = Instant.now().plusSeconds(15);
        int count = 2;
        when(mockJsonConverter.fromJson(inMsg, SampleMessage[].class))
                .thenReturn(new SampleMessage[]{sampleMsg});

        messageListener.handleMessage(correlationId, count, expectedExpiry, inMsg);

        verify(messagePublisher, times(1))
                .sendToRetryQueue(
                        eq(correlationId),
                        eq(count + 1),
                        assertArg(expiry -> assertThat(expiry).isEqualTo(expectedExpiry)),
                        assertArg(nextRetry -> assertThat(nextRetry)
                                .isAfterOrEqualTo(Instant.now().minusSeconds(4))
                                .isBeforeOrEqualTo(Instant.now().plusSeconds(8))),
                        eq(inMsg));
    }

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

        verify(messagePublisher, times(1))
                .sendToOutboundQueue(assertArg(actual -> assertThat(actual).isEqualTo(sampleMsg)));
    }

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

        verify(messagePublisher, times(1))
                .sendToRetryQueue(
                        eq(correlationId),
                        eq(count),
                        eq(msgExpiry),
                        eq(msgNextRetry),
                        eq(message)
                );
    }

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

        verify(messagePublisher, times(1))
                .sendToMainQueue(
                        eq(correlationId),
                        eq(count),
                        eq(msgExpiry),
                        eq(message)
                );
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

        verifyNoInteractions(messagePublisher);
    }
}
