package uvigo.tfgalmacen.controllers.apartadosAjustesControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.stage.StageStyle;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.User;
import uvigo.tfgalmacen.database.RolePermissionDAO;
import uvigo.tfgalmacen.database.UsuarioDAO;
import uvigo.tfgalmacen.utils.ColorFormatter;

import static uvigo.tfgalmacen.RutasFicheros.WINDOW_AJUSTES_ACTUALIZAR_PEDIDO_ELIMINAR_USUARIOS_FXML;
import static uvigo.tfgalmacen.controllers.pedidosController.ESTADOS_DEL_PEDIDO;
import static uvigo.tfgalmacen.database.PedidoDAO.getPedidosAllData;
import static uvigo.tfgalmacen.database.PedidoDAO.updateEstadoPedidoCanceladoCompletado;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.*;

public class windowAjustesEliminarUsuariosController {

    private static final Logger LOGGER = Logger.getLogger(windowAjustesEliminarUsuariosController.class.getName());

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


    private static final String PLACEHOLDER_USUARIO = "Selecciona un usuario";

    @FXML
    private Button ExitButton;

    @FXML
    private TextField nombreEliminar_text;
    @FXML
    private TextField apellido1Eliminar_text;
    @FXML
    private TextField apellido2Eliminar_text;
    @FXML
    private TextField emailEliminar_text;
    @FXML
    private TextField rolesEliminar_text;

    @FXML
    private ComboBox<String> usernameEliminar_comboBox;

    @FXML
    private Button eliminarUsuario_btn;

    @FXML
    private HBox windowBar;

    private final Map<String, User> cacheUsuarios = new LinkedHashMap<>();

    @FXML
    public void initialize() {
        // Campos de solo lectura
        setReadOnly(nombreEliminar_text, apellido1Eliminar_text, apellido2Eliminar_text, emailEliminar_text, rolesEliminar_text);

        // Placeholder del ComboBox
        usernameEliminar_comboBox.setPromptText(PLACEHOLDER_USUARIO);

        // Cargar usuarios
        cargarUsernamesYCache();

        // Listener de selección
        usernameEliminar_comboBox.getSelectionModel().selectedItemProperty().addListener((_, _, nv) -> {
            if (nv != null && !nv.isBlank()) {
                cargarDatosUsuarioDesdeCache(nv);
            } else {
                limpiarCampos();
            }
        });

        // Botones
        eliminarUsuario_btn.setOnAction(_ -> onEliminarUsuario());
        ExitButton.setOnMouseClicked(_ -> cerrarVentana());

        limpiarCampos();
    }

    private void setReadOnly(TextField... fields) {
        for (TextField tf : fields) {
            if (tf == null) continue;
            tf.setEditable(false);
            tf.setFocusTraversable(false);
        }
    }

