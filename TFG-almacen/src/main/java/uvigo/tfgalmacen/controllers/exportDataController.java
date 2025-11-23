package uvigo.tfgalmacen.controllers;

import javafx.fxml.Initializable;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.database.DatabaseConnection;
import uvigo.tfgalmacen.utils.ColorFormatter;
import uvigo.tfgalmacen.utils.ExcelGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private AnchorPane Pane;

    @FXML
    private Button exportar_btn;

    @FXML
    private TextField nombre_fichero_text;

    @FXML
    private HBox windowBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });


        exportar_btn.setOnMouseClicked(_ -> {
            try {
                exportarData();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        nombre_fichero_text.setPromptText(PLACEHOLDER_NOMBRE_FICHERO);
    }

    private void exportarData() throws SQLException {

        List<String> tablesNames = getTables(Main.connection, DatabaseConnection.DATABASE_NAME);
        System.out.println(tablesNames);

        // Nombre base del fichero
        String nombre = nombre_fichero_text.getText();
        if (nombre == null || nombre.isBlank()) {
            nombre = PLACEHOLDER_NOMBRE_FICHERO; // "DataAlmacen"
        }

        // Asegurar extensión .xlsx
        if (!nombre.toLowerCase().endsWith(".xlsx")) {
            nombre += ".xlsx";
        }

        Stage stage = (Stage) exportar_btn.getScene().getWindow();

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Seleccionar carpeta de destino");

        File initialDir = new File("export/");
        if (!initialDir.exists() || !initialDir.isDirectory()) {
            initialDir = new File(System.getProperty("user.home"));
        }
        chooser.setInitialDirectory(initialDir);

        File carpeta = chooser.showDialog(stage);
        if (carpeta == null) {
            LOGGER.info("Exportación cancelada: no se seleccionó carpeta.");
            return;
        }

        // Ahora sí: destino es un FICHERO dentro de la carpeta
        File destino = new File(carpeta, nombre);

        try {
            exportarTodasLasTablasAExcel(Main.connection, tablesNames, destino);
            LOGGER.info("Exportación completa a: " + destino.getAbsolutePath());
            // aquí puedes lanzar tu ventana_warning de éxito
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error exportando base de datos a Excel", e);
            e.printStackTrace();
        }
    }


    private File elegirCarpeta(Stage owner) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Selecciona carpeta de destino");
        chooser.setInitialDirectory(new File("export/"));
        return chooser.showDialog(owner);
    }


}
