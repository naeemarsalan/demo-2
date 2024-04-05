package com.tucanoo.crm.amq;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import com.google.gson.JsonObject;

public class ReportGenerator {
    private String brokerUrl;
    private String queueName;

    public ReportGenerator(String brokerUrl, String queueName) {
        this.brokerUrl = brokerUrl;
        this.queueName = queueName;
    }

    public void submitToQueue(JsonObject jsonObject) {
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (queue)
            Destination destination = session.createQueue(queueName);

            // Create a MessageProducer from the Session to the Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a TextMessage object from the JsonObject
            TextMessage message = session.createTextMessage(jsonObject.toString());

            // Send the message
            producer.send(message);
            System.out.println("Sent message to queue: " + message.getText());

            // Clean up
            producer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("Caught:" + e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Example usage
        ReportGenerator reportGenerator = new ReportGenerator("tcp://localhost:61616", "REPORT_QUEUE");
        
        // Example JSON object
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("reportType", "sales");
        jsonObject.addProperty("startDate", "2024-01-01");
        jsonObject.addProperty("endDate", "2024-03-31");
        
        // Submit JSON object to the queue
        reportGenerator.submitToQueue(jsonObject);
    }
}

