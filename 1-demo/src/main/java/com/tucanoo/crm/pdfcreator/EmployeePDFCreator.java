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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class EmployeePDFCreator {

    public static void createPDF(List<Map<String,Object>> employee) {
        for (int i = 0; i < employee.size(); i++) {
            try {
            System.out.println(employee.get(i));
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Employee Details");
            contentStream.newLineAtOffset(100 * (100*i), 700 * (100*i) );
            contentStream.newLine();
            contentStream.showText("First Name: " + employee.get(i).get("firstName").toString());
            contentStream.newLineAtOffset(100 * (100*i), 700 * (200*i));
            contentStream.newLine();
            contentStream.showText("Last Name: " + employee.get(i).get("lastName").toString());
            contentStream.newLineAtOffset(100 * (100*i), 700 * (300*i));
            contentStream.newLine();
            contentStream.showText("Country: " + employee.get(i).get("country").toString());
            contentStream.newLineAtOffset(100 * (100*i), 700 * (400*i));
            contentStream.newLine();
            contentStream.showText("Email Address: " + employee.get(i).get("emailAddress").toString());
            contentStream.newLineAtOffset(100 * (100*i), 700 * (500*i));
            contentStream.newLine();
            contentStream.showText("Phone Number: " + employee.get(i).get("phoneNumber").toString());
            contentStream.newLineAtOffset(100 * (100*i), 700 * (600*i));
            contentStream.newLine();
            contentStream.showText("City: " + employee.get(i).get("city").toString());
            contentStream.endText();
            contentStream.close();

            String fileName =  employee.get(i).get("firstName").toString() + "_" + employee.get(i).get("lastName").toString() + ".pdf";
            document.save(fileName);
            document.close();
            } catch (IOException e) {
               e.printStackTrace();
             }

        }

        // try {
           
            // PDPageContentStream contentStream = new PDPageContentStream(document, page);.
            // contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            // contentStream.beginText();
            // contentStream.newLineAtOffset(100, 700);
            // contentStream.showText("Employee Details");
            // contentStream.newLine();
            // contentStream.showText("First Name: " + employee.get("firstName").getAsString());
            // contentStream.newLine();
            // contentStream.showText("Last Name: " + employee.get("lastName").getAsString());
            // contentStream.newLine();
            // contentStream.showText("Country: " + employee.get("country").getAsString());
            // contentStream.newLine();
            // contentStream.showText("Email Address: " + employee.get("emailAddress").getAsString());
            // contentStream.newLine();
            // contentStream.showText("Phone Number: " + employee.get("phoneNumber").getAsString());
            // contentStream.newLine();
            // contentStream.showText("City: " + employee.get("city").getAsString());
            // contentStream.endText();
            // contentStream.close();

            // String fileName = employee.get("firstName").getAsString() + "_" + employee.get("lastName").getAsString() + ".pdf";
            // document.save(fileName);
            // document.close();

            // System.out.println("PDF created successfully: " + fileName);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }
}
