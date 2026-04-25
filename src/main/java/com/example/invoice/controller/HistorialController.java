package com.example.invoice.controller;

import com.example.invoice.model.DataStore;
import com.example.invoice.model.Invoice;
import com.example.invoice.model.InvoiceItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class HistorialController {
    @FXML private DatePicker dpFrom;
    @FXML private DatePicker dpTo;
    @FXML private TableView<Invoice> tvInvoices;
    @FXML private TableColumn<Invoice, String> colFolio;
    @FXML private TableColumn<Invoice, String> colCliente;
    @FXML private TableColumn<Invoice, LocalDate> colFecha;
    @FXML private TableColumn<Invoice, Number> colTotal;
    @FXML private TableColumn<Invoice, String> colStatus;

    @FXML private TableView<InvoiceItem> tvDetail;
    @FXML private TableColumn<InvoiceItem, String> dProd;
    @FXML private TableColumn<InvoiceItem, String> dSku;
    @FXML private TableColumn<InvoiceItem, Number> dQty;
    @FXML private TableColumn<InvoiceItem, Number> dUnit;
    @FXML private TableColumn<InvoiceItem, Number> dSub;

    private ObservableList<Invoice> all = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        tvInvoices.setItems(all);

        colFolio.setCellValueFactory(cd -> javafx.beans.binding.Bindings.createStringBinding(cd.getValue()::getFolio));
        colCliente.setCellValueFactory(cd -> javafx.beans.binding.Bindings.createStringBinding(() -> cd.getValue().getClient().getName()));
        colFecha.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getDate()));
        colTotal.setCellValueFactory(cd -> new javafx.beans.property.SimpleDoubleProperty(cd.getValue().getTotal()));
        colTotal.setCellFactory(col -> new javafx.scene.control.TableCell<Invoice, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) setText(null); else setText(String.format("$%.2f", value.doubleValue()));
            }
        });
        colStatus.setCellValueFactory(cd -> javafx.beans.binding.Bindings.createStringBinding(() -> cd.getValue().isPaid() ? "Pagada" : "Pendiente"));

        // Detail
        tvInvoices.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> showDetail(newV));
    }

    private void showDetail(Invoice inv) {
        if (inv == null) {
            tvDetail.getItems().clear();
            return;
        }
        tvDetail.setItems(inv.getItems());
        dProd.setCellValueFactory(cd -> javafx.beans.binding.Bindings.createStringBinding(() -> cd.getValue().getProduct().getName()));
        dSku.setCellValueFactory(cd -> javafx.beans.binding.Bindings.createStringBinding(() -> cd.getValue().getProduct().getSku()));
        dQty.setCellValueFactory(cd -> cd.getValue().quantityProperty());
        dUnit.setCellValueFactory(cd -> javafx.beans.binding.Bindings.createDoubleBinding(() -> cd.getValue().getProduct().getPrice(), cd.getValue().productProperty()));
        dSub.setCellValueFactory(cd -> cd.getValue().lineTotalProperty());
    }

    @FXML
    private void onFilter() {
        LocalDate from = dpFrom.getValue();
        LocalDate to = dpTo.getValue();
        if (from == null || to == null) return;
        ObservableList<Invoice> filtered = all.filtered(inv -> !inv.getDate().isBefore(from) && !inv.getDate().isAfter(to));
        tvInvoices.setItems(filtered);
    }

    @FXML
    private void onClearFilter() {
        dpFrom.setValue(null);
        dpTo.setValue(null);
        tvInvoices.setItems(all);
    }
}
