package uvigo.tfgalmacen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import uvigo.tfgalmacen.database.ProductoDAO;
import uvigo.tfgalmacen.utils.ColorFormatter;
import uvigo.tfgalmacen.utils.WindowResizer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.database.DatabaseConnection.connect;
import static uvigo.tfgalmacen.database.DatabaseConnection.close;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.crearStageBasico;

import uvigo.tfgalmacen.database.PedidoDAO;

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
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/uvigo/tfgalmacen/loginWindow.fxml"));
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

    /**
     * Permite mover la ventana arrastrando un contenedor con fx:id="windowBar".
     * Si el nodo no existe, registra un warning y continúa (para evitar romper la app).
     */
    private void WindowMovement(Parent root, Stage stage) {
        HBox windowBar = null;
        try {
            windowBar = (HBox) root.lookup("#windowBar");
        } catch (Exception e) {
            // lookup lanzó algo raro; lo registramos, pero no detenemos la app
            LOGGER.log(Level.WARNING, "Error al hacer lookup de #windowBar", e);
        }

        if (windowBar == null) {
            LOGGER.warning("No se encontró el nodo con fx:id='windowBar'. El arrastre de ventana estará deshabilitado.");
            return;
        }

        windowBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        windowBar.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
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
