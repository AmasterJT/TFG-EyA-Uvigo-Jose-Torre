package uvigo.tfgalmacen.controllers;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
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
import javafx.stage.Stage;
import uvigo.tfgalmacen.Main;

import uvigo.tfgalmacen.User;
import uvigo.tfgalmacen.database.DataConfig;
import uvigo.tfgalmacen.database.UsuarioDAO;
import uvigo.tfgalmacen.database.RolePermissionDAO;

import static uvigo.tfgalmacen.utils.TerminalColors.*;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.*;
import static uvigo.tfgalmacen.database.UsuarioDAO.SQLcheckUser;


public class loginController implements Initializable {

    public static final boolean IS_RESIZABLE = false;

    public static List<User> allUsers;

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
        System.out.println("⏳ loginnnnn");


        if (SQLcheckUser(Main.connection, username.getText(), password.getText())) {
            System.out.println(VERDE + "✅ login correcto" + RESET);

            Main.currentUser = new User(username.getText(), password.getText(), Main.connection);


            try {
                // Carga la nueva escena desde otro archivo FXML
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("/uvigo/tfgalmacen/main.fxml"));
                Parent newRoot = loader.load();

                // Obtiene la referencia del Stage actual
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Configura la nueva escena en el Stage
                Scene newScene = new Scene(newRoot);

                WindowMovement(newRoot, stage);
                enableWindowResize(newRoot, stage);

                stage.setScene(newScene);
                stage.centerOnScreen();
                stage.show();


            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println(ROJO + "❌ login incorrecto" + RESET);
        }

        System.out.println(CYAN + "Usuario Activo: " + RESET + Main.currentUser.getName() + ", " +
                CYAN + "ROL: " + RESET + Main.currentUser.getRole());

        if (Main.currentUser.getRole().equals("SysAdmin")) {

        }
    }

    @FXML
    void showRegisterStage(MouseEvent event) {
        System.out.println("peticion de registro");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginExitButton.setOnMouseClicked(event -> {
            System.exit(0);
        });

        // Configurar acción de ENTER en el campo username.
        username.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loginButton.requestFocus(); // Hace foco en el botón.
                loginButton.fire(); // Simula el clic en el botón.
            }
        });

        // Configurar acción de ENTER en el campo password.
        password.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loginButton.requestFocus(); // Hace foco en el botón.
                loginButton.fire(); // Simula el clic en el botón.
            }
        });
    }

}

