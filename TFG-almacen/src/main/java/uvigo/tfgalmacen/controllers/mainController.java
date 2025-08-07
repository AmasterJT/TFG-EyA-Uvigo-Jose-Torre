package uvigo.tfgalmacen.controllers;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;
import uvigo.tfgalmacen.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class mainController implements Initializable {


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
    private Button recepcionButton;

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
    private Label roleLabel;

    @FXML
    private BorderPane BorderPane;

    @FXML
    private AnchorPane root;

    @FXML
    private AnchorPane almacenContainer;

    @FXML
    AnchorPane Slider;

    @FXML
    private Button ajustesButton;


    private Button activeScene = null;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ExitButton.setOnMouseClicked(_ -> Platform.exit());

        almacenButton.setOnMouseClicked(_ -> loadAlmacenView());

        inventarioButton.setOnMouseClicked(_ -> loadInventarioView());

        pedidosButton.setOnMouseClicked(_ -> loadPedidosView());

        recepcionButton.setOnMouseClicked(_ -> loadRecepcionView());

        ajustesButton.setOnMouseClicked(_ -> loadAjustesView());



        // MenuBackButton.setOnMouseClicked(_ -> slideMenu(false));
        // MenuButton.setOnMouseClicked(_ -> slideMenu(true));

        // Por defecto carga la vista de almacen
        loadAlmacenView();

        if(Main.currentUser != null){
            roleLabel.setText(Main.currentUser.getRole());
        }
        else{
            roleLabel.setText("NO ROL");
        }


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
            Logger.getLogger(mainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadAlmacenView() {
        loadFXML("almacen");
        marcarBotonActivo(almacenButton);
    }

    @FXML
    private void loadInventarioView() {
        loadFXML("inventario");
        marcarBotonActivo(inventarioButton);
    }

    @FXML
    private void loadPedidosView() {
        loadFXML("pedidos");
        marcarBotonActivo(pedidosButton);
    }

    @FXML
    private void loadRecepcionView() {
        loadFXML("recepcion");
        marcarBotonActivo(recepcionButton);
    }

    @FXML
    private void loadAjustesView() {
        loadFXML("ajustes");
        marcarBotonActivo(ajustesButton);
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


    private void marcarBotonActivo(Button botonSeleccionado) {
        // Estilo para botón activo
        String estiloActivo = "-fx-background-color: #2E2E2E; -fx-text-fill: white;";
        // Estilo para botón inactivo (vacío o el que uses por defecto)
        String estiloNormal = "-fx-background-color: #804012; -fx-text-fill: white;";

        // Aplicar estilos condicionalmente
        almacenButton.setStyle(botonSeleccionado == almacenButton ? estiloActivo : estiloNormal);
        inventarioButton.setStyle(botonSeleccionado == inventarioButton ? estiloActivo : estiloNormal);
        pedidosButton.setStyle(botonSeleccionado == pedidosButton ? estiloActivo : estiloNormal);
        recepcionButton.setStyle(botonSeleccionado == recepcionButton ? estiloActivo : estiloNormal);

        // Actualizar referencia del botón activo
        activeScene = botonSeleccionado;
    }


}