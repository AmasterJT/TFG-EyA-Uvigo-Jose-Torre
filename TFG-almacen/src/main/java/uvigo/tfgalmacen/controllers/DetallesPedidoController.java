package uvigo.tfgalmacen.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.ProductoPedido;
import uvigo.tfgalmacen.database.PedidoDAO;
import uvigo.tfgalmacen.windowComponentAndFuncionalty;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static uvigo.tfgalmacen.database.DetallesPedidoDAO.getProductosPorCodigoReferencia;
import static uvigo.tfgalmacen.utils.TerminalColors.*;
import static uvigo.tfgalmacen.windowComponentAndFuncionalty.limpiarGridPane;

public class DetallesPedidoController {

    @FXML
    private Button ExitButton;

    @FXML
    private Button aplicar_nuevo_estado_btn;

    @FXML
    private Button aplicar_nuevo_estado_btn1;

    @FXML
    private GridPane grid_pendientes;

    @FXML
    private ScrollPane pedidiosEnCursoScroll;

    @FXML
    private HBox windowBar;

    private Pedido pedido_para_detallar;

    @FXML
    private Label codigo_referencia_pedido_detalle_label;

    @FXML
    private Label estado_pedido_detalle_label;


    private List <ProductoPedido> productos_del_pedido;

    /** Número de columnas del grid de pedidos. */
    private final int COLUMS = 1;


    public void initialize() {
        // Llenar usuarios desde la base de datos

        ExitButton.setOnMouseClicked(event -> {

            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });


    }


    public void setData(Pedido pedido_para_detallar, Connection connection)  {
        this.pedido_para_detallar = pedido_para_detallar;

        codigo_referencia_pedido_detalle_label.setText(pedido_para_detallar.getCodigo_referencia());
        estado_pedido_detalle_label.setText(pedido_para_detallar.getEstado());

        productos_del_pedido = getProductosPorCodigoReferencia(Main.connection, pedido_para_detallar.getCodigo_referencia());

        renderizarProductos(productos_del_pedido, grid_pendientes);

    }

    public static  List<ItemDetallesPedidoController> allItemControllers = new ArrayList<>();

    private void renderizarProductos(List <ProductoPedido> productos_del_pedido, GridPane grid) {
        limpiarGridPane(grid);


        int column = 0, row = 1;
        try {
            for (ProductoPedido procuto_del_pedido : productos_del_pedido) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/uvigo/tfgalmacen/itemDetallesPedido.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                ItemDetallesPedidoController itemController = fxmlLoader.getController();
                itemController.setData(procuto_del_pedido);

                allItemControllers.add(itemController); // ✅ Guardamos cada controller

                if (column == COLUMS) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
