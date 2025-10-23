package uvigo.tfgalmacen.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {

    // Colores ANSI para consola
    private static final String RESET = "\033[0m";  // Resetea el color
    private static final String GREEN = "\033[32m"; // Verde
    private static final String RED = "\033[31m";   // Rojo
    private static final String ORANGE = "\033[34m";  // Azul

    static String URL = DataConfig.URL;
    static String USER = DataConfig.USER;
    static String PASSWORD = DataConfig.PASSWORD;
    static String DRIVER = DataConfig.DRIVER;
    public static String DATABASE_NAME = DataConfig.DATABASE_NAME;

    public static Boolean isConnected = false;

    public static Connection connect() throws SQLException {
        try {
            // Cargar el driver de MySQL
            Class.forName(DRIVER);

            // Establecer la conexión
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("✅ " + GREEN + "CONEXIÓN EXITOSA A LA BASE DE DATOS" + RESET);

            isConnected = true;
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver no encontrado: " + e.getMessage());
            throw new SQLException("No se pudo cargar el driver de MySQL", e);
        } catch (SQLException e) {
            System.err.println("❌ Error de conexión: " + e.getMessage());
            throw e;
        }
    }

    public static void close(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                isConnected = false;
                System.out.println("⚠️ " + ORANGE + "CONEXIÓN CERRADA" + RESET);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error cerrando la conexión: " + e.getMessage());
        }
    }

}
