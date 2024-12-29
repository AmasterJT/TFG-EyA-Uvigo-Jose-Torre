package uvigo.tfgalmacen.controllers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
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
import javafx.stage.Stage;
import uvigo.tfgalmacen.Main;

import static uvigo.tfgalmacen.database.UsuarioDAO.SQLcheckUser;

/**
 *
 * @author Ramesh Godara
 */
public class LoginController implements Initializable{

    public static final boolean IS_RESIZABLE = false;

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
        System.out.println("loginnnnn");

        if (checkLogin(username.getText(), password.getText())){
           System.out.println("login correcto");

            try {
                // Carga la nueva escena desde otro archivo FXML
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("/uvigo/tfgalmacen/main.fxml"));
                Parent newRoot = loader.load();

                // Obtiene la referencia del Stage actual
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Configura la nueva escena en el Stage
                Scene newScene = new Scene(newRoot);

                WindowUtils.enableWindowMovement(newRoot, stage);
                WindowUtils.enableWindowResize(newRoot, stage);

                stage.setScene(newScene);
                stage.centerOnScreen();
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else{
            System.out.println("login incorrecto");
        };


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
    }


    private boolean checkLogin(String username, String password) {
        return SQLcheckUser(Main.connection, username, password);
    }
}

