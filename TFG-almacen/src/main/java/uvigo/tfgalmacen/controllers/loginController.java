package uvigo.tfgalmacen.controllers;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.*;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.User;
import uvigo.tfgalmacen.database.UsuarioDAO;
import uvigo.tfgalmacen.utils.WindowResizer;

import static uvigo.tfgalmacen.RutasFxml.MAIN_FXML;
import static uvigo.tfgalmacen.utils.TerminalColors.*;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.*;
import static uvigo.tfgalmacen.database.UsuarioDAO.SQLcheckUser;


import java.util.logging.Logger;
import java.util.logging.Level;

import uvigo.tfgalmacen.utils.ColorFormatter;

public class loginController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(loginController.class.getName());


    static {
        // Sube el nivel del logger
        LOGGER.setLevel(Level.ALL);

        // Evita que use los handlers del padre (que suelen estar en INFO con SimpleFormatter)
        LOGGER.setUseParentHandlers(false);

        // Crea un ConsoleHandler propio con tu ColorFormatter
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);                 // ¡importante!
        ch.setFormatter(new ColorFormatter());  // tu formatter con colores/emoji
        LOGGER.addHandler(ch);

        // (Opcional) Si quieres también afectar al root logger:
        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            h.setLevel(Level.ALL); // si decides mantenerlos
        }
    }


    @FXML
    private Button loginButton;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    @FXML
    private Button loginExitButton;

    @FXML
    void login(ActionEvent event) {

        if (SQLcheckUser(Main.connection, username.getText(), password.getText())) {
            LOGGER.fine("Login correcto para el usuario: " + username.getText());

            Main.currentUser = new User(username.getText(), password.getText(), Main.connection);

            try {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource(MAIN_FXML));
                Parent newRoot = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene newScene = new Scene(newRoot);
                newScene.setFill(javafx.scene.paint.Color.TRANSPARENT);

                WindowMovement(newRoot, stage);
                //WindowResizer.attach(newRoot, stage, newScene);

                stage.setScene(newScene);
                stage.centerOnScreen();


                stage.setOnShown(e -> {
                    // fuerza cálculo CSS/layout antes de ajustar
                    newRoot.applyCss();
                    newRoot.layout();
                    stage.sizeToScene();
                });

                System.out.println(stage.getHeight());
                System.out.println(stage.getWidth());

                stage.show();

                LOGGER.fine("Escena principal cargada correctamente.");

            } catch (IOException e) {
                LOGGER.warning("Error cargando la escena principal" + e.getMessage());
                e.printStackTrace();
            }

        } else {
            LOGGER.warning("Login incorrecto para el usuario: " + username.getText());
            shake(username, SHAKE_DURATION);
            shake(password, SHAKE_DURATION);
        }

        if (Main.currentUser != null) {
            LOGGER.info(() -> "Usuario activo: " + Main.currentUser.getName() +
                    " | Rol: " + Main.currentUser.getRole());
        }
    }

    @FXML
    void showRegisterStage(MouseEvent event) {
        LOGGER.info("Petición de registro recibida.");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loginExitButton.setOnMouseClicked(_ -> {
            LOGGER.info("Cerrando aplicación desde el botón de salida.");
            System.exit(0);
        });

        // Configurar ENTER para username y password
        EventHandler<KeyEvent> onEnterPressed = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loginButton.getStyleClass().add("hover-simulated");
                loginButton.requestFocus();
                loginButton.fire();

                // Quitar el efecto de hover tras 150 ms
                PauseTransition delay = new PauseTransition(Duration.millis(150));
                delay.setOnFinished(e -> loginButton.getStyleClass().remove("hover-simulated"));
                delay.play();
            }
        };

        username.setOnKeyPressed(onEnterPressed);
        password.setOnKeyPressed(onEnterPressed);

        //LOGGER.info("Controlador de login inicializado correctamente.");
    }
}
