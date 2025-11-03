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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.RutasFicheros.*;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.crearStageBasico;

public class mainController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(mainController.class.getName());

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
    private Button minimizeButton;

    @FXML
    private Label MenuButton;
    @FXML
    private Label MenuBackButton;

    @FXML
    private Label roleLabel;

    @FXML
    private BorderPane BorderPane;

    @FXML
    AnchorPane Slider;

    @FXML
    private AnchorPane root;

    @FXML
    private Button ajustesButton;
    @FXML
    private Button orden_compra_btn;

    private Button activeScene = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ExitButton.setOnMouseClicked(_ -> Platform.exit());
        minimizeButton.setOnAction(_ -> minimizarVentana());


        almacenButton.setOnMouseClicked(_ -> loadAlmacenView());
        inventarioButton.setOnMouseClicked(_ -> loadInventarioView());
        pedidosButton.setOnMouseClicked(_ -> loadPedidosView());
        recepcionButton.setOnMouseClicked(_ -> loadRecepcionView());
        ajustesButton.setOnMouseClicked(_ -> loadAjustesView());
        orden_compra_btn.setOnMouseClicked(_ -> abrirVentanaOrdenCompra());

        // Por defecto carga la vista de almacen
        loadAlmacenView();

        if (Main.currentUser != null) {
            roleLabel.setText(Main.currentUser.getRole());
        } else {
            roleLabel.setText("NO ROL");
        }
    }

    private void minimizarVentana() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    private void abrirVentanaOrdenCompra() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(WINDOW_ORDEN_COMPRA_FXML));
            Parent root = loader.load();

            Stage ventanaOrdenCompra = crearStageBasico(root, "Orden de compra");

            Stage ventanaPadre = (Stage) orden_compra_btn.getScene().getWindow();


            // Bloquear la ventana padre
            ventanaOrdenCompra.initOwner(ventanaPadre);
            ventanaOrdenCompra.initModality(Modality.WINDOW_MODAL);
            ventanaOrdenCompra.initStyle(StageStyle.TRANSPARENT);


            ventanaOrdenCompra.showAndWait();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir la ventana de orden de compra", e);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //                                   APARTADOS DEL MAIN CONTROLLER
    //------------------------------------------------------------------------------------------------------------------

    @FXML
    private void loadFXML(String PATH) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH));
            Parent parent = loader.load();
            BorderPane.setCenter(parent);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error cargando FXML: " + PATH, ex);
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void loadAlmacenView() {
        loadFXML(APARTADO_ALMACEN_FXML);
        marcarBotonActivo(almacenButton);
    }

    @FXML
    private void loadInventarioView() {
        loadFXML(APARTADO_INVENTARIO_FXML);
        marcarBotonActivo(inventarioButton);
    }

    @FXML
    private void loadPedidosView() {
        loadFXML(APARTADO_PEDIDOS_FXML);
        marcarBotonActivo(pedidosButton);
    }

    @FXML
    private void loadRecepcionView() {
        loadFXML(APARTADO_RECEPCION_FXML);
        marcarBotonActivo(recepcionButton);
    }

    @FXML
    private void loadAjustesView() {
        loadFXML(APARTADO_AJUSTES_FXML);
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
            LOGGER.warning("Directorio no encontrado: " + folder.getAbsolutePath());
            return;
        }

        File[] xmlFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));

        if (xmlFiles == null || xmlFiles.length == 0) {
            LOGGER.info("No se encontraron archivos XML en: " + folder.getAbsolutePath());
            return;
        }

        try {
            for (File xmlFile : xmlFiles) {
                String command = excelPath + " \"" + xmlFile.getAbsolutePath() + "\"";
                Runtime.getRuntime().exec(command);
                LOGGER.fine("Abriendo en Excel: " + xmlFile.getName());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error abriendo XML en Excel", e);
        }
    }

    private void slideMenu(boolean show) {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.4), Slider);
        slide.setToX(show ? 0 : -176);
        slide.setOnFinished(_ -> {
            MenuButton.setVisible(!show);
            MenuBackButton.setVisible(show);
        });
        slide.play();
    }

    private void marcarBotonActivo(Button botonSeleccionado) {
        // Estilo para botón activo

        String colorActivo = Main.colors.get("-amaster-dark-brown");
        String estiloActivo = String.format("-fx-background-color: %s !important; -fx-text-fill: white;", colorActivo);
        // Estilo para botón inactivo
        String colorNormal = Main.colors.get("-amaster-brown");
        String estiloNormal = String.format("-fx-background-color: %s !important; -fx-text-fill: white;", colorNormal);

        // Aplicar estilos condicionalmente
        almacenButton.setStyle(botonSeleccionado == almacenButton ? estiloActivo : estiloNormal);
        inventarioButton.setStyle(botonSeleccionado == inventarioButton ? estiloActivo : estiloNormal);
        pedidosButton.setStyle(botonSeleccionado == pedidosButton ? estiloActivo : estiloNormal);
        recepcionButton.setStyle(botonSeleccionado == recepcionButton ? estiloActivo : estiloNormal);

        // Actualizar referencia del botón activo
        activeScene = botonSeleccionado;
    }
}
