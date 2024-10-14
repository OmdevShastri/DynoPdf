package com.omdevs.dynopdf;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class InvoiceItem {
    private String name;
    private String quantity;
    private double rate;
    private double amount;

    // Getters and Setters
    InvoiceItem(String name, String quantity, double rate, double amount) {
        this.name = name;
        this.quantity = quantity;
        this.rate = rate;
        this.amount = amount;
    }

}
