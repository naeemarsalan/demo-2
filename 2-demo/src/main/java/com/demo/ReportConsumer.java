package com.demo;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import com.demo.pdfcreator.PDFCreator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ReportConsumer {

    private final Logger logger = Logger.getLogger(ReportConsumer.class);
    private final ObjectMapper objectMapper;

    public ReportConsumer() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Incoming("reports")
    @Outgoing("generated-reports")
    public String receive(String employeeJson) {
        try {
            Employee employee = objectMapper.readValue(employeeJson, Employee.class);
            PDFCreator pdfCreator = new PDFCreator();
            String pdfPath = pdfCreator.createPDF(employee);  // Ensure createPDF returns the file path

            logger.info("Generated PDF report for employee: " + employee.getName());
            // // Convert PDF to Base64
            byte[] pdfData = Files.readAllBytes(Paths.get(pdfPath));
            String encodedPdf = Base64.getEncoder().encodeToString(pdfData);

            // Modify JSON payload to include Base64 PDF
            ObjectNode rootNode = (ObjectNode) objectMapper.readTree(employeeJson);
            rootNode.put("pdf", encodedPdf);
            logger.info("Added employee PDF to queue: " + employee.getName());
            return rootNode.toString();  // This JSON with PDF is now sent to the next queue
        } catch (IOException e) {
            logger.error("Failed to process employee PDF report", e);
            return null;  // Handle null in your messaging system appropriately
        }
    }
}
