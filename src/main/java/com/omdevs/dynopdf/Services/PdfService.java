package com.omdevs.dynopdf.Services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.omdevs.dynopdf.InvoiceItem;
import com.omdevs.dynopdf.InvoiceRequest;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class PdfService {

    private final String pdfStoragePath = "C:/Users/StarPort/Downloads/";

    public String generateInvoicePdf(InvoiceRequest request) throws Exception {
        String fileName = generateFileName(request);

        File file = new File(pdfStoragePath + fileName + ".pdf");
        if (file.exists()) {
            return file.getAbsolutePath();
        } else {
            generatePdfContent(request, file);
            return file.getAbsolutePath();
        }
    }

    public Resource getPdf(String id) throws FileNotFoundException {
        File file = new File(pdfStoragePath + id + ".pdf");
        if (file.exists()) {
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
        sellerBuyerTable.setSpacingBefore(10f);
        sellerBuyerTable.setSpacingAfter(10f);

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
        itemTable.setSpacingBefore(10f);
        itemTable.setSpacingAfter(10f);

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

        document.close();
    }

    private String generateFileName(InvoiceRequest request) {
        return UUID.randomUUID().toString(); // Generate unique file name
    }
}