    private void cargarUsernamesYCache() {
        try {
            cacheUsuarios.clear();
            usernameEliminar_comboBox.getItems().clear();

            List<String> usernames = UsuarioDAO.getAllUsernames(Main.connection);
            usernameEliminar_comboBox.getItems().addAll(usernames);

            // Precarga opcional
            for (String u : usernames) {
                User usr = UsuarioDAO.getUserByUsername(Main.connection, u);
                if (usr != null) cacheUsuarios.put(u, usr);
            }

            // Deja el placeholder si no hay usuarios
            if (usernames.isEmpty()) {
                usernameEliminar_comboBox.getSelectionModel().clearSelection();
                limpiarCampos();
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cargando usuarios para eliminar", e);
            alertError("No fue posible cargar la lista de usuarios.");
        }
    }

    private void cargarDatosUsuarioDesdeCache(String username) {
        User u = cacheUsuarios.get(username);
        if (u == null) {
            u = UsuarioDAO.getUserByUsername(Main.connection, username);
            if (u != null) cacheUsuarios.put(username, u);
        }

        if (u == null) {
            limpiarCampos();
            alertWarn("Aviso", "No se encontró el usuario seleccionado.");
            return;
        }

        nombreEliminar_text.setText(nvl(u.getName()));
        apellido1Eliminar_text.setText(nvl(u.getApellido1()));
        apellido2Eliminar_text.setText(nvl(u.getApellido2()));
        emailEliminar_text.setText(nvl(u.getEmail()));

        String rolNombre = RolePermissionDAO.getRoleNameById(Main.connection, u.getIdRol());
        rolesEliminar_text.setText(nvl(rolNombre));
    }

    private void onEliminarUsuario() {
        String username = usernameEliminar_comboBox.getValue();
        if (isBlank(username)) {
            shake(usernameEliminar_comboBox, SHAKE_DURATION);
            alertWarn("Campos requeridos", "Selecciona un usuario.");
            return;
        }

        boolean confirmado = ventana_confirmacion(
                "Confirmar eliminación",
                "¿Eliminar usuario?" +
                        "\nSe eliminará el usuario '" + username + "'. Esta acción no se puede deshacer.");

        if (!confirmado) return;

        try {
            boolean ok = UsuarioDAO.deleteUserByUsername(Main.connection, username);
            if (ok) {
                alertInfo("Se eliminó el usuario '" + username + "'.");
                cargarUsernamesYCache();

                Stage stage = (Stage) eliminarUsuario_btn.getScene().getWindow();
                stage.close();
            } else {
                alertWarn("No eliminado", "No se pudo eliminar el usuario porque hay que reasignar algunos pedidos.");
                abrirVentanaEditarPedido(obtenerPedidosAsignadosAUsuario(username));


            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "El usuario " + username + " tiene pedidos asignados", e.getStackTrace());
            alertError("No fue posible eliminar el usuario.\n" + e.getMessage());
        }
    }

    private List<Pedido> obtenerPedidosAsignadosAUsuario(String username) {
        List<Pedido> pedidosAsignados = new java.util.ArrayList<>();
        try {
            List<Pedido> todosLosPedidos = getPedidosAllData(Main.connection);
            for (Pedido p : todosLosPedidos) {
                if (p.getId_usuario() > 0)
                    if (Objects.equals(UsuarioDAO.getUsernameById(Main.connection, p.getId_usuario()), username)) {
                        if (!p.getEstado().equals(ESTADOS_DEL_PEDIDO.get(2)) && !p.getEstado().equals(ESTADOS_DEL_PEDIDO.get(3))) {
                            pedidosAsignados.add(p);
                        } else {
                            updateEstadoPedidoCanceladoCompletado(Main.connection, p.getId_pedido());
                        }
                    }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo pedidos asignados al usuario: " + username, e);
        }
        return pedidosAsignados;
    }

    private void limpiarCampos() {
        nombreEliminar_text.clear();
        apellido1Eliminar_text.clear();
        apellido2Eliminar_text.clear();
        emailEliminar_text.clear();
        rolesEliminar_text.clear();
    }

    private void cerrarVentana() {
        Stage s = (Stage) ExitButton.getScene().getWindow();
        s.close();
    }

    // --- Helpers UI ---
    private void alertWarn(String t, String c) {
        Alert a = new Alert(Alert.AlertType.WARNING, c, ButtonType.OK);
        a.setTitle(t);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void alertError(String c) {
        Alert a = new Alert(Alert.AlertType.ERROR, c, ButtonType.OK);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void alertInfo(String c) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, c, ButtonType.OK);
        a.setTitle("Usuario eliminado");
        a.setHeaderText(null);
        a.showAndWait();
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }


    private void abrirVentanaEditarPedido(List<Pedido> pedidos_asignados) {

        if (pedidos_asignados.isEmpty()) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(WINDOW_AJUSTES_ACTUALIZAR_PEDIDO_ELIMINAR_USUARIOS_FXML));
            AnchorPane pane = loader.load();

            windowActualizarPedidoDeUsuarioEliminadoController controller = loader.getController();

            // tu lógica para “eliminar usuario automáticamente”
            // por ejemplo:
            //controller.setOnAllPedidosProcesados(this::onEliminarUsuario);
            controller.setCurrent_username(usernameEliminar_comboBox.getValue());
            controller.setData(pedidos_asignados);
            controller.setUsers(usernameEliminar_comboBox.getValue());


            Stage stage = crearStageBasico(pane, true, "Asignar pedido a usuario");


            Stage ventanaPadre = (Stage) usernameEliminar_comboBox.getScene().getWindow();


            // Bloquear la ventana padre
            stage.initOwner(ventanaPadre);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);


            stage.showAndWait();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir la ventana de actualizar de pedidos", e.getStackTrace());
            e.printStackTrace();
        }

    }


}
