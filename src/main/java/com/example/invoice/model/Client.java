package com.example.invoice.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Client {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty rfc = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();

    public Client(String name, String rfc, String address) {
        this.name.set(name);
        this.rfc.set(rfc);
        this.address.set(address);
    }

    public StringProperty nameProperty() { return name; }
    public StringProperty rfcProperty() { return rfc; }
    public StringProperty addressProperty() { return address; }

    public String getName() { return name.get(); }
    public String getRfc() { return rfc.get(); }
    public String getAddress() { return address.get(); }

    @Override
    public String toString() { return getName(); }
}
