package uvigo.tfgalmacen.controllers.apartadosAjustesControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.User;
import uvigo.tfgalmacen.controllers.mainController;
import uvigo.tfgalmacen.utils.ColorFormatter;

import javafx.util.StringConverter;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;


import static uvigo.tfgalmacen.RutasFicheros.WINDOW_AJUSTES_ACTUALIZAR_PEDIDO_ELIMINAR_USUARIOS_FXML;
import static uvigo.tfgalmacen.RutasFicheros.WINDOW_AJUSTES_ELIMINAR_USUARIOS_FXML;
import static uvigo.tfgalmacen.database.UsuarioDAO.getAllUsers;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.crearStageBasico;

public class windowActualizarPedidoDeUsuarioEliminadoController {

    private static final String PLACEHOLDER_USUARIO = "Seleccionar usuario";

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

    public String getCurrent_username() {
        return current_username;
    }

    public void setCurrent_username(String current_username) {
        this.current_username = current_username;
    }

    public String current_username;


    @FXML
    private Button ExitButton;

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

        System.out.println(current_username);

        ExitButton.setOnMouseClicked(_ -> {
            combo_pedido_update.getItems().clear();
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });
    }

    private void setBloqueHorasEnvio() {
        combo_hora_envio_update.setItems(horas_envio);
        combo_hora_envio_update.getSelectionModel().selectFirst();
    }

    public void setUsers(String username) {
        List<User> users = getAllUsers(Main.connection);

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                users.remove(user);
                break;
            }
        }

        System.out.println(username);
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
