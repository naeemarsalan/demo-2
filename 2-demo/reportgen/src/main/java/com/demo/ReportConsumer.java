package com.demo;

import java.io.IOException;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReportConsumer {

    private final Logger logger = Logger.getLogger(ReportConsumer.class);
    private final ObjectMapper objectMapper;

    public ReportConsumer() {
        objectMapper = new ObjectMapper();
        // Ignore unrecognized properties
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Incoming("report")
    public void receive(String customerJson) {
        try {
            Customer customer = objectMapper.readValue(customerJson, Customer.class);
            logger.infof("Received customer: %s", customer.getId());
        } catch (IOException e) {
            logger.error("Failed to parse customer JSON", e);
        }
    }
}