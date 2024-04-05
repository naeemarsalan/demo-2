package com.demo;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;


public class ReportConsumer {

    private final Logger logger = Logger.getLogger(ReportConsumer.class);

    @Incoming("report")
    public void receive(String customer) {
        logger.infof("Customer %s", customer);
    }
}
