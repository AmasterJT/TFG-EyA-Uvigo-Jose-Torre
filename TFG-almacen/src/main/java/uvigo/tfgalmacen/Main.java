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

import uvigo.tfgalmacen.database.ProductoDAO;
import static uvigo.tfgalmacen.database.DatabaseConnection.*;
import static uvigo.tfgalmacen.database.RolePermissionDAO.printRolesAndPermissions;

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

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        Connection connection = null;
        try {
            // Establecer conexión
            connection = connect();

            // Aquí puedes ejecutar consultas a la base de datos, por ejemplo:
            Statement stmt = connection.createStatement();
            String query = "SELECT * FROM Usuarios";
            stmt.executeQuery(query);

            ProductoDAO.readProductos(connection);
            printRolesAndPermissions(connection);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection);  // Cerrar la conexión
        }


        launch();
    }
}