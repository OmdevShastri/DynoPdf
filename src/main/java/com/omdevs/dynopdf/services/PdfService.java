package com.omdevs.dynopdf.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.omdevs.dynopdf.Entity.InvoiceItem;
import com.omdevs.dynopdf.Entity.InvoiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.stream.Stream;

@Service
// Add the logger to capture and display CLI information
public class PdfService {

    private static final Logger logger = LoggerFactory.getLogger(PdfService.class);

    private final String pdfStoragePath = "C:/Users/StarPort/Downloads/GeneratedPdfs/";

    // Method to check if the directory exists, and create it if not
    private void ensureDirectoryExists() {
        File directory = new File(pdfStoragePath);
        if (!directory.exists()) {
            directory.mkdirs();
            logger.info("Created directory for storing PDFs: {}", directory.getAbsolutePath());
        } else {
            logger.info("Directory for storing PDFs already exists: {}", directory.getAbsolutePath());
        }
    }

    // Hash method to check file's current state
    private String getFileHash(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        byte[] hashBytes = digest.digest(fileBytes);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public String generateInvoicePdf(InvoiceRequest request) throws Exception {
        ensureDirectoryExists();  // Check and create the folder if necessary

        String fileName = generateFileNameFromHash(request);
        File file = new File(pdfStoragePath + fileName + ".pdf");

        if (file.exists()) {
            logger.info("PDF already exists: {}", file.getAbsolutePath());
            return file.getAbsolutePath();
        } else {
            logger.info("Generating PDF...");
            generatePdfContent(request, file);
            logger.info("PDF generated and stored at: {}", file.getAbsolutePath());
            return file.getAbsolutePath();
        }
    }

    // Generate a file name based on the hash of the content (PDF data)
    private String generateFileNameFromHash(InvoiceRequest request) throws Exception {
        String data = request.toString(); // Convert the input data to string or use specific fields
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();  // This will serve as the unique filename based on content
    }

    public Resource getPdf(String id) throws FileNotFoundException {
        File file = new File(pdfStoragePath + id + ".pdf");
        if (file.exists()) {
            logger.info("PDF file exists: {}", file.getAbsolutePath());
            return new FileSystemResource(file);
        } else {
            throw new FileNotFoundException("PDF not found");
        }
    }



    private void generatePdfContent(InvoiceRequest request, File file) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        // Create table for Seller and Buyer
        PdfPTable sellerBuyerTable = new PdfPTable(2);
        sellerBuyerTable.setWidthPercentage(100);
        sellerBuyerTable.setSpacingBefore(100f);
        sellerBuyerTable.setSpacingAfter(0f);

        // Seller cell
        PdfPCell sellerCell = new PdfPCell(new Paragraph("Seller:\n" + request.getSeller() + "\n" +
                request.getSellerAddress() + "\n" + "GSTIN: " + request.getSellerGstin()));
        sellerCell.setExtraParagraphSpace(1f);
        sellerCell.setPaddingLeft(10f);
        sellerCell.setPaddingTop(10f);
        sellerCell.setPaddingRight(10f);
        sellerCell.setPaddingBottom(10f);
        sellerCell.setBorder(Rectangle.BOX);
        sellerBuyerTable.addCell(sellerCell);

        // Buyer cell
        PdfPCell buyerCell = new PdfPCell(new Paragraph("Buyer:\n" + request.getBuyer() + "\n" +
                request.getBuyerAddress() + "\n" + "GSTIN: " + request.getBuyerGstin()));
        buyerCell.setExtraParagraphSpace(1f);
        buyerCell.setPaddingLeft(10f);
        buyerCell.setPaddingTop(10f);
        buyerCell.setPaddingRight(10f);
        buyerCell.setPaddingBottom(10f);
        buyerCell.setBorder(Rectangle.BOX);
        sellerBuyerTable.addCell(buyerCell);

        document.add(sellerBuyerTable);

        // Create table for Items
        PdfPTable itemTable = new PdfPTable(4); // 4 columns
        itemTable.setWidthPercentage(100);
        itemTable.setSpacingBefore(0f);
        itemTable.setSpacingAfter(0f);

        // Table headers
        Stream.of("Item", "Quantity", "Rate", "Amount").forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setPhrase(new Phrase(columnTitle));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemTable.addCell(header);
        });

        // Adding item rows from request
        for (InvoiceItem item : request.getItems()) {
            itemTable.addCell(new Phrase(item.getName()));
            itemTable.addCell(new Phrase(item.getQuantity()));
            itemTable.addCell(new Phrase(String.valueOf(item.getRate())));
            itemTable.addCell(new Phrase(String.valueOf(item.getAmount())));
        }

        document.add(itemTable);

        PdfPTable emptyTable = new PdfPTable(1); // 4 columns
        emptyTable.setWidthPercentage(100);
        emptyTable.setSpacingBefore(0f);
        emptyTable.setSpacingAfter(10f);

        // empty cell
        PdfPCell empty = new PdfPCell(emptyTable);
        empty.setBorder(Rectangle.BOX);
        sellerBuyerTable.addCell(empty);

        document.add(emptyTable);

        document.close();
    }

    private String generateFileName(InvoiceRequest request) {
        return UUID.randomUUID().toString(); // Generate unique file name
    }
}
