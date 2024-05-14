package com.tucanoo.crm.amq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;


@Component
public class MessageSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMessage(String destination, JsonObject message) {
        jmsTemplate.convertAndSend(destination, message.toString());
    }
}
