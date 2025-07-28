package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.database.PedidoDAO;

import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class pedidosController {

    private static final Logger LOGGER = Logger.getLogger(pedidosController.class.getName());


    List<String> ESTADOS_DEL_PEDIDO = List.of("Pendiente", "En proceso", "Cancelado", "Completado");


    @FXML private ListView<?> pedidiosCanceladosList;

    @FXML private ScrollPane pedidiosCanceladosScroll;

    @FXML private ScrollPane pedidiosEnCursoScroll;



    @FXML private ScrollPane pedidiosPendientesScroll;

    @FXML GridPane grid_pendientes;


    @FXML private GridPane grid_en_curso;

    @FXML
    private Button move_to_en_proceso_btn;

    @FXML
    private Button move_to_pendiente_btn;

    @FXML
    private Button ver_detalles_pedido_btn;



    public static  List<ItemPedidoController> allItemControllers = new ArrayList<>();



    /** Número de columnas del grid de pedidos. */
    private final int COLUMS = 1;

    /** Número de filas del grid de pedidos. */
    private final int ROWS = 7;

    /** Número máximo de items a mostrar en el grid. */
    private final int NUM_ITEMS_GRID = COLUMS * ROWS;


    /** Página actual del inventario. */
    private int paginaActual = 0;



    public void initialize() {
        allItemControllers.clear(); // Limpiar la lista para evitar duplicados
        configurarScrollYGrid();
        renderizarPedidos(PedidoDAO.getPedidosPendientes(Main.connection), grid_pendientes);
        renderizarPedidos(PedidoDAO.getPedidosEnProceso(Main.connection), grid_en_curso);

        move_to_en_proceso_btn.setOnAction(_ -> handleMoveToEnProcesoClick());
        ver_detalles_pedido_btn.setOnAction(_ -> verDetallesPedido());
    }


    private void configurarScrollYGrid() {
        pedidiosPendientesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pedidiosPendientesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        grid_pendientes.prefWidthProperty().bind(pedidiosPendientesScroll.widthProperty());
    }

    private void renderizarPedidos(List<Pedido> pedidos, GridPane grid) {
        limpiarGridPane(grid);


        int column = 0, row = 1;
        try {
            for (Pedido pedido : pedidos) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/uvigo/tfgalmacen/itemPedidos.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                ItemPedidoController itemController = fxmlLoader.getController();
                itemController.setData(pedido);

                allItemControllers.add(itemController); // ✅ Guardamos cada controller


                if (column == COLUMS) {
                    column = 0;
                    row++;
                }
                grid.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error cargando los pedidos en el grid", e);
        }

    }

    public void limpiarGridPane(GridPane gridPane) {
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
    }

    private List<Pedido> getPedidosSeleccionadosPendientes() {
        List<Pedido> seleccionados = new ArrayList<>();

        for (ItemPedidoController controller : allItemControllers) {
            if (controller.isSelected()){
                seleccionados.add(controller.getPedido());
            }
        }
        return seleccionados;
    }

    @FXML
    private void handleMoveToEnProcesoClick() {
        List<Pedido> seleccionados = getPedidosSeleccionadosPendientes();
        System.out.println("Pedidos seleccionados:");


        for (Pedido pedido : seleccionados) {
            System.out.println(pedido);
        }
        if (seleccionados.isEmpty()){
            LOGGER.warning("No hay pedidos seleccionados para mover a 'En Proceso'.");

            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText(null); // puedes poner un título más corto aquí si quieres
            alerta.setContentText("No se ha seleccionado ningún pedido");
            alerta.showAndWait();

            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uvigo/tfgalmacen/MovePendienteToEnProceso.fxml"));
            AnchorPane pane = loader.load();

            MovePendienteToEnProcesoController controller = loader.getController();


            // Pasa datos
            controller.setData(seleccionados, Main.connection);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Asignar pedido a usuario");
            stage.setScene(new javafx.scene.Scene(pane));
            stage.show();

            stage.setOnHidden(e -> {
                // Llama al método que redibuja/recarga datos
                this.redibujar();  // o refreshTable(), etc.
            });

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir la ventana de movimiento de pedidos", e);
        }
    }

    private void verDetallesPedido() {
        List<Pedido> seleccionados = getPedidosSeleccionadosPendientes();
        System.out.println("Pedidos seleccionados:");


        for (Pedido pedido : seleccionados) {
            System.out.println(pedido);
        }
        if (seleccionados.isEmpty()){
            LOGGER.warning("No hay pedidos seleccionados para mover a 'En Proceso'.");

            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText(null); // puedes poner un título más corto aquí si quieres
            alerta.setContentText("No se ha seleccionado ningún pedido");
            alerta.showAndWait();

            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uvigo/tfgalmacen/MovePendienteToEnProceso.fxml"));
            AnchorPane pane = loader.load();

            MovePendienteToEnProcesoController controller = loader.getController();


            // Pasa datos
            controller.setData(seleccionados, Main.connection);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Asignar pedido a usuario");
            stage.setScene(new javafx.scene.Scene(pane));
            stage.show();

            stage.setOnHidden(e -> {
                // Llama al método que redibuja/recarga datos
                this.redibujar();  // o refreshTable(), etc.
            });

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir la ventana de movimiento de pedidos", e);
        }
    }

    public void redibujar() {
        // Recargar tabla, combobox, pedidos, etc.
        renderizarPedidos(PedidoDAO.getPedidosPendientes(Main.connection), grid_pendientes);
        renderizarPedidos(PedidoDAO.getPedidosEnProceso(Main.connection), grid_en_curso);
    }
}
