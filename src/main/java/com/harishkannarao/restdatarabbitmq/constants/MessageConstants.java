package com.harishkannarao.restdatarabbitmq.constants;

import com.harishkannarao.restdatarabbitmq.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MessageConstants {

    public static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);
    public static final String X_CORRELATION_ID = "X-Correlation-ID";
    public static final String X_COUNT = "X-Count";
    public static final String X_MESSAGE_EXPIRY = "X-Message-Expiry";
    public static final String X_MESSAGE_NEXT_RETRY = "X-Message-Next-Retry";
}
