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


    List<User> users;

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


        aplicar_nuevo_estado_btn.setOnMouseClicked(_ -> actualizarPedido());
    }

    // =========================================
    // 1) Hook opcional para “reintentar eliminar usuario” cuando no queden pedidos
    //    (el padre puede inyectarlo con setOnAllPedidosProcesados)
    // =========================================
    private Runnable onAllPedidosProcesados;

    public void setOnAllPedidosProcesados(Runnable r) {
        this.onAllPedidosProcesados = r;
    }


    // =========================================
// 2) Actualizar pedido con las reglas solicitadas
// =========================================
    private void actualizarPedido() {
        Pedido pedido_actualizar = combo_pedido_update.getValue();
        User usuario_selecionado = combo_usuario_update.getValue();
        String hora_salida = combo_hora_envio_update.getValue();

        if (pedido_actualizar == null) {
            LOGGER.warning("No hay pedido seleccionado.");
            return;
        }

        int id_pedido = pedido_actualizar.getId_pedido();

        try {
            Main.connection.setAutoCommit(false);

            if (usuario_selecionado == null) {
                // Caso solicitado:
                // - estado -> Pendiente
                // - id_usuario -> NULL
                // - hora_salida -> NULL
                // Reutilizamos tus DAO ya existentes:
                //    1) poner id_usuario = NULL
                //    2) poner estado = 'Pendiente' y hora_salida = NULL
                uvigo.tfgalmacen.database.PedidoDAO.updateEstadoPedidoCanceladoCompletado(Main.connection, id_pedido); // id_usuario = NULL
                uvigo.tfgalmacen.database.PedidoDAO.updateEstadoPedido(Main.connection, id_pedido, "Pendiente", null); // estado + hora_salida=NULL

                LOGGER.fine(() -> "Pedido " + id_pedido + " reasignado a 'Pendiente' con usuario y hora_salida en NULL.");

                Main.connection.commit();

                // Sacar el pedido del combo y actuar si queda vacío
                removePedidoFromComboAndMaybeFinish(pedido_actualizar);
                return;
            }

            // Validación de hora
            if (hora_salida == null || hora_salida.isBlank()) {
                LOGGER.warning("No hay hora de salida seleccionada.");
                Main.connection.rollback();
                return;
            }

            // Caso normal: asignar usuario + hora_salida
            int id_usuario = usuario_selecionado.getId_usuario();
            uvigo.tfgalmacen.database.PedidoDAO.updateUsuarioYHoraSalidaPedido(Main.connection, id_pedido, id_usuario, hora_salida);


            Main.connection.commit();

            // Sacar el pedido del combo y actuar si queda vacío
            removePedidoFromComboAndMaybeFinish(pedido_actualizar);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error actualizando pedido " + id_pedido + ". Haciendo rollback.", ex);
            try {
                Main.connection.rollback();
            } catch (Exception ignored) {
            }
        } finally {
            try {
                Main.connection.setAutoCommit(true);
            } catch (Exception ignored) {
            }
        }
    }

    // =========================================
// 3) Helper: quitar del combo, seleccionar siguiente o cerrar si está vacío
// =========================================
    private void removePedidoFromComboAndMaybeFinish(Pedido p) {
        // Guarda índice antes de eliminar
        int oldIndex = combo_pedido_update.getSelectionModel().getSelectedIndex();

        // Elimina el pedido actual
        combo_pedido_update.getItems().remove(p);
        combo_hora_envio_update.getSelectionModel().clearSelection();
        combo_hora_envio_update.setPromptText(PLACEHOLDER_HORA);
        estado_del_pedido.setText("");

        // Si no quedan pedidos → ejecutar callback o cerrar ventana
        if (combo_pedido_update.getItems().isEmpty()) {
            LOGGER.info("No quedan pedidos pendientes en el combo. Reintentando eliminar el usuario automáticamente.");
            Stage stage = (Stage) aplicar_nuevo_estado_btn.getScene().getWindow();
            stage.close();
            if (onAllPedidosProcesados != null) {
                try {
                    onAllPedidosProcesados.run();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error ejecutando onAllPedidosProcesados.", e);
                }
            } else {
                stage = (Stage) aplicar_nuevo_estado_btn.getScene().getWindow();
                stage.close();
            }
            return;
        }

        // Selecciona automáticamente el siguiente pedido
        int newIndex = Math.min(oldIndex, combo_pedido_update.getItems().size() - 1);
        combo_pedido_update.getSelectionModel().select(newIndex);

        // Actualiza los datos de la interfaz (hora y estado)
        Pedido nuevoSel = combo_pedido_update.getValue();
        if (nuevoSel != null) {
            String hora = obtenerHoraSalidaDePedido(nuevoSel);
            String estado = "   " + obtenerEstadoPedido(nuevoSel);

            if (hora != null && horas_envio.contains(hora)) {
                combo_hora_envio_update.getSelectionModel().select(hora);
            } else {
                combo_hora_envio_update.getSelectionModel().clearSelection();
                combo_hora_envio_update.setPromptText(PLACEHOLDER_HORA);
            }

            estado_del_pedido.setText(estado);
        }

        LOGGER.info("Seleccionado automáticamente el siguiente pedido tras eliminar uno.");
    }

    private void setBloqueHorasEnvio() {
        combo_hora_envio_update.setItems(horas_envio);
        combo_hora_envio_update.setPromptText(PLACEHOLDER_HORA);
        combo_hora_envio_update.getSelectionModel().clearSelection();
    }

    public void setUsers(String username) {
        users = getAllUsers(Main.connection);

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
