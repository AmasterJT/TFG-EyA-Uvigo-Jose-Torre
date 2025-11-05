package uvigo.tfgalmacen.controllers.apartadosAjustesControllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class windowAjustesUsuariosController implements Initializable {

    @FXML
    private Button ExitButton;

    @FXML
    private TextField apellido1_text;

    @FXML
    private TextField apellido2_text;

    @FXML
    private Button cambiar_password_btn;

    @FXML
    private TextField email_text;

    @FXML
    private Button guardar_cambios_btn;

    @FXML
    private TextField nombre_text;

    @FXML
    private ComboBox<?> roles_comboBox;

    @FXML
    private TextField username_text;

    @FXML
    private HBox windowBar;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });
    }

}
