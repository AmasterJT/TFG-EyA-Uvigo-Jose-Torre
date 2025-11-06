package uvigo.tfgalmacen.controllers.apartadosAjustesControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.User;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.database.UsuarioDAO.getAllUsers;

public class windowActualizarPedidoDeUsuarioEliminadoController {

    private static final String PLACEHOLDER_USUARIO = "Seleccionar usuario";
    private static final String PLACEHOLDER_HORA = "Seleccionar hora";

    // Lista observable de horas de envío
    private final ObservableList<String> horas_envio = FXCollections.observableArrayList(
            "primera_hora",
            "segunda_hora"
    );

    private static final Logger LOGGER = Logger.getLogger(windowActualizarPedidoDeUsuarioEliminadoController.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new ColorFormatter());
        LOGGER.addHandler(ch);

        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            h.setLevel(Level.ALL);
        }
    }

    private String current_username;

    public String getCurrent_username() {
        return current_username;
    }

    public void setCurrent_username(String current_username) {
        this.current_username = current_username;
    }

    @FXML
    private Button ExitButton;
    @FXML
    private Label estado_del_pedido;
    @FXML
    private Button aplicar_nuevo_estado_btn;
    @FXML
    private ComboBox<String> combo_hora_envio_update;
    @FXML
    private ComboBox<Pedido> combo_pedido_update;
    @FXML
    private ComboBox<User> combo_usuario_update;
    @FXML
    private HBox windowBar;

    public void initialize() {
        setBloqueHorasEnvio();


        combo_pedido_update.getSelectionModel().selectedItemProperty().addListener((obs, oldP, newP) -> {
            if (newP == null) {
                combo_hora_envio_update.getSelectionModel().clearSelection();
                combo_hora_envio_update.setPromptText(PLACEHOLDER_HORA);
                return;
            }
            String hora = obtenerHoraSalidaDePedido(newP);
            String estado = "   " + obtenerEstadoPedido(newP);

            if (hora != null && horas_envio.contains(hora)) {

                combo_hora_envio_update.getSelectionModel().select(hora);
                estado_del_pedido.setText(estado);
            } else {
                // si el pedido no tiene hora válida, no seleccionamos nada
                combo_hora_envio_update.getSelectionModel().clearSelection();
                combo_hora_envio_update.setPromptText(PLACEHOLDER_HORA);
            }
        });


        ExitButton.setOnMouseClicked(_ -> {
            combo_pedido_update.getItems().clear();
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });
    }

    private void setBloqueHorasEnvio() {
        combo_hora_envio_update.setItems(horas_envio);
        combo_hora_envio_update.setPromptText(PLACEHOLDER_HORA);
        combo_hora_envio_update.getSelectionModel().clearSelection();
    }

    public void setUsers(String username) {
        List<User> users = getAllUsers(Main.connection);

        // Quita del combo al usuario actual (si existe)
        users.removeIf(u -> u != null && username != null && username.equals(u.getUsername()));

        combo_usuario_update.setItems(FXCollections.observableArrayList(users));
        combo_usuario_update.setPromptText(PLACEHOLDER_USUARIO);
        combo_usuario_update.getSelectionModel().clearSelection();

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

        // Selecciona el primero (disparará el listener y sincronizará la hora)
        if (!pedidosSeleccionados.isEmpty()) {
            combo_pedido_update.getSelectionModel().selectFirst();
        } else {
            combo_pedido_update.getSelectionModel().clearSelection();
        }

    }

    private String obtenerEstadoPedido(Pedido p) {
        if (p == null) return null;

        // 1) Intentar getHora_salida()
        try {
            Method m = p.getClass().getMethod("getEstado");
            Object v = m.invoke(p);
            System.out.println(v.toString());
            return v == null ? null : v.toString();
        } catch (Exception e) {

            LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }

        LOGGER.fine("No se pudo obtener hora_salida del Pedido mediante reflexión.");
        return null;
    }

    // --- Utilidad segura para extraer la hora del Pedido, sin depender del nombre exacto del getter ---
    private String obtenerHoraSalidaDePedido(Pedido p) {
        if (p == null) return null;

        // 1) Intentar getHora_salida()
        try {
            Method m = p.getClass().getMethod("getHoraSalida");
            Object v = m.invoke(p);
            return v == null ? null : v.toString();
        } catch (Exception e) {

            LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }

        LOGGER.fine("No se pudo obtener hora_salida del Pedido mediante reflexión.");
        return null;
    }

    private String normalizarHora(String v) {
        if (v == null) return null;
        String x = v.trim().toLowerCase();

        // admite variantes comunes
        return switch (x) {
            case "primera_hora", "primera hora", "primera", "1", "h1" -> "primera_hora";
            case "segunda_hora", "segunda hora", "segunda", "2", "h2" -> "segunda_hora";
            default -> null; // no reconocido
        };
    }

    private void configurarPlaceholderHora() {
        // Celdas del desplegable (normales)
        combo_hora_envio_update.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
            }
        });

        // Celda del botón (lo que se ve cuando está cerrado)
        combo_hora_envio_update.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(PLACEHOLDER_HORA);
                    // opcional: estilo “gris” tipo prompt
                    setStyle("-fx-text-fill: -fx-prompt-text-fill;");
                } else {
                    setText(item);
                    setStyle(null);
                }
            }
        });
    }
}
