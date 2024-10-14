package com.omdevs.dynopdf;

import com.omdevs.dynopdf.Services.PdfService;
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
        request.setItems(items);

        String pdfPath = pdfService.generateInvoicePdf(request);
        assertNotNull(pdfPath);
        assertTrue(new File(pdfPath).exists());
    }
}
