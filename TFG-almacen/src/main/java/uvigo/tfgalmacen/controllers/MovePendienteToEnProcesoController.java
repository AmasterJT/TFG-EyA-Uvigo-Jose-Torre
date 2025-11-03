package uvigo.tfgalmacen.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.User;
import uvigo.tfgalmacen.database.PedidoDAO;

import java.util.List;

import static uvigo.tfgalmacen.database.UsuarioDAO.getAllUsers;
import static uvigo.tfgalmacen.utils.TerminalColors.*;

public class MovePendienteToEnProcesoController {

    @FXML
    private ComboBox<Pedido> combo_pedido_update;

    @FXML
    private ComboBox<User> combo_usuario_update;

    @FXML
    private ComboBox<String> combo_hora_envio_update;

    @FXML
    private Button ExitButton;

    @FXML
    private Button aplicar_nuevo_estado_btn;

    // Ahora es un ObservableList directamente
    private final ObservableList<String> horas_envio = FXCollections.observableArrayList(
            "primera_hora",
            "segunda_hora"
    );

    public void initialize() {
        setUsers();
        setBloqueHorasEnvio();

        ExitButton.setOnMouseClicked(_ -> {
            combo_pedido_update.getItems().clear();
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });

        aplicar_nuevo_estado_btn.setOnMouseClicked(_ ->
                actualizarPedido(
                        combo_pedido_update.getValue(),
                        combo_usuario_update.getValue(),
                        combo_hora_envio_update.getValue()
                )
        );
    }

    private void setBloqueHorasEnvio() {
        // ❌ NO castear
        // combo_hora_envio_update.setItems((ObservableList<String>) horas_envio);

        // ✅ Usar directamente el ObservableList
        combo_hora_envio_update.setItems(horas_envio);
        combo_hora_envio_update.getSelectionModel().selectFirst();
    }

    private void setUsers() {
        List<User> users = getAllUsers(Main.connection);
        combo_usuario_update.setItems(FXCollections.observableArrayList(users));

        combo_usuario_update.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                return user == null ? "" : (user.getName() + " " + user.getApellido());
            }

            @Override
            public User fromString(String s) {
                return null;
            }
        });

        if (!combo_usuario_update.getItems().isEmpty()) {
            combo_usuario_update.getSelectionModel().selectFirst();
        }
    }

    private void actualizarPedido(Pedido pedido, User usuarioSeleccionado, String horaEnvioSeleccionada) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);

        if (combo_pedido_update.getValue() == null || combo_usuario_update.getValue() == null) {
            System.out.println(ROJO + "❌ Se debe seleccionar un pedido y un usuario válido." + RESET);
            alerta.setContentText("Se debe seleccionar un pedido y un usuario válido");
            alerta.showAndWait();
            return;
        }

        if (pedido == null || usuarioSeleccionado == null) {
            System.out.println(ROJO + "❌ Error interno: pedido o usuario no válidos." + RESET);
            alerta.setContentText("Se debe seleccionar un pedido y un usuario válido");
            alerta.showAndWait();
            return;
        }

        // Actualización
        pedido.setEstado("En proceso");
        pedido.setUsuario(usuarioSeleccionado);

        PedidoDAO.updateEstadoPedido(Main.connection, pedido.getId_pedido(), "En proceso", horaEnvioSeleccionada);
        PedidoDAO.updateUsuarioPedido(Main.connection, pedido.getId_pedido(), pedido.getId_usuario());

        System.out.println(CYAN + "✅ El pedido: " + RESET + pedido.getCodigo_referencia() +
                CYAN + " lo va a ejecutar el usuario: " + RESET +
                usuarioSeleccionado.getName() + " " + usuarioSeleccionado.getApellido() +
                " (" + pedido.getId_usuario() + ")");

        combo_pedido_update.getItems().remove(pedido);

        if (combo_pedido_update.getItems().isEmpty()) {
            Stage stage = (Stage) aplicar_nuevo_estado_btn.getScene().getWindow();
            stage.close();
        }
    }

    public void setData(List<Pedido> pedidosSeleccionados) {
        combo_pedido_update.setItems(FXCollections.observableArrayList(pedidosSeleccionados));

        combo_pedido_update.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pedido pedido) {
                return (pedido != null) ? pedido.getCodigo_referencia() : "";
            }

            @Override
            public Pedido fromString(String string) {
                return pedidosSeleccionados.stream()
                        .filter(p -> p.getCodigo_referencia().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        combo_pedido_update.getSelectionModel().selectFirst();
    }
}
