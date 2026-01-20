package uvigo.tfgalmacen.controllers;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.models.User;
import uvigo.tfgalmacen.utils.ColorFormatter;
import uvigo.tfgalmacen.utils.PasswordUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.*;

import static uvigo.tfgalmacen.RutasFicheros.MAIN_FXML;
import static uvigo.tfgalmacen.database.UsuarioDAO.SQLcheckUser;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.*;

public class windowLoginController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(windowLoginController.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new ColorFormatter());
        LOGGER.addHandler(ch);
        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) h.setLevel(Level.ALL);
    }

    // Nombres de clases CSS
    private static final String TF_NORMAL = "textfield-normal";
    private static final String TF_ERROR = "textfield-error";
    private static final String LBL_NORMAL = "label-normal";
    private static final String LBL_ERROR = "label-error";

    @FXML
    private Button loginButton;
    @FXML
    private PasswordField password;
    @FXML
    private TextField username;
    @FXML
    private Button loginExitButton;

    // Asegúrate de tener estos fx:id en el FXML
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;

    public static Map<String, String> generarHashesArgon2(List<String> passwords) {
        Map<String, String> resultado = new LinkedHashMap<>();

        for (String pwd : passwords) {
            if (pwd == null || pwd.isBlank()) continue;
            String hash = PasswordUtils.hashPassword(pwd);
            resultado.put(pwd, hash);
        }

        return resultado;
    }

    @FXML
    void login(ActionEvent event) {

        // Limpia hover simulado si viene del ENTER
        loginButton.getStyleClass().remove("hover-simulated");

        final String user = username.getText();
        final String pass = password.getText();

        List<String> lista = Arrays.asList("Admin",
                "juan123",
                "maria123",
                "carlos123",
                "luisa123",
                "ana123",
                "luis123",
                "sofia123",
                "javier123",
                "claudia123",
                "pablo123",
                "andrea123",
                "diego123",
                "valeria123",
                "miguel123",
                "isabel123");

        Map<String, String> hashes = generarHashesArgon2(lista);

        hashes.forEach((plain, hash) -> {
                    System.out.println(plain + " -> " + hash);
                }
        );


        if (SQLcheckUser(Main.connection, user, pass)) {
            LOGGER.fine("Login correcto para el usuario: " + user);

            Main.currentUser = new User(user, pass, Main.connection);

            System.out.println("Usuario activo tras login: " + Main.currentUser);

            try {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource(MAIN_FXML));
                Parent newRoot = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene newScene = new Scene(newRoot);
                newScene.setFill(javafx.scene.paint.Color.TRANSPARENT);

                WindowMovement(newRoot, stage);
                stage.setScene(newScene);
                stage.centerOnScreen();
                stage.setOnShown(e -> {
                    newRoot.applyCss();
                    newRoot.layout();
                    stage.sizeToScene();
                });
                stage.show();

                // Reset visual por si veníamos de error
                setErrorState(username, usernameLabel, false);
                setErrorState(password, passwordLabel, false);

                if (Main.currentUser != null) {
                    LOGGER.info(() -> "Usuario activo: " + Main.currentUser.getName() +
                            " | Rol: " + Main.currentUser.getRole());
                }

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error cargando la escena principal", e);
                e.printStackTrace();
            }

        } else {
            LOGGER.warning("Login incorrecto para el usuario: " + user);
            setErrorState(username, usernameLabel, true);
            setErrorState(password, passwordLabel, true);
            shake(username, SHAKE_DURATION);
            shake(password, SHAKE_DURATION);
        }
    }

    @FXML
    void showRegisterStage(MouseEvent event) {
        LOGGER.info("Petición de registro recibida.");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Estilo base inicial
        setErrorState(username, usernameLabel, false);
        setErrorState(password, passwordLabel, false);

        // Cierre
        loginExitButton.setOnMouseClicked(_ -> {
            LOGGER.info("Cerrando aplicación desde el botón de salida.");
            System.exit(0);
        });

        // ENTER en username/password dispara login con micro-hover en botón
        EventHandler<KeyEvent> onEnterPressed = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loginButton.getStyleClass().add("hover-simulated");
                loginButton.requestFocus();
                loginButton.fire();

                PauseTransition delay = new PauseTransition(Duration.millis(150));
                delay.setOnFinished(e -> loginButton.getStyleClass().remove("hover-simulated"));
                delay.play();
            }
        };
        username.setOnKeyPressed(onEnterPressed);
        password.setOnKeyPressed(onEnterPressed);

        // Al enfocar o clicar cada campo, limpiamos solo ese campo/label
        username.focusedProperty().addListener((obs, was, isNow) -> {
            if (isNow) setErrorState(username, usernameLabel, false);
        });
        username.setOnMouseClicked(_ -> setErrorState(username, usernameLabel, false));

        password.focusedProperty().addListener((obs, was, isNow) -> {
            if (isNow) setErrorState(password, passwordLabel, false);
        });
        password.setOnMouseClicked(_ -> setErrorState(password, passwordLabel, false));
    }

    // --------- Helpers UI (toggle de clases CSS) ---------

    private void setErrorState(TextField field, Label label, boolean error) {
        if (field != null) {
            field.getStyleClass().removeAll(TF_NORMAL, TF_ERROR);
            field.getStyleClass().add(error ? TF_ERROR : TF_NORMAL);
        }
        if (label != null) {
            label.getStyleClass().removeAll(LBL_NORMAL, LBL_ERROR);
            label.getStyleClass().add(error ? LBL_ERROR : LBL_NORMAL);
        }
    }
}
