package uvigo.tfgalmacen.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.database.PedidoDAO;
import uvigo.tfgalmacen.utils.ColorFormatter;
import uvigo.tfgalmacen.utils.windowComponentAndFuncionalty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.crearStageBasico;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.ventana_warning;

public class pedidosController {

    private static final Logger LOGGER = Logger.getLogger(pedidosController.class.getName());

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


    private static final List<String> ESTADOS_DEL_PEDIDO = List.of("Pendiente", "En proceso", "Cancelado", "Completado");
    private static final String ESTADO_PENDIENTE = ESTADOS_DEL_PEDIDO.getFirst();
    private static final String ESTADO_EN_PROCESO = ESTADOS_DEL_PEDIDO.get(1);

    @FXML
    private ListView<?> pedidiosCanceladosList; // (No usado aquí, pero lo conservo por el FXML)
    @FXML
    private ScrollPane pedidiosCanceladosScroll; // (No usado aquí, pero lo conservo por el FXML)
    @FXML
    private ScrollPane pedidiosEnCursoScroll;
    @FXML
    private ScrollPane pedidiosPendientesScroll;
    @FXML
    private GridPane grid_pendientes;
    @FXML
    private GridPane grid_en_curso;
    @FXML
    private Button move_to_en_proceso_btn;
    @FXML
    private Button move_to_pendiente_btn; // (No usado aquí, pero lo conservo por el FXML)
    @FXML
    private Button ver_detalles_pedido_btn;
    @FXML
    private Button ver_detalles_pedido_pendiente_btn;
    @FXML
    private Button ver_detalles_pedido_en_proceso_btn;
    @FXML
    private Button clear_pendientes_btn;
    @FXML
    private Button clear_en_proceso_btn;

    // Mantengo tu diseño original (estático), pero lo encapsulo
    public static final List<ItemPedidoController> allItemControllers = new ArrayList<>();

    public void initialize() {
        allItemControllers.clear();
        configurarScrollsYGrids();
        redibujar();

        // Handlers null-safe para evitar NPE si el FXML no inyecta el control
        if (move_to_en_proceso_btn != null) {
            move_to_en_proceso_btn.setOnAction(_ -> handleMoveToEnProcesoClick());
        }

        if (ver_detalles_pedido_btn != null) {
            ver_detalles_pedido_btn.setOnAction(_ -> verDetallesPedido());
        }

        if (ver_detalles_pedido_pendiente_btn != null) {
            ver_detalles_pedido_pendiente_btn.setOnAction(_ -> verDetallesPedidoFiltro(ESTADO_PENDIENTE));
        }

        if (ver_detalles_pedido_en_proceso_btn != null) {
            ver_detalles_pedido_en_proceso_btn.setOnAction(_ -> verDetallesPedidoFiltro(ESTADO_EN_PROCESO));
        }

        if (clear_pendientes_btn != null) {
            clear_pendientes_btn.setOnAction(_ -> clearALL(ESTADO_PENDIENTE));
        }

        if (clear_en_proceso_btn != null) {
            clear_en_proceso_btn.setOnAction(_ -> clearALL(ESTADO_EN_PROCESO));
        }
    }

    private void configurarScrollsYGrids() {
        if (pedidiosPendientesScroll != null) {
            pedidiosPendientesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            pedidiosPendientesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            if (grid_pendientes != null) {
                grid_pendientes.prefWidthProperty().bind(pedidiosPendientesScroll.widthProperty());
            }
        }
        if (pedidiosEnCursoScroll != null) {
            pedidiosEnCursoScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            pedidiosEnCursoScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            if (grid_en_curso != null) {
                grid_en_curso.prefWidthProperty().bind(pedidiosEnCursoScroll.widthProperty());
            }
        }
    }

    private void renderizarPedidos(List<Pedido> pedidos, GridPane grid) {
        if (grid == null) return;

        windowComponentAndFuncionalty.limpiarGridPane(grid);

        try {
            int row = 0;
            for (Pedido pedido : pedidos) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/uvigo/tfgalmacen/itemPedidos.fxml"));
                AnchorPane pane = loader.load();

                ItemPedidoController controller = loader.getController();
                controller.setData(pedido);
                allItemControllers.add(controller);

                grid.add(pane, 0, row++);
                GridPane.setMargin(pane, new Insets(10));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error cargando los pedidos en el grid", e);
        }
    }

