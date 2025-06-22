package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.database.PedidoDAO;

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



    /** Número de columnas del grid de pedidos. */
    private final int COLUMS = 1;

    /** Número de filas del grid de pedidos. */
    private final int ROWS = 7;

    /** Número máximo de items a mostrar en el grid. */
    private final int NUM_ITEMS_GRID = COLUMS * ROWS;


    /** Página actual del inventario. */
    private int paginaActual = 0;



    public void initialize() {
        configurarScrollYGrid();
        renderizarPedidos(PedidoDAO.getPedidosPendientes(Main.connection), grid_pendientes);
        renderizarPedidos(PedidoDAO.getPedidosEnProceso(Main.connection), grid_en_curso);
    }


    private void configurarScrollYGrid() {
        pedidiosPendientesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pedidiosPendientesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        grid_pendientes.prefWidthProperty().bind(pedidiosPendientesScroll.widthProperty());
    }



    private void renderizarPedidos(List<Pedido> pedidos, GridPane grid) {
        limpiarGridPane(grid);
        int totalPedidos = pedidos.size();

        int column = 0, row = 1;
        try {
            for (int i = 0; i < totalPedidos; i++) {
                Pedido pedido = pedidos.get(i);
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/uvigo/tfgalmacen/itemPedidos.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                ItemPedidoController itemController = fxmlLoader.getController();
                itemController.setData(pedido);

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
}
