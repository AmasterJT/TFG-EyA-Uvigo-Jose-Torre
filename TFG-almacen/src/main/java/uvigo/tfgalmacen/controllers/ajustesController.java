package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.RutasFicheros.WINDOW_LOGIN_FXML;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.crearStageBasico;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.ventana_confirmacion;

public class ajustesController {

    private static final Logger LOGGER = Logger.getLogger(ajustesController.class.getName());

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
    private Button cerrarSesionBtn;

    @FXML
    public void initialize() {
        if (cerrarSesionBtn != null) {
            cerrarSesionBtn.setOnAction(_ -> cerrarSesion());
        }
    }

    /**
     * Cierra la sesión del usuario actual y vuelve a la ventana de login.
     */
    private void cerrarSesion() {

        if (!ventana_confirmacion("Confirmar cierre de sesión", "¿Seguro que deseas cerrar tu sesión?")) {
            return;
        }

        try {
            LOGGER.info("🟡 Cerrando sesión del usuario actual...");

            // Limpiar usuario actual
            Main.currentUser = null;

            // Obtener ventana actual (main)
            Stage ventanaPrincipal = (Stage) cerrarSesionBtn.getScene().getWindow();

            // Cargar ventana login
            FXMLLoader loader = new FXMLLoader(getClass().getResource(WINDOW_LOGIN_FXML));
            Parent root = loader.load();

            Stage loginStage = crearStageBasico(root, true, "Inicio de sesión");
            loginStage.initStyle(StageStyle.TRANSPARENT);

            // Mostrar login y cerrar la actual
            loginStage.show();
            ventanaPrincipal.close();

            LOGGER.fine("Sesión cerrada correctamente, regresando a ventana de login.");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "❌ Error al intentar cerrar sesión y volver al login.", e);
            e.printStackTrace(); // opcional: conserva trazas de depuración
        }
    }
}