    @Contract("_ -> new")
    private @NotNull List<Pedido> getPedidosSeleccionados(String estadoFiltro) {
        Set<Pedido> seleccionados = new LinkedHashSet<>();
        for (ItemPedidoController controller : allItemControllers) {
            Pedido pedido = controller.getPedido();

            boolean pasaFiltro = (estadoFiltro == null) ||
                    Objects.equals(estadoFiltro, pedido.getEstado());

            if (controller.isSelected() && pasaFiltro) {
                if (seleccionados.add(pedido)) {
                    controller.setSelected(false); // des-selecciono al recogerlo
                }
            }
        }
        return new ArrayList<>(seleccionados);
    }

    private void clearALL(String estadoFiltro) {
        for (ItemPedidoController controller : allItemControllers) {
            Pedido pedido = controller.getPedido();
            boolean coincide = (estadoFiltro == null) ||
                    Objects.equals(estadoFiltro, pedido.getEstado());

            if (coincide) {
                controller.setSelected(false);
            }
        }
        LOGGER.fine(() -> "Clear selection for estado=" + estadoFiltro);
    }

    private void handleMoveToEnProcesoClick() {
        List<Pedido> pendientes = getPedidosSeleccionados(ESTADO_PENDIENTE);

        if (pendientes.isEmpty()) {
            ventana_warning("Advertencia", null, "No se ha seleccionado ningún pedido pendiente");

            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uvigo/tfgalmacen/MovePendienteToEnProceso.fxml"));
            AnchorPane pane = loader.load();

            MovePendienteToEnProcesoController controller = loader.getController();
            controller.setData(pendientes, Main.connection);


            Stage stage = crearStageBasico("Asignar pedido a usuario", pane, true);
            stage.setOnHidden(_ -> redibujar());
            stage.show();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir la ventana de movimiento de pedidos", e);
        }
    }

    private void verDetallesPedido() {
        List<Pedido> seleccionados = getPedidosSeleccionados(null);

        if (seleccionados.isEmpty()) {
            ventana_warning("Advertencia", null, "No se ha seleccionado ningún pedido");
            return;
        }

        abrirVentanasDetalle(seleccionados);
    }

    private void verDetallesPedidoFiltro(String estadoFiltro) {
        List<Pedido> seleccionados = getPedidosSeleccionados(estadoFiltro);

        if (seleccionados.isEmpty()) {
            ventana_warning("Advertencia", null, "No se ha seleccionado ningún pedido");

            LOGGER.warning(() -> "No se seleccionó ningun pedido");
            return;
        }

        LOGGER.info(() -> "Abrir detalles con filtro estado=" + estadoFiltro);
        abrirVentanasDetalle(seleccionados);
    }

    private void abrirVentanasDetalle(@NotNull List<Pedido> pedidos) {
        for (Pedido pedido : pedidos) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/uvigo/tfgalmacen/detallesPedidoView.fxml"));
                AnchorPane pane = loader.load();

                DetallesPedidoController controller = loader.getController();
                controller.setData(pedido, Main.connection);

                Stage stage = crearStageBasico("Detalles del pedido: " + pedido.getCodigo_referencia(), pane, true);
                stage.setOnHidden(_ -> redibujar());
                stage.show();

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "No se pudo abrir la ventana de detalles del pedido", e);
            }
        }
    }


    public void redibujar() {
        // Redibujar en el hilo de UI para evitar posibles problemas si se llama desde callbacks
        Platform.runLater(() -> {
            allItemControllers.clear();
            renderizarPedidos(PedidoDAO.getPedidosPendientes(Main.connection), grid_pendientes);
            renderizarPedidos(PedidoDAO.getPedidosEnProceso(Main.connection), grid_en_curso);
        });
    }

    private void mostrarAlertaAdvertencia(String mensaje) {
        /*Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
*/
    }
}
