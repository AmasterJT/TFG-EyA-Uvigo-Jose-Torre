package uvigo.tfgalmacen.controllers;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainController implements Initializable {


    @FXML
    AnchorPane topBar;

    @FXML
    HBox windowBar;

    @FXML
    private Button inventarioButton;

    @FXML
    private Button pedidosButton;

    @FXML
    private Button almacenButton;

    @FXML
    private Button ExitButton;

    @FXML
    private Button openExcelButton;

    @FXML
    private Label MenuButton;

    @FXML
    private Label MenuBackButton;

    @FXML
    private Label welcomeText;

    @FXML
    private BorderPane BorderPane;

    @FXML
    private AnchorPane root;

    @FXML
    private AnchorPane almacenContainer;

    @FXML
    AnchorPane Slider;




    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        inventarioButton.setOnMouseClicked(_ -> loadInventarioView());

        pedidosButton.setOnMouseClicked(_ -> loadPedidosView());

        almacenButton.setOnMouseClicked(_ -> loadAlmacenView());

        ExitButton.setOnMouseClicked(_ -> Platform.exit());


        // MenuBackButton.setOnMouseClicked(_ -> slideMenu(false));
        // MenuButton.setOnMouseClicked(_ -> slideMenu(true));

        // Por defecto carga la vista de almacen
        loadAlmacenView();

    }


   //------------------------------------------------------------------------------------------------------------------

    //                                   APARTADOS DEL MAIN CONTROLLER

    //------------------------------------------------------------------------------------------------------------------

    @FXML
    private void loadFXML(String fileName) {
        Parent parent;
        try {
            String PATH = "/uvigo/tfgalmacen/" + fileName + ".fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH));
            parent = loader.load();

            BorderPane.setCenter(parent);

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadAlmacenView() {
        loadFXML("almacen");
    }

    @FXML
    private void loadInventarioView() {
        loadFXML("inventario");
    }

    @FXML
    private void loadPedidosView() {
        loadFXML("pedidos");
    }

    //------------------------------------------------------------------------------------------------------------------

    //                                        Funciones adicionales

    //------------------------------------------------------------------------------------------------------------------


    @FXML
    private void openXmlInExcel() {
        String excelPath = "\"C:\\Program Files (x86)\\Microsoft Office\\root\\Office16\\EXCEL.EXE\"";
        File folder = new File("output_files/");

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Directorio no encontrado.");
            return;
        }

        File[] xmlFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));

        if (xmlFiles == null || xmlFiles.length == 0) {
            System.out.println("No se encontraron archivos XML.");
            return;
        }

        try {
            for (File xmlFile : xmlFiles) {
                // Usa el comando por defecto del sistema para abrir el archivo con la aplicación asociada (Excel si está bien asociado).
                //Desktop.getDesktop().open(xmlFile);

                // Construir el comando para abrir cada archivo XML con Excel
                String command = excelPath + " \"" + xmlFile.getAbsolutePath() + "\"";
                Runtime.getRuntime().exec(command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void slideMenu(boolean show) {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.4), Slider);
        slide.setToX(show ? 0 : -176);
        slide.setOnFinished(e -> {
            MenuButton.setVisible(!show);
            MenuBackButton.setVisible(show);
        });
        slide.play();
    }




}