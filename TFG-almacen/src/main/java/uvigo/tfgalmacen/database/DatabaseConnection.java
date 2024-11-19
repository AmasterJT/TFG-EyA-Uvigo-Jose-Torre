package uvigo.tfgalmacen.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    // Datos de conexión
    private static final String URL = "jdbc:mysql://localhost:3306/tfg_almacenDB"; // URL de tu base de datos
    private static final String USER = "root"; // Tu usuario de MySQL
    private static final String PASSWORD = "Amaster123*"; // Tu contraseña de MySQL

    public static Connection connect() throws SQLException {
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establecer la conexión
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("Conexión exitosa a la base de datos");

            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("Driver no encontrado: " + e.getMessage());
            throw new SQLException("No se pudo cargar el driver de MySQL", e);
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            throw e;
        }
    }

    public static void close(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada");
            }
        } catch (SQLException e) {
            System.err.println("Error cerrando la conexión: " + e.getMessage());
        }
    }

    /*public static void main(String[] args) {
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
    }*/
}
