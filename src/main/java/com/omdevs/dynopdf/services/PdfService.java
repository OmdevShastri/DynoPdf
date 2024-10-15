package com.omdevs.dynopdf.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.omdevs.dynopdf.Entity.InvoiceItem;
import com.omdevs.dynopdf.Entity.InvoiceRequest;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Files;
import java.security.MessageDigest;

@Service
// Add the logger to capture and display CLI information
public class PdfService {

    private static final Logger logger = LoggerFactory.getLogger(PdfService.class);

    private final String pdfStoragePath = "C:/Users/StarPort/Downloads/GeneratedPdfs/";


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
        String fileName = generateFileName(request);
        File file = new File(pdfStoragePath + fileName + ".pdf");

        if (file.exists()) {
            String existingHash = getFileHash(file);
            String newHash = getFileHash(new File("path/to/temp/pdf")); // Temporary new PDF file hash

            if (!existingHash.equals(newHash)) {
                logger.info("File already exists with a different hash. Do you want to overwrite? (yes/no)");
                Scanner scanner = new Scanner(System.in);
                String confirmation = scanner.nextLine();

                if (!"yes".equalsIgnoreCase(confirmation)) {
                    logger.info("User opted not to overwrite the file.");
                    return file.getAbsolutePath();
                }
            }
            logger.info("Generating new PDF and overwriting the existing file...");
        }

        generatePdfContent(request, file);
        logger.info("PDF generated and stored at: {}", file.getAbsolutePath());
        return file.getAbsolutePath();
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
        sellerCell.setBorder(Rectangle.BOX);
        sellerBuyerTable.addCell(sellerCell);

        // Buyer cell
        PdfPCell buyerCell = new PdfPCell(new Paragraph("Buyer:\n" + request.getBuyer() + "\n" +
                request.getBuyerAddress() + "\n" + "GSTIN: " + request.getBuyerGstin()));
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
