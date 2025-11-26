package uvigo.tfgalmacen.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.database.PaletSalidaDAO;
import uvigo.tfgalmacen.database.PedidoDAO;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.RutasFicheros.*;
import static uvigo.tfgalmacen.database.PedidoDAO.getCodigoReferenciaById;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.crearStageBasico;

public class envioController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(envioController.class.getName());

    // Sustituye virtual threads por un pool normal:
    private static final ExecutorService FX_BG_EXEC = Executors.newFixedThreadPool(4);  // por ejemplo

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

    private final List<ItemEnvioController> listaItemsEnvio = new ArrayList<>();

    @FXML
    private Button generar_etiquetas_btn;

    @FXML
    private GridPane grid_envio;

    @FXML
    private ScrollPane pedidosEnCursoPrimeraHoraScroll; // scroll del envío

    @FXML
    private Button enviar_pedido;
    // nº de columnas "útiles" para palets (además de la columna 0 con el id_pedido)
    // Realmente no hace falta fijar un máximo, el GridPane crece según uses columnas.
    // Lo dejo como constante por si luego quieres reordenar o cambiar diseño.
    private static final int COLUMNS_START_PALET = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarScrollYGrid();
        cargarPaletsSalidaEnGrid();

        generar_etiquetas_btn.setOnAction(_ -> procesarEtiquetasSeleccionadas());

        enviar_pedido.setOnAction(_ -> abrirVetanaEnviarPedido());
    }

    private void abrirVetanaEnviarPedido() {

        Stage owner = (Stage) enviar_pedido.getScene().getWindow();
        openWindowAsync(WINDOW_GENERAR_ENVIO_FXML, "Crear nuevo porducto", owner);
    }

    private void openWindowAsync(String fxmlPath, String title, Stage owner) {
        // Creamos el loader AQUÍ para poder usarlo luego
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

        Task<Parent> task = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                // Cargamos el FXML en background
                return loader.load();
            }
        };

        task.setOnSucceeded(_ -> {
            try {
                Parent root = task.getValue();

                // <<< OBTENEMOS EL CONTROLLER DE LA VENTANA HIJA >>>
                windowGenerarPedidoController controllerHijo = loader.getController();
                if (controllerHijo != null) {
                    controllerHijo.setEnvioParent(this);  // pasamos referencia al padre
                }

                Stage win = crearStageBasico(root, true, title);
                if (owner != null) {
                    win.initOwner(owner);
                    win.initModality(Modality.WINDOW_MODAL);
                    win.initStyle(StageStyle.TRANSPARENT);
                }
                win.showAndWait();
                LOGGER.fine(() -> "Ventana abierta: " + title);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al inicializar ventana hija: " + title, e);
            }
        });

        task.setOnFailed(_ -> {
            Throwable ex = task.getException();
            LOGGER.log(Level.SEVERE, "No se pudo abrir la ventana: " + title, ex);
            ex.printStackTrace();
        });

        FX_BG_EXEC.submit(task);
    }

    private void limpiarGrid(GridPane grid) {
        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
    }

    private void configurarScrollYGrid() {
        if (pedidosEnCursoPrimeraHoraScroll != null) {
            pedidosEnCursoPrimeraHoraScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            pedidosEnCursoPrimeraHoraScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            if (grid_envio != null) {
                grid_envio.prefWidthProperty().bind(pedidosEnCursoPrimeraHoraScroll.widthProperty());
                grid_envio.setHgap(10);
                grid_envio.setVgap(10);
            }
        }
    }

    private void procesarEtiquetasSeleccionadas() {
        System.out.println("---- Etiquetas seleccionadas ----");

        boolean alguno = false;

        for (ItemEnvioController item : listaItemsEnvio) {
            if (item.estaSeleccionado()) { // función que añadiremos abajo
                System.out.println("esta seleccionado → SSCC: " + item.getSscc());
                item.getGenerar_etiqueta_btn().fire();
                alguno = true;
            }
        }

        if (!alguno) {
            System.out.println("Ningún item seleccionado.");
        }
    }


    private void cargarPaletsSalidaEnGrid() {
        limpiarGrid(grid_envio);
        listaItemsEnvio.clear();   // IMPORTANTE: limpiar también la lista

        Connection conn = Main.connection;
        Map<Integer, List<Integer>> mapa =
                PaletSalidaDAO.getPaletsSalidaAgrupadosPorPedido(conn);

        int row = 0;

        for (Map.Entry<Integer, List<Integer>> entry : mapa.entrySet()) {
            int idPedido = entry.getKey();
            List<Integer> paletsSalida = entry.getValue();

            String codigo_referencia_pedido = getCodigoReferenciaById(Main.connection, idPedido);
            Label lblPedido = new Label(codigo_referencia_pedido);
            lblPedido.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white");

            grid_envio.add(lblPedido, 0, row);
            GridPane.setMargin(lblPedido, new Insets(10, 10, 10, 10));

            int col = 1;
            for (Integer idPaletSalida : paletsSalida) {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource(ITEM_ENVIO_FXML)
                    );
                    AnchorPane itemRoot = loader.load();

                    ItemEnvioController ctrl = loader.getController();
                    listaItemsEnvio.add(ctrl);

                    ctrl.setData(
                            Objects.requireNonNull(PedidoDAO.getPedidoPorCodigo(Main.connection, codigo_referencia_pedido)),
                            Objects.requireNonNull(PaletSalidaDAO.getPaletSalidaById(Main.connection, idPaletSalida))
                    );

                    grid_envio.add(itemRoot, col, row);
                    GridPane.setMargin(itemRoot, new Insets(10));
                    col++;

                } catch (Exception e) {
                    System.err.println("Error cargando ItemEnvio: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            row++;
        }
    }

    public void refrescarGridEnvio() {
        cargarPaletsSalidaEnGrid();
    }


    /**
     * Devuelve true si TODOS los palets_salida del pedido indicado
     * tienen ya su etiqueta generada (tieneEtiqueta == true).
     * Si no hay ningún palet para ese pedido, devuelve false.
     */
    public boolean tieneTodasEtiquetasParaPedido(int idPedido) {
        // Filtramos los items que pertenecen a ese pedido
        List<ItemEnvioController> itemsDePedido = listaItemsEnvio.stream()
                .filter(it -> it.getIdPedido() == idPedido)
                .toList();

        if (itemsDePedido.isEmpty()) {
            // no hay palets_salida para este pedido → no se puede enviar
            System.out.println("No hay palets_salida en el grid para el pedido id=" + idPedido);
            return false;
        }

        // Todos deben tener etiqueta
        for (ItemEnvioController it : itemsDePedido) {
            if (!it.isTieneEtiqueta()) {
                System.out.println("Pedido id=" + idPedido +
                        " aún tiene palets sin etiqueta. Ejemplo SSCC=" + it.getSscc());
                return false;
            }
        }
        return true;
    }
}
