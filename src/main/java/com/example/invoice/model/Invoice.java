package com.example.invoice.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class Invoice {
    private final String folio;
    private final Client client;
    private final LocalDate date;
    private final boolean paid;
    private final ObservableList<InvoiceItem> items = FXCollections.observableArrayList();

    public Invoice(String folio, Client client, LocalDate date, boolean paid) {
        this.folio = folio;
        this.client = client;
        this.date = date;
        this.paid = paid;
    }

    public String getFolio() { return folio; }
    public Client getClient() { return client; }
    public LocalDate getDate() { return date; }
    public boolean isPaid() { return paid; }
    public ObservableList<InvoiceItem> getItems() { return items; }

    public double getSubtotal() {
        return items.stream().mapToDouble(i -> i.lineTotalProperty().get()).sum();
    }

    public double getIva() { return getSubtotal() * 0.16; }
    public double getTotal() { return getSubtotal() + getIva(); }
}
