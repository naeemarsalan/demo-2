package com.tucanoo.crm.pdfcreator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;


import com.tucanoo.crm.mapimage.MapImageGenerator;
import com.fasterxml.jackson.databind.JsonNode;


import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.io.File;


public class EmployeePDFCreator {
    private static BufferedImage toBufferedImage(Image image) {
        // Create a blank BufferedImage with the same dimensions as the Image
        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        // Draw the Image onto the BufferedImage
        Graphics graphics = bufferedImage.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        return bufferedImage;
    }
    

    public static void createPDF(List<Map<String,Object>> employee) {
        for (int i = 0; i < employee.size(); i++) {
            try {
            JsonNode coordinates = MapImageGenerator.getCoordinates(employee.get(i).get("city").toString(), employee.get(i).get("country").toString());
            Image mapImage = MapImageGenerator.getMapImage(coordinates);
            PDDocument document = new PDDocument();
            BufferedImage bufferedImage = toBufferedImage(mapImage);
            File tempFile = File.createTempFile("temp", ".png");
            ImageIO.write(bufferedImage, "png", tempFile);
            PDImageXObject image = PDImageXObject.createFromFileByContent(tempFile, document);
            PDPage page = new PDPage();
            PDFont fontNormal = PDType1Font.HELVETICA;
            PDFont fontBold = PDType1Font.HELVETICA_BOLD;
            document.addPage(page);
            float imageWidth = image.getWidth();
            float imageHeight = image.getHeight();
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            float x = (pageWidth - imageWidth) / 2;
            float y = (pageHeight - imageHeight) / 2;
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
            contentStream.drawImage(image, x, y, imageWidth, imageHeight);
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
