package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.sql.Timestamp;

public class ItemPaletFinalController {

    @FXML
    private AnchorPane backgroundAnchorPane;

    @FXML
    private Label cliente_label;

    @FXML
    private Label codigo_pedido_label;

    @FXML
    private Label sscc_label;

    public void setData(String codigo_ref, String sscc, String nombre_cliente, int cantidad_total, int numero_productos, Timestamp timestamp) {

        sscc_label.setText(sscc);
        codigo_pedido_label.setText(codigo_ref);
        cliente_label.setText(nombre_cliente);
    }
}
