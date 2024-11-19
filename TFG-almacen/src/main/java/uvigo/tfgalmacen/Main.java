package uvigo.tfgalmacen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static uvigo.tfgalmacen.database.DatabaseConnection.*;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {


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

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar la conexión
            close(connection);
        }

        

        launch();
    }
}