package uvigo.tfgalmacen.controllers.apartadosAjustesControllers;

import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.controllers.ItemDetallesPedidoController;
import uvigo.tfgalmacen.controllers.mainController;
import uvigo.tfgalmacen.database.PedidoDAO;
import uvigo.tfgalmacen.database.UsuarioDAO;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.database.DetallesPedidoDAO.actualizarEstadoProductoPedido;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.*;

public class windowAjustesCambiarContrasenaUsuariosController implements Initializable {


    private static final Logger LOGGER = Logger.getLogger(windowAjustesCambiarContrasenaUsuariosController.class.getName());

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

    @FXML
    private Button ExitButton;

    @FXML
    private Button cambiar_contrasena_btn;

    @FXML
    private PasswordField confirm_contrsena_text;

    @FXML
    private PasswordField contrsena_text;


    private int id_usuario;

    // Estilos para error y normal
    private static final String ERROR_STYLE = "-fx-background-color: red;";
    private static final String NORMAL_STYLE = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });


        // Click en los campos → limpiar error visual
        contrsena_text.setOnMouseClicked(_ -> limpiarEstilosError());
        confirm_contrsena_text.setOnMouseClicked(_ -> limpiarEstilosError());

        // También al escribir
        contrsena_text.setOnKeyTyped(_ -> limpiarEstilosError());
        confirm_contrsena_text.setOnKeyTyped(_ -> limpiarEstilosError());

        cambiar_contrasena_btn.setOnAction(_ -> cambiarContrasena());


        EventHandler<KeyEvent> onEnterPressed = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                cambiar_contrasena_btn.fire();
            }
        };


        contrsena_text.setOnKeyPressed(onEnterPressed);
        confirm_contrsena_text.setOnKeyPressed(onEnterPressed);
    }

    private void cambiarContrasena() {
        String pass1 = contrsena_text.getText();
        String pass2 = confirm_contrsena_text.getText();

        // Validar campos vacíos
        if (pass1 == null || pass1.isBlank() || pass2 == null || pass2.isBlank()) {
            LOGGER.warning("Intento de cambio de contraseña con campos vacíos. id_usuario=" + id_usuario);

            marcarErrorCampos();
            shake(contrsena_text, SHAKE_DURATION);
            shake(confirm_contrsena_text, SHAKE_DURATION);
            return;
        }

        // Validar que coinciden
        if (!pass1.equals(pass2)) {
            LOGGER.warning("Las contraseñas no coinciden para id_usuario=" + id_usuario);

            marcarErrorCampos();
            shake(contrsena_text, SHAKE_DURATION);
            shake(confirm_contrsena_text, SHAKE_DURATION);
            return;
        }

        // Llamar al DAO
        LOGGER.info("Intentando actualizar contraseña para id_usuario=" + id_usuario);


        boolean resultado = ventana_confirmacion(
                "Confirmar Cambio de Contraseña",
                "¿Seguro que deseas cambiar la contraseña?"
        );

        if (resultado) {
            boolean ok = UsuarioDAO.actualizarContrasena(Main.connection, id_usuario, pass1);

            if (ok) {
                ventana_success("Aviso", "Contraseña Cambiada", "Contraseña cambiada exitosamente");
                LOGGER.info("Contraseña actualizada correctamente para id_usuario=" + id_usuario);

                // Opcional: mostrar algún aviso (si tienes ventana_warning, etc.)
                // ventana_warning("Contraseña actualizada", "La contraseña se ha cambiado correctamente.", "");

                // Cerrar ventana
                Stage stage = (Stage) cambiar_contrasena_btn.getScene().getWindow();
                stage.close();
            } else {
                LOGGER.severe("No se pudo actualizar la contraseña para id_usuario=" + id_usuario);
                marcarErrorCampos();
                shake(contrsena_text, SHAKE_DURATION);
                shake(confirm_contrsena_text, SHAKE_DURATION);
            }
        }
    }

    private void marcarErrorCampos() {
        contrsena_text.setStyle(ERROR_STYLE);
        confirm_contrsena_text.setStyle(ERROR_STYLE);
    }

    private void limpiarEstilosError() {
        contrsena_text.setStyle(NORMAL_STYLE);
        confirm_contrsena_text.setStyle(NORMAL_STYLE);
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public void setData(int id_usuario) {
        this.id_usuario = id_usuario;
    }
}
