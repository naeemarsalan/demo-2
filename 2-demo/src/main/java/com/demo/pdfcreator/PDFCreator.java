package com.demo.pdfcreator;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.demo.Employee;
import com.demo.MapImageGenerator;
import com.fasterxml.jackson.databind.JsonNode;

public class PDFCreator {

    public String createPDF(Employee employee) {
        String filePath = "/tmp/pdf/" + employee.getFirstName() + "_" + employee.getLastName() + ".pdf";
        File tempFile = null; // Declare tempFile at a scope accessible to the entire method
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            JsonNode coordinates = MapImageGenerator.getCoordinates(employee.getCity(), employee.getCountry());
            Image mapImage = MapImageGenerator.getMapImage(coordinates);
            BufferedImage bufferedImage = toBufferedImage(mapImage);
            tempFile = File.createTempFile("temp", ".png");  // Assign tempFile here
            ImageIO.write(bufferedImage, "png", tempFile);
            PDImageXObject pdImage = PDImageXObject.createFromFileByContent(tempFile, document);

            PDFont fontNormal = PDType1Font.HELVETICA;
            PDFont fontBold = PDType1Font.HELVETICA_BOLD;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                addText(contentStream, fontBold, fontNormal, employee);
                addImage(contentStream, pdImage, page);
            }

            document.save(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete(); // Ensure the temporary file is deleted
            }
        }
        return filePath;
    }

    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

    private void addText(PDPageContentStream contentStream, PDFont fontBold, PDFont fontNormal, Employee employee) throws IOException {
        contentStream.beginText();
        contentStream.setFont(fontBold, 15);
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText("Name: " + employee.getName());
        contentStream.setFont(fontNormal, 12);
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Address: " + employee.getAddress());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Email: " + employee.getEmailAddress());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Country: " + employee.getCountry());
        contentStream.endText();
    }

    private void addImage(PDPageContentStream contentStream, PDImageXObject image, PDPage page) throws IOException {
        float imageWidth = image.getWidth();
        float imageHeight = image.getHeight();
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();

        // Calculate the height of the text block
        float textHeight = 80; // Assuming a default text block height; you may calculate this dynamically

        // Calculate the height of the image block
        float imageBlockHeight = imageHeight + 60; // Adjust this value as needed for spacing between text and image

        // Calculate the y-coordinate for the image
        float y = pageHeight - textHeight - imageBlockHeight - 50; // Adjusted position below the text block

        // Calculate the x-coordinate to center the image horizontally
        float x = (pageWidth - imageWidth) / 2;

        // Draw the image
        contentStream.drawImage(image, x, y, imageWidth, imageHeight);
    }
}
