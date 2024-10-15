package com.omdevs.dynopdf;

import com.omdevs.dynopdf.Entity.InvoiceItem;
import com.omdevs.dynopdf.Entity.InvoiceRequest;
import com.omdevs.dynopdf.services.PdfService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PdfServiceTests {

    @Autowired
    private PdfService pdfService;

    @Test
    public void testGeneratePdf() throws Exception {
        InvoiceRequest request = new InvoiceRequest();
        request.setSeller("XYZ Pvt. Ltd.");
        request.setSellerGstin("29AABBCCDD121ZD");
        request.setSellerAddress("New Delhi, India");
        request.setBuyer("Vedant Computers");
        request.setBuyerGstin("29AABBCCDD131ZD");
        request.setBuyerAddress("New Delhi, India");

        List<InvoiceItem> items = new ArrayList<>();
        items.add(new InvoiceItem("Product 1", "12 Nos", 123.00, 1476.00));
        items.add(new InvoiceItem("Product 2", "120 Nos", 223.00, 476.00));
        request.setItems(items);

        String pdfPath = pdfService.generateInvoicePdf(request);
        assertNotNull(pdfPath);
        assertTrue(new File(pdfPath).exists());
    }
}
