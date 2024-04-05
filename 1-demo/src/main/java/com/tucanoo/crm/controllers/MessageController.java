package com.tucanoo.crm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.tucanoo.crm.amq.MessageSender;
import com.google.gson.JsonObject;


@RestController
public class MessageController {

    @Autowired
    private MessageSender messageSender;

    @GetMapping("/send-message")
    public String sendMessage(@RequestParam String destination, JsonObject message) {
        messageSender.sendMessage(destination, message);
        return "Message sent to " + destination + ": " + message;
    }
}