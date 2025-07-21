package uvigo.tfgalmacen.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.database.UsuarioDAO;

import java.sql.Connection;
import java.util.List;

public class MovePendienteToEnProcesoController {

    @FXML
    private ComboBox<Pedido> combo_pedido_update;

    @FXML
    private ComboBox<String> combo_usuario_update;


    @FXML
    private Button ExitButton;

    private List<Pedido> pedidosSeleccionados;
    private Connection connection;

    public void initialize() {
        // Llenar usuarios desde la base de datos
        combo_usuario_update.getItems().addAll(UsuarioDAO.getAllNombresUsuarios(Main.connection));
        ExitButton.setOnMouseClicked(_ -> Platform.exit());
    }

    public void setData(List<Pedido> pedidosSeleccionados, Connection connection) {
        this.pedidosSeleccionados = pedidosSeleccionados;
        this.connection = connection;

        // Establece los pedidos en el ComboBox
        combo_pedido_update.setItems(FXCollections.observableArrayList(pedidosSeleccionados));

        // Establece un convertidor para mostrar solo el código de referencia
        combo_pedido_update.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pedido pedido) {
                return (pedido != null) ? pedido.getCodigo_referencia() : "";
            }

            @Override
            public Pedido fromString(String string) {
                // Normalmente no se usa, pero podrías buscar el pedido por el código si quieres
                return pedidosSeleccionados.stream()
                        .filter(p -> p.getCodigo_referencia().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }
}
