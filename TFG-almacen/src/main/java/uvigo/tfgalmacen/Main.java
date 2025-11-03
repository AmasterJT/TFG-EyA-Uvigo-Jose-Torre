package uvigo.tfgalmacen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.w3c.dom.ls.LSOutput;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.RutasFicheros.WINDOW_LOGIN_FXML;
import static uvigo.tfgalmacen.database.DatabaseConnection.connect;
import static uvigo.tfgalmacen.database.DatabaseConnection.close;
import static uvigo.tfgalmacen.utils.CssColorLoader.loadColors;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.crearStageBasico;

public class Main extends Application {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

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

    public static Map<String, String> colors;


    /**
     * Conexión compartida (si prefieres, muévela a un “ConnectionProvider” o usa un pool).
     */
    public static Connection connection = null;
    public static User currentUser = null;
    public static List<User> allUsers = null;

    /**
     * Offsets para arrastre de ventana.
     */
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void init() {
        // Inicializa la conexión antes de crear UI (init() no corre en el FX thread).
        try {
            connection = connect();

            // (Opcional) Pequeño sanity-check: una query ligera
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("SELECT 1");
            }

            // (Opcional) Carga inicial de datos. Evita tareas pesadas aquí.
            // final List<Pedido> pedidos = PedidoDAO.getPedidosAllData(connection);
            // LOGGER.info(() -> "Pedidos precargados: " + pedidos.size());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inicializando la conexión a BD", e);
        }

        colors = loadColors("/uvigo/tfgalmacen/MFXColors.css");
        System.out.println(colors);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(WINDOW_LOGIN_FXML));
        Parent root = fxmlLoader.load();

        stage = crearStageBasico(root);
        stage.show();
    }

    @Override
    public void stop() {
        // Cierra recursos antes de salir
        close(connection);
        connection = null;
        LOGGER.info("Aplicación detenida y conexión cerrada.");
    }


    public static void main(String[] args) {
        // Si quieres dejar pruebas de consola, protégelas con try/catch y logs.
        // Evita trabajo pesado en el hilo principal; usa init()/start() o tareas en background.
        try {
            // Ejemplo (comentado) de lectura para depurar antes de lanzar UI:
            // Connection tmp = connect();
            // List<Pedido> lista = PedidoDAO.getPedidosAllData(tmp);
            // lista.forEach(p -> LOGGER.info(p::toString));
            // close(tmp);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error en bloque de pre-lanzamiento (opcional)", e);
        }

        launch(); // Arranca JavaFX
    }
}
