package uvigo.tfgalmacen.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.windowComponentAndFuncionalty;

import java.sql.Connection;
import java.util.List;

public class DetallesPedidoController {

    @FXML
    private Button ExitButton;

    @FXML
    private Button aplicar_nuevo_estado_btn;

    @FXML
    private Button aplicar_nuevo_estado_btn1;

    @FXML
    private GridPane grid_pendientes;

    @FXML
    private ScrollPane pedidiosEnCursoScroll;

    @FXML
    private HBox windowBar;

    private Pedido pedido_para_detallar;

    @FXML
    private Label codigo_referencia_pedido_detalle_label;

    @FXML
    private Label estado_pedido_detalle_label;

    public void initialize() {
        // Llenar usuarios desde la base de datos

        ExitButton.setOnMouseClicked(event -> {

            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });
    }


    public void setData(Pedido pedido_para_detallar, Connection connection) {
        this.pedido_para_detallar = pedido_para_detallar;

        codigo_referencia_pedido_detalle_label.setText(pedido_para_detallar.getCodigo_referencia());
        estado_pedido_detalle_label.setText(pedido_para_detallar.getEstado());
    }

}
