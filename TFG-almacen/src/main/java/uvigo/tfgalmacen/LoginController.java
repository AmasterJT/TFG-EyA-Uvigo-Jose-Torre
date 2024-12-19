package uvigo.tfgalmacen;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author Ramesh Godara
 */
public class LoginController implements Initializable{

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
}

