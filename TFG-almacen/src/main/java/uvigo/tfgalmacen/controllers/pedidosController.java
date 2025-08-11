package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.database.PedidoDAO;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.WindowMovement;

public class pedidosController {

    private static final Logger LOGGER = Logger.getLogger(pedidosController.class.getName());

    private static final List<String> ESTADOS_DEL_PEDIDO = List.of("Pendiente", "En proceso", "Cancelado", "Completado");

    @FXML private ListView<?> pedidiosCanceladosList;
    @FXML private ScrollPane pedidiosCanceladosScroll;
    @FXML private ScrollPane pedidiosEnCursoScroll;
    @FXML private ScrollPane pedidiosPendientesScroll;
    @FXML private GridPane grid_pendientes;
    @FXML private GridPane grid_en_curso;
    @FXML private Button move_to_en_proceso_btn;
    @FXML private Button move_to_pendiente_btn;
    @FXML private Button ver_detalles_pedido_btn;

    public static final List<ItemPedidoController> allItemControllers = new ArrayList<>();

    private static final int COLUMNS = 1;
    private static final int ROWS = 7;

    public void initialize() {
        allItemControllers.clear();
        configurarScrollYGrid();
        redibujar();

        move_to_en_proceso_btn.setOnAction(_ -> handleMoveToEnProcesoClick());
        ver_detalles_pedido_btn.setOnAction(_ -> verDetallesPedido());
    }



    private void configurarScrollYGrid() {
        pedidiosPendientesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pedidiosPendientesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        grid_pendientes.prefWidthProperty().bind(pedidiosPendientesScroll.widthProperty());
    }

    private void renderizarPedidos(List<Pedido> pedidos, GridPane grid) {
        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();

        int column = 0, row = 0;
        try {
            for (Pedido pedido : pedidos) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/uvigo/tfgalmacen/itemPedidos.fxml"));
                AnchorPane pane = loader.load();

                ItemPedidoController controller = loader.getController();
                controller.setData(pedido);
                allItemControllers.add(controller);

                grid.add(pane, column, row++);
                GridPane.setMargin(pane, new Insets(10));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error cargando los pedidos en el grid", e);
        }
    }

    private List<Pedido> getPedidosSeleccionados(String estadoFiltro) {
        List<Pedido> seleccionados = new ArrayList<>();
        for (ItemPedidoController controller : allItemControllers) {
            Pedido pedido = controller.getPedido();
            if (controller.isSelected() &&
                    (estadoFiltro == null || estadoFiltro.equals(pedido.getEstado())) &&
                    !seleccionados.contains(pedido)) {

                seleccionados.add(pedido);
                controller.setSelected(false);
            }
        }
        return seleccionados;
    }

    private void handleMoveToEnProcesoClick() {
        List<Pedido> pendientes = getPedidosSeleccionados("Pendiente");

        if (pendientes.isEmpty()) {
            mostrarAlertaAdvertencia("No se ha seleccionado ningún pedido pendiente");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uvigo/tfgalmacen/MovePendienteToEnProceso.fxml"));
            AnchorPane pane = loader.load();

            MovePendienteToEnProcesoController controller = loader.getController();
            controller.setData(pendientes, Main.connection);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Asignar pedido a usuario");
            stage.setScene(new javafx.scene.Scene(pane));
            stage.setOnHidden(e -> redibujar());
            stage.show();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir la ventana de movimiento de pedidos", e);
        }
    }

    private void verDetallesPedido() {
        List<Pedido> seleccionados = getPedidosSeleccionados(null);

        if (seleccionados.isEmpty()) {
            mostrarAlertaAdvertencia("No se ha seleccionado ningún pedido");
            return;
        }

        for (Pedido pedido : seleccionados) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/uvigo/tfgalmacen/detallesPedidoView.fxml"));
                AnchorPane pane = loader.load();

                DetallesPedidoController controller = loader.getController();
                controller.setData(pedido, Main.connection);

                Stage stage = new Stage();
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setTitle("Detalles del pedido: " + pedido.getCodigo_referencia());
                stage.setScene(new javafx.scene.Scene(pane));
                WindowMovement(pane, stage);
                stage.setOnHidden(e -> redibujar());
                stage.show();

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "No se pudo abrir la ventana de detalles del pedido", e);
            }
        }
    }

    public void redibujar() {
        renderizarPedidos(PedidoDAO.getPedidosPendientes(Main.connection), grid_pendientes);
        renderizarPedidos(PedidoDAO.getPedidosEnProceso(Main.connection), grid_en_curso);
    }

    private void mostrarAlertaAdvertencia(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}
