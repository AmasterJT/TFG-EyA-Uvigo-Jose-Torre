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
import uvigo.tfgalmacen.models.Pedido;
import uvigo.tfgalmacen.models.User;
import uvigo.tfgalmacen.database.PedidoDAO;

import java.util.List;

import static uvigo.tfgalmacen.database.UsuarioDAO.getAllUsers;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.SHAKE_DURATION;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.shake;
// importa tus utilidades donde tengas shake(...) e isInvalidSelection(...)
// import static tu.paquete.UIUtils.shake;
// import static tu.paquete.UIUtils.isInvalidSelection;

public class windowMovePendienteToEnProcesoController {

    private static final String PLACEHOLDER_USUARIO = "Seleccionar usuario";

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

    // Lista observable de horas de envío
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
        combo_hora_envio_update.setItems(horas_envio);
        combo_hora_envio_update.getSelectionModel().selectFirst();
    }

    private void setUsers() {
        List<User> users = getAllUsers(Main.connection);

        combo_usuario_update.setItems(FXCollections.observableArrayList(users));
        combo_usuario_update.setPromptText(PLACEHOLDER_USUARIO);     // 🔹 placeholder visible mientras no haya selección
        combo_usuario_update.getSelectionModel().clearSelection();   // 🔹 empieza SIN selección

        combo_usuario_update.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                return (user == null) ? "" : (user.getName() + " " + user.getApellido1());
            }

            @Override
            public User fromString(String s) {
                return null;
            }
        });
    }

    private void actualizarPedido(Pedido pedido, User usuarioSeleccionado, String horaEnvioSeleccionada) {
        // 📌 Validación de selección de usuario con “placeholder”
        //    Regla: si NO hay usuario seleccionado, no hacemos nada y hacemos shake.
        if (usuarioSeleccionado == null) {
            // usa tus utilidades:
            shake(combo_usuario_update, SHAKE_DURATION);
            return;
        }

        // Validación de pedido
        if (pedido == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText(null);
            alerta.setContentText("Se debe seleccionar un pedido válido");
            alerta.showAndWait();
            return;
        }

        // Lógica de actualización
        pedido.setEstado("En proceso");
        pedido.setUsuario(usuarioSeleccionado);

        PedidoDAO.updateEstadoPedido(Main.connection, pedido.getId_pedido(), "En proceso", horaEnvioSeleccionada);
        PedidoDAO.updateUsuarioPedido(Main.connection, pedido.getId_pedido(), pedido.getId_usuario());

        // Retirar el pedido movido de la lista
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

        combo_pedido_update.getSelectionModel().selectFirst(); // Selecciona el primer pedido por defecto
    }
}
