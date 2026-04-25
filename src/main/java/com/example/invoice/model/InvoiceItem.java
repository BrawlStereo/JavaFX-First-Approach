package com.example.invoice.model;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;

public class InvoiceItem {
    private final ObjectProperty<Product> product = new SimpleObjectProperty<>();
    private final IntegerProperty quantity = new SimpleIntegerProperty(1);
    private final ReadOnlyDoubleWrapper lineTotal = new ReadOnlyDoubleWrapper();

    public InvoiceItem(Product p, int qty) {
        this.product.set(p);
        this.quantity.set(qty);
        DoubleBinding totalBind = new DoubleBinding() {
            { bind(quantity, p.priceProperty()); }
            @Override protected double computeValue() {
                return quantity.get() * p.getPrice();
            }
        };
        lineTotal.bind(totalBind);
    }

    public Product getProduct() { return product.get(); }
    public ObjectProperty<Product> productProperty() { return product; }
    public IntegerProperty quantityProperty() { return quantity; }
    public ReadOnlyDoubleProperty lineTotalProperty() { return lineTotal.getReadOnlyProperty(); }
}
