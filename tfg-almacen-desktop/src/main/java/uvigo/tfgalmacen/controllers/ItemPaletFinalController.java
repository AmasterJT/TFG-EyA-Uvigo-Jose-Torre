package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ItemPaletFinalController {

    @FXML
    private Label cliente_label;

    @FXML
    private Label codigo_pedido_label;

    @FXML
    private Label sscc_label;

    public void setData(String codigo_ref, String sscc, String nombre_cliente) {

        sscc_label.setText(sscc);
        codigo_pedido_label.setText(codigo_ref);
        cliente_label.setText(nombre_cliente);
    }
}
