package uvigo.tfgalmacen.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.User;
import uvigo.tfgalmacen.database.PedidoDAO;
import uvigo.tfgalmacen.database.UsuarioDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import static uvigo.tfgalmacen.database.UsuarioDAO.getAllUsers;
import static uvigo.tfgalmacen.utils.TerminalColors.*;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.WindowMovement;

public class MovePendienteToEnProcesoController {

    @FXML
    private ComboBox<Pedido> combo_pedido_update;

    @FXML
    private ComboBox<User> combo_usuario_update;

    @FXML
    private HBox windowBar;

    @FXML
    private Button ExitButton;

    @FXML
    private Button aplicar_nuevo_estado_btn;


    private List<Pedido> pedidosSeleccionados;

    public void initialize() {

        // Llenar usuarios desde la base de datos
        setUsers();
        ExitButton.setOnMouseClicked(event -> {
            combo_pedido_update.getItems().clear();
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });


        aplicar_nuevo_estado_btn.setOnMouseClicked(_ -> actualizarPedido(combo_pedido_update.getValue(), combo_usuario_update.getValue()));


    }

    private void setUsers() {
        List<User> users = getAllUsers(Main.connection);

        // Establece los pedidos en el ComboBox
        combo_usuario_update.setItems(FXCollections.observableArrayList(users));

        // Establece un convertidor para mostrar solo el código de referencia
        combo_usuario_update.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                return user.getName() + " " + user.getApellido();
            }

            @Override
            public User fromString(String s) {
                return null;
            }


        });
    }

    private void actualizarPedido(Pedido pedido, User usuarioSeleccionado) {
        // Validar que ambos ComboBox tengan un valor seleccionado


        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null); // puedes poner un título más corto aquí si quieres

        if (combo_pedido_update.getValue() == null || combo_usuario_update.getValue() == null) {
            System.out.println(ROJO + "❌ Se debe seleccionar un pedido y un usuario válido." + RESET);


            alerta.setContentText("Se debe seleccionar un pedido y un usuario válido");
            alerta.showAndWait();

            return;
        }

        // Validación adicional por si los objetos no fueron bien pasados
        if (pedido == null || usuarioSeleccionado == null) {
            System.out.println(ROJO + "❌ Error interno: pedido o usuario no válidos." + RESET);
            alerta.setContentText("Se debe seleccionar un pedido y un usuario válido");
            alerta.showAndWait();
            return;
        }

        // Lógica de actualización
        pedido.setEstado("En proceso");
        pedido.setUsuario(usuarioSeleccionado);

        PedidoDAO.updateEstadoPedido(Main.connection, pedido.getId_pedido(), "En proceso");
        PedidoDAO.updateUsuarioPedido(Main.connection, pedido.getId_pedido(), pedido.getId_usuario());

        System.out.println(CYAN + "✅ El pedido: " + RESET + pedido.getCodigo_referencia() +
                CYAN + " lo va a ejecutar el usuario: " + RESET +
                usuarioSeleccionado.getName() + " " + usuarioSeleccionado.getApellido() +
                " (" + pedido.getId_usuario() + ")");

        combo_pedido_update.getItems().remove(pedido);

        if (combo_pedido_update.getItems().isEmpty()) {
            // Cierra la ventana si no quedan más pedidos
            Stage stage = (Stage) aplicar_nuevo_estado_btn.getScene().getWindow();
            stage.close();
        }
    }


    public void setData(List<Pedido> pedidosSeleccionados, Connection connection) {
        this.pedidosSeleccionados = pedidosSeleccionados;

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

        combo_pedido_update.getSelectionModel().selectFirst();
    }

}
