package com.omdevs.dynopdf.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvoiceItem {
    private String name;
    private String quantity;
    private double rate;
    private double amount;

    // Getters and Setters
    public InvoiceItem(String name, String quantity, double rate, double amount) {
        this.name = name;
        this.quantity = quantity;
        this.rate = rate;
        this.amount = amount;
    }

}
