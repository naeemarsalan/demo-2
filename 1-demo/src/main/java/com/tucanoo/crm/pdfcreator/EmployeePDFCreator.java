package com.tucanoo.crm.pdfcreator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class EmployeePDFCreator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the JSON data:");
        String json = scanner.nextLine();
        scanner.close();

        Gson gson = new Gson();
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        JsonArray dataArray = jsonObject.getAsJsonArray("data");

        for (int i = 0; i < dataArray.size(); i++) {
            JsonObject employee = dataArray.get(i).getAsJsonObject();
            createPDF(employee);
        }
    }

    public static void createPDF(JsonObject employee) {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Employee Details");
            contentStream.newLine();
            contentStream.showText("First Name: " + employee.get("firstName").getAsString());
            contentStream.newLine();
            contentStream.showText("Last Name: " + employee.get("lastName").getAsString());
            contentStream.newLine();
            contentStream.showText("Country: " + employee.get("country").getAsString());
            contentStream.newLine();
            contentStream.showText("Email Address: " + employee.get("emailAddress").getAsString());
            contentStream.newLine();
            contentStream.showText("Phone Number: " + employee.get("phoneNumber").getAsString());
            contentStream.newLine();
            contentStream.showText("City: " + employee.get("city").getAsString());
            contentStream.endText();
            contentStream.close();

            String fileName = employee.get("firstName").getAsString() + "_" + employee.get("lastName").getAsString() + ".pdf";
            document.save(fileName);
            document.close();

            System.out.println("PDF created successfully: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
