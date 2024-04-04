package com.tucanoo.crm.pdfcreator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDFont;
import com.tucanoo.crm.mapimage.MapImageGenerator;

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
            MapImageGenerator.getCoordinates(employee.get(i).get("city").toString(), employee.get(i).get("country").toString());
            System.out.println(employee.get(i));
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            PDFont fontNormal = PDType1Font.HELVETICA;
            PDFont fontBold = PDType1Font.HELVETICA_BOLD;
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 600);
            contentStream.setFont(fontBold, 15);
            contentStream.showText("Name: ");
            contentStream.setFont(fontNormal, 15);
            contentStream.showText (employee.get(i).get("firstName").toString() + " " + employee.get(i).get("lastName").toString());
            contentStream.newLineAtOffset(200, 00);
            contentStream.setFont(fontBold, 15);
            contentStream.showText("Address: ");
            contentStream.setFont(fontNormal, 15);
            contentStream.showText (employee.get(i).get("address").toString());
            contentStream.newLineAtOffset(-200, -20);
            contentStream.setFont(fontBold, 15);
            contentStream.showText("Email: " );
            contentStream.setFont(fontNormal, 15);
            contentStream.showText (employee.get(i).get("emailAddress").toString());
            contentStream.newLineAtOffset(200, 00);
            contentStream.setFont(fontBold, 15);
            contentStream.showText("Country: " );
            contentStream.setFont(fontNormal, 15);
            contentStream.showText (employee.get(i).get("country").toString());
            contentStream.endText();
            contentStream.close();

            String fileName =  "pdf/" + employee.get(i).get("firstName").toString() + "_" + employee.get(i).get("lastName").toString() + ".pdf";
            document.save(fileName);
            document.close();
            } catch (IOException e) {
               e.printStackTrace();
             }

        }
    }
}
