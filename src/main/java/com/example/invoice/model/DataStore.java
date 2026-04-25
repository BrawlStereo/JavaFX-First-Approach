package com.example.invoice.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataStore {
    public static ObservableList<Client> getClients() {
        return FXCollections.observableArrayList(
            new Client("Dylan Pereyra", "FLLP920101XXX", "Av. Principal 123, CDMX"),
            new Client("Jose Pedro Gastélum", "TRZ800505YYY", "Calle 45 #10, Puebla"),
            new Client("Alejandro Gutierrez", "PCL700212ZZZ", "Calle Comercio 80, GDL"),
            new Client("Jesús Alejandro Casique", "RBS650323AAA", "Boulevard 5, Monterrey"),
            new Client("Braulio Fernando Antero", "CTS990101BBB", "Calle Tech 1, Querétaro")
        );
    }

    public static ObservableList<Product> getProducts() {
        return FXCollections.observableArrayList(
            new Product("T001", "Leche Lala Proteína", 32.99),
            new Product("T002", "Galletas Emperador", 28.49),
            new Product("A100", "Aceite 1L", 45.00),
            new Product("F200", "Coca-Cola 1L", 39.00),
            new Product("P300", "Bloc de notas A4", 25.00),
            new Product("P301", "Bolígrafo Azul", 3.50),
            new Product("E400", "Chocomilk", 12.00),
            new Product("S500", "Televisor Samsung 40\"", 4000.00),
            new Product("R500", "Shampoo para hombre 5 en 1: Shampoo, acondicionador, exfoliante, gel de baño, crema de afeitar", 500.00)
        );
    }

    // Central store for invoices (in-memory)
    private static final ObservableList<com.example.invoice.model.Invoice> invoicesStore = FXCollections.observableArrayList();

    public static void addInvoice(com.example.invoice.model.Invoice inv) {
        invoicesStore.add(inv);
    }
}
