package uvigo.tfgalmacen.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    static String URL =  DataConfig.URL;
    static String USER =  DataConfig.USER;
    static String PASSWORD =  DataConfig.PASSWORD;

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

}
