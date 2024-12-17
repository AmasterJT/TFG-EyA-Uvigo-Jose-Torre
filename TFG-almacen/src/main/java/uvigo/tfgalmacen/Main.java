package uvigo.tfgalmacen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.stage.StageStyle;
import uvigo.tfgalmacen.database.DatabaseConnection;
import uvigo.tfgalmacen.database.ProductoDAO;

import static uvigo.tfgalmacen.dataTransform.DataExcelExporter.exportDatabaseTablesToXML;
import static uvigo.tfgalmacen.dataTransform.DataExcelExporter.exportDatabaseToXML;
import static uvigo.tfgalmacen.database.DatabaseConnection.*;
import static uvigo.tfgalmacen.database.RolePermissionDAO.printRolesAndPermissions;
import static uvigo.tfgalmacen.database.TableLister.listTables;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        if (!isConnected){
            // Crear un cuadro de diálogo de información
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Información");
            alert.setContentText("No se ha establecido la conexión a la base de datos");

            // Mostrar el cuadro de diálogo y esperar a que el usuario lo cierre
            alert.showAndWait();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));

        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        Connection connection;
        try {
            // Establecer conexión
            connection = connect();

            // Aquí puedes ejecutar consultas a la base de datos, por ejemplo:
            Statement stmt = connection.createStatement();
            String query = "SELECT * FROM usuarios";
            stmt.executeQuery(query);

            ProductoDAO.readProductos(connection);
            printRolesAndPermissions(connection);

            // Listar las tablas de la base de datos
            listTables(connection, DatabaseConnection.DATABASE_NAME);

            // Exportar datos de la tabla "Pedidos" a un archivo "pedidos.xml"
            exportDatabaseTablesToXML(connection);
            exportDatabaseToXML(connection);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            System.out.println("deberiamos cerrar la conexion aqui");
            //close(connection);  // Cerrar la conexión
        }

        launch(); // lanzamos la aplicacion grafica
    }
}