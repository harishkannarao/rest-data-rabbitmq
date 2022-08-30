package com.harishkannarao.restdatarabbitmq.listener;

import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {

    public void processMessage(String message) {
        System.out.println("message = " + message);
    }
}
