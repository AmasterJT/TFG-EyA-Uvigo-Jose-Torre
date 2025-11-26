package uvigo.tfgalmacen.controllers;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.database.DatabaseConnection;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.*;

import static uvigo.tfgalmacen.database.TableLister.getTables;
import static uvigo.tfgalmacen.utils.ExcelGenerator.exportarTodasLasTablasAExcel;

public class exportDataController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(exportDataController.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new ColorFormatter());
        LOGGER.addHandler(ch);

        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            h.setLevel(Level.ALL);
        }
    }

    private static final String PLACEHOLDER_NOMBRE_FICHERO = "DataAlmacen";

    @FXML
    private Button ExitButton;


    @FXML
    private Button abrir_explorador_btn;

    @FXML
    private TextField carpeta_destino_text;

    @FXML
    private Button exportar_btn;

    @FXML
    private TextField nombre_fichero_text;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });

        // --- Carpeta inicial por defecto → Descargas ---
        carpeta_destino_text.setText(getCarpetaDescargas());

        abrir_explorador_btn.setOnAction(_ -> seleccionarCarpeta());
        abrir_explorador_btn.setTooltip(new Tooltip("Seleccionar carpeta"));

        exportar_btn.setOnMouseClicked(_ -> {
            try {
                exportarData();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error en exportación", e);
            }
        });

        nombre_fichero_text.setText(PLACEHOLDER_NOMBRE_FICHERO);
    }

    // ============================================================
    //   FUNCIONES PRINCIPALES
    // ============================================================

    private void seleccionarCarpeta() {
        Stage stage = (Stage) abrir_explorador_btn.getScene().getWindow();

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Seleccionar carpeta de destino");

        File initial = new File(getCarpetaDescargas());
        if (!initial.exists()) {
            initial = new File(System.getProperty("user.home"));
        }
        chooser.setInitialDirectory(initial);

        File carpeta = chooser.showDialog(stage);

        if (carpeta != null) {
            carpeta_destino_text.setText(carpeta.getAbsolutePath());
            LOGGER.info("Carpeta seleccionada: " + carpeta.getAbsolutePath());
        }
    }


    private void exportarData() throws SQLException {

        // Validar carpeta destino
        String rutaCarpeta = carpeta_destino_text.getText();
        if (rutaCarpeta == null || rutaCarpeta.isBlank()) {
            LOGGER.warning("No se seleccionó carpeta destino");
            return;
        }

        File carpeta = new File(rutaCarpeta);
        if (!carpeta.exists()) {
            LOGGER.warning("Ruta no válida: " + rutaCarpeta);
            return;
        }

        // Obtener nombre del archivo
        String nombreArchivo = nombre_fichero_text.getText();
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            nombreArchivo = PLACEHOLDER_NOMBRE_FICHERO;
        }

        if (!nombreArchivo.toLowerCase().endsWith(".xlsx")) {
            nombreArchivo += ".xlsx";
        }

        File destino = new File(carpeta, nombreArchivo);

        LOGGER.info("Exportando a: " + destino.getAbsolutePath());

        // Obtener tablas de MySQL
        List<String> tablas = getTables(Main.connection, DatabaseConnection.DATABASE_NAME);

        try {
            exportarTodasLasTablasAExcel(Main.connection, tablas, destino);
            LOGGER.info("Exportación finalizada correctamente.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error exportando datos", e);
        }
    }


    // ============================================================
    //   UTILIDADES
    // ============================================================

    private String getCarpetaDescargas() {
        return System.getProperty("user.home") + File.separator + "Downloads";
    }
}
