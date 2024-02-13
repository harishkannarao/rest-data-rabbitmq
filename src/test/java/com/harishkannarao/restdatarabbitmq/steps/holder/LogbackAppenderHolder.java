package com.harishkannarao.restdatarabbitmq.steps.holder;

import ch.qos.logback.classic.Level;
import com.harishkannarao.restdatarabbitmq.listener.MessageListener;
import com.harishkannarao.restdatarabbitmq.logback.LogbackTestAppender;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LogbackAppenderHolder {
    private LogbackTestAppender messageListenerAppender = new LogbackTestAppender(MessageListener.class.getName(), Level.INFO);
}
