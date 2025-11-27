package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.Tipo;

import java.net.URL;
import java.util.ResourceBundle;

import static uvigo.tfgalmacen.database.TipoDAO.insertTipoStrict;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.ventana_success;

public class windowCrearTipoController implements Initializable {

    @FXML
    private Button ExitButton;

    @FXML
    private Button crear_nuevo_tipo_btn;

    @FXML
    private ColorPicker nuevo_color_tipo_picker;

    @FXML
    private TextField nuevo_tipo_text;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarBotonesVentana();

    }

    private void configurarBotonesVentana() {
        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });

        crear_nuevo_tipo_btn.setOnAction(_ -> crearNuevoTipo());

    }

    private void crearNuevoTipo() {

        javafx.scene.paint.Color colorSeleccionado = nuevo_color_tipo_picker.getValue();

        // Convertir a formato RGB (0–255)
        int r = (int) (colorSeleccionado.getRed() * 255);
        int g = (int) (colorSeleccionado.getGreen() * 255);
        int b = (int) (colorSeleccionado.getBlue() * 255);

        // Formato HEX
        String hex = String.format("#%02X%02X%02X", r, g, b);


        String Rstr = String.format("%.3f", r / 255.0);
        String Gstr = String.format("%.3f", g / 255.0);
        String Bstr = String.format("%.3f", b / 255.0);

        String color_tipo = Rstr.replace(",", ".") + "," + Gstr.replace(",", ".") + "," + Bstr.replace(",", ".");


        String nombre = nuevo_tipo_text.getText();

        insertTipoStrict(Main.connection, new Tipo(color_tipo, nombre));
        Almacen.actualizarAlmacen();

        ventana_success("Tipo nuevo creado", "tipo nuevo creado correctamente", String.format("Se ha creado el tipo %s con color %s", nombre, hex));

        Stage stage = (Stage) crear_nuevo_tipo_btn.getScene().getWindow();
        stage.close();

    }
}


