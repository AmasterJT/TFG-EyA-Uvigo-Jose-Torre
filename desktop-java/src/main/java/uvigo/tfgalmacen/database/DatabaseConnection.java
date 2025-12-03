package uvigo.tfgalmacen.database;


import uvigo.tfgalmacen.utils.ColorFormatter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());


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

            LOGGER.fine("CONEXIÓN EXITOSA A LA BASE DE DATOS");

            isConnected = true;
            return connection;
        } catch (ClassNotFoundException e) {
            LOGGER.severe("Driver no encontrado: " + e.getMessage());
            throw new SQLException("No se pudo cargar el driver de MySQL", e);
        } catch (SQLException e) {
            LOGGER.severe("Error de conexión: \n" + e.getMessage());
            throw e;
        }
    }

    public static void close(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                isConnected = false;
                LOGGER.warning("CONEXIÓN CERRADA");
            }
        } catch (SQLException e) {
            LOGGER.severe("Error cerrando la conexión: \" + e.getMessage()");
        }
    }

}
