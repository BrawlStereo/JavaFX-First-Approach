package com.example.invoice.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Product {
    private final StringProperty sku = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();

    public Product(String sku, String name, double price) {
        this.sku.set(sku);
        this.name.set(name);
        this.price.set(price);
    }

    public StringProperty skuProperty() { return sku; }
    public StringProperty nameProperty() { return name; }
    public DoubleProperty priceProperty() { return price; }

    public String getSku() { return sku.get(); }
    public String getName() { return name.get(); }
    public double getPrice() { return price.get(); }

    @Override public String toString() { return getName(); }
}
