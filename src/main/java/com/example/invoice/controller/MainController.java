package com.example.invoice.controller;

import com.example.invoice.model.Client;
import com.example.invoice.model.DataStore;
import com.example.invoice.model.InvoiceItem;
import com.example.invoice.model.Product;
import com.example.invoice.util.FolioGenerator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainController {
    @FXML private ComboBox<Client> cbClients;
    @FXML private TextField tfRfc;
    @FXML private TextField tfAddress;
    @FXML private DatePicker dpIssue;
    @FXML private DatePicker dpDue;
    @FXML private Label lblFolio;

    @FXML private ListView<Product> lvProducts;

    @FXML private TableView<InvoiceItem> tvItems;
    @FXML private TableColumn<InvoiceItem, String> colSku;
    @FXML private TableColumn<InvoiceItem, String> colName;
    @FXML private TableColumn<InvoiceItem, Integer> colQty;
    @FXML private TableColumn<InvoiceItem, Number> colUnit;
    @FXML private TableColumn<InvoiceItem, Number> colTotal;
    @FXML private TableColumn<InvoiceItem, Void> colActions;

    @FXML private Label lblSubtotal;
    @FXML private Label lblIva;
    @FXML private Label lblTotal;

    private ObservableList<InvoiceItem> items = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
    cbClients.setItems(DataStore.getClients());
        lvProducts.setItems(DataStore.getProducts());

        // Custom cell: product name + '+' button (adds qty = 1)
        lvProducts.setCellFactory(list -> new javafx.scene.control.ListCell<>() {
            private final Button addBtn = new Button("+");
            private final javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(8);
            private final Label name = new Label();
            {
                box.getChildren().addAll(name, addBtn);
                addBtn.setOnAction(e -> {
                    Product p = getItem();
                    if (p == null) return;
                    // Add one unit per click. If the product exists in the list, increment quantity by 1.
                    int qty = 1;
                    InvoiceItem existing = items.stream().filter(it -> it.getProduct().getSku().equals(p.getSku())).findFirst().orElse(null);
                    if (existing != null) {
                        existing.quantityProperty().set(existing.quantityProperty().get() + qty);
                    } else {
                        items.add(new InvoiceItem(p, qty));
                    }
                    tvItems.refresh();
                });
            }
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setGraphic(null);
                } else {
                    name.setText(p.getName());
                    setGraphic(box);
                }
            }
        });

        // Autocompletar RFC y dirección cuando cambia el cliente
        cbClients.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                tfRfc.setText(newV.getRfc());
                tfAddress.setText(newV.getAddress());
            } else {
                tfRfc.clear();
                tfAddress.clear();
            }
        });

        lblFolio.setText(FolioGenerator.nextFolio());

        // TableView
        tvItems.setItems(items);
        colSku.setCellValueFactory(cd -> Bindings.createStringBinding(() -> cd.getValue().getProduct().getSku()));
        colName.setCellValueFactory(cd -> Bindings.createStringBinding(() -> cd.getValue().getProduct().getName()));

        // Cantidad editable
        colQty.setCellValueFactory(cd -> cd.getValue().quantityProperty().asObject());
        colQty.setCellFactory(tc -> {
            javafx.scene.control.cell.TextFieldTableCell<InvoiceItem, Integer> cell = new javafx.scene.control.cell.TextFieldTableCell<>(new javafx.util.converter.IntegerStringConverter());
            return cell;
        });
        colQty.setOnEditCommit((javafx.scene.control.TableColumn.CellEditEvent<InvoiceItem, Integer> ev) -> {
            InvoiceItem item = ev.getRowValue();
            Integer newVal = ev.getNewValue();
            if (newVal == null || newVal.intValue() <= 0) {
                showAlert("Cantidad inválida");
                tvItems.refresh();
                return;
            }
            item.quantityProperty().set(newVal.intValue());
        });

        colUnit.setCellValueFactory(cd -> Bindings.createDoubleBinding(() -> cd.getValue().getProduct().getPrice(), cd.getValue().productProperty()));
        // Formatear precio unitario como moneda
        colUnit.setCellFactory(col -> new javafx.scene.control.TableCell<InvoiceItem, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", value.doubleValue()));
                }
            }
        });

        colTotal.setCellValueFactory(cd -> cd.getValue().lineTotalProperty());
        colTotal.setCellFactory(col -> new javafx.scene.control.TableCell<InvoiceItem, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", value.doubleValue()));
                }
            }
        });

        // Acciones: eliminar
        colActions.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            private final Button btn = new Button("Eliminar");
            {
                btn.setOnAction(e -> {
                    InvoiceItem it = getTableView().getItems().get(getIndex());
                    items.remove(it);
                });
            }
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        tvItems.setEditable(true);

        // TextFormatter para cantidad (enteros positivos) - used by editable TableCell editing
        TextFormatter<Integer> integerFormatter = new TextFormatter<>(new javafx.util.converter.IntegerStringConverter(), 1, change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                if (newText.isEmpty()) return change;
                try {
                    int v = Integer.parseInt(newText);
                    return v > 0 ? change : null;
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
            return null;
        });

        // Totales: subtotal reactivo que escucha cambios en cada InvoiceItem.lineTotal
        SimpleDoubleProperty subtotal = new SimpleDoubleProperty(0.0);
        ChangeListener<Number> lineTotalListener = (obs, oldV, newV) -> recomputeSubtotal(subtotal);

        // Cuando items cambian, suscribirse a lineTotalProperty de cada item
        items.addListener((ListChangeListener<InvoiceItem>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (InvoiceItem it : c.getAddedSubList()) {
                        it.lineTotalProperty().addListener(lineTotalListener);
                    }
                }
                if (c.wasRemoved()) {
                    for (InvoiceItem it : c.getRemoved()) {
                        it.lineTotalProperty().removeListener(lineTotalListener);
                    }
                }
            }
            recomputeSubtotal(subtotal);
            tvItems.refresh();
        });

        // inicializar
        recomputeSubtotal(subtotal);

        lblSubtotal.textProperty().bind(Bindings.format("$%.2f", subtotal));
        DoubleBinding iva = subtotal.multiply(0.16);
        lblIva.textProperty().bind(Bindings.format("$%.2f", iva));
        lblTotal.textProperty().bind(Bindings.format("$%.2f", subtotal.add(iva)));

    // Note: individual '+' buttons on each product handle adding; no global add button binding needed

        // React to list changes to update bindings if needed
        // existing items listener moved above to handle subtotal
    }

    @FXML
    private void openHistory() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/historial.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage st = new javafx.stage.Stage();
            st.setTitle("Historial de Facturas");
            st.setScene(new javafx.scene.Scene(root));
            st.initOwner(lblFolio.getScene().getWindow());
            st.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void saveDraft() {
        // Prevent saving a draft without selecting a client
        Client c = cbClients.getValue();
        if (c == null) { showAlert("Seleccione un cliente antes de guardar el borrador"); return; }

        // create invoice with status pending and add a copy of items to DataStore
        com.example.invoice.model.Invoice inv = new com.example.invoice.model.Invoice(lblFolio.getText(), c, dpIssue.getValue() == null ? java.time.LocalDate.now() : dpIssue.getValue(), false);
        for (InvoiceItem it : items) {
            inv.getItems().add(new InvoiceItem(it.getProduct(), it.quantityProperty().get()));
        }
        com.example.invoice.model.DataStore.addInvoice(inv);
        // regenerate folio for next
        lblFolio.setText(com.example.invoice.util.FolioGenerator.nextFolio());
        showInfo("Borrador guardado en historial.");
    }

    @FXML
    private void generateInvoice() {
        if (cbClients.getValue() == null) { showAlert("Seleccione un cliente antes de generar la factura"); return; }
        if (items.isEmpty()) { showAlert("Agregue al menos un concepto antes de generar la factura"); return; }
        if (dpIssue.getValue() == null) { showAlert("Seleccione la fecha de emisión antes de generar la factura"); return; }

        com.example.invoice.model.Invoice inv = new com.example.invoice.model.Invoice(lblFolio.getText(), cbClients.getValue(), dpIssue.getValue(), true);
        for (InvoiceItem it : items) {
            inv.getItems().add(new InvoiceItem(it.getProduct(), it.quantityProperty().get()));
        }
        com.example.invoice.model.DataStore.addInvoice(inv);
        showInfo("Factura generada y guardada (Pagada).");
        // limpiar form
        resetForm();
        lblFolio.setText(com.example.invoice.util.FolioGenerator.nextFolio());
        items.clear();
    }

    @FXML
    private void resetForm() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de limpiar el formulario?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmar");
        confirm.showAndWait().ifPresent(b -> {
                if (b == ButtonType.YES) {
                cbClients.setValue(null);
                tfRfc.clear();
                tfAddress.clear();
                dpIssue.setValue(null);
                dpDue.setValue(null);
                lvProducts.getSelectionModel().clearSelection();
                items.clear();
            }
        });
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }

    private void recomputeSubtotal(SimpleDoubleProperty subtotal) {
        double s = items.stream().mapToDouble(it -> it.lineTotalProperty().get()).sum();
        subtotal.set(s);
    }

    // onAddConcept removed: use list cell '+' buttons to add items with spinner quantity

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
}
