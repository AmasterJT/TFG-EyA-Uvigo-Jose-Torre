package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.models.Pedido;
import uvigo.tfgalmacen.models.ProductoPedido;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.RutasFicheros.ITEM_DETALLE_PEDIDO_FXML;
import static uvigo.tfgalmacen.database.DetallesPedidoDAO.actualizarEstadoProductoPedido;
import static uvigo.tfgalmacen.database.DetallesPedidoDAO.getProductosPorCodigoReferencia;
import static uvigo.tfgalmacen.database.PedidoDAO.*;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.*;

public class windowDetallesPedidoController {

    // 🧩 Logger con colores personalizados
    private static final Logger LOGGER = Logger.getLogger(windowDetallesPedidoController.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new ColorFormatter());
        LOGGER.addHandler(consoleHandler);

        // Opcional: ajustar también el root logger (evita silencios en handlers)
        for (Handler h : Logger.getLogger("").getHandlers()) {
            h.setLevel(Level.ALL);
        }
    }

    @FXML
    private Button ExitButton;

    @FXML
    private Button minimizeButton;

    @FXML
    private Button aplicar_cambios_detalle_pedido_btn;

    @FXML
    private GridPane grid_pendientes;


    @FXML
    private Label codigo_referencia_pedido_detalle_label;

    @FXML
    private Label estado_pedido_detalle_label;

    @FXML
    private Label palets_pedido_label;

    private Pedido pedido_para_detallar;
    /**
     * Número de columnas del grid de pedidos.
     */
    private final int COLUMS = 1;

    public static List<ItemDetallesPedidoController> allItemControllers = new ArrayList<>();


    public void initialize() {
        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            LOGGER.info("Ventana de detalles de pedido cerrada.");
            stage.close();
        });

        minimizeButton.setOnAction(_ -> minimizarVentana());
        aplicar_cambios_detalle_pedido_btn.setOnAction(_ -> actualizarDetallePedidos());


    }


    private void minimizarVentana() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);

    }

    private void actualizarDetallePedidos() {
        System.out.println("click");
        for (ItemDetallesPedidoController itemController : allItemControllers) {
            System.out.println("Detalle: " + itemController.id_BDD + " -> " + itemController.getProducto_listo_en_pedido_check().isSelected());

            actualizarEstadoProductoPedido(Main.connection, itemController.id_BDD, itemController.getProducto_listo_en_pedido_check().isSelected());
        }


        ventana_success("Productos actualizados correctamente", "Los cambios en los productos del pedido se han aplicado con éxito.", "Información");

        Stage stage = (Stage) aplicar_cambios_detalle_pedido_btn.getScene().getWindow();
        stage.close();
    }

    public void setData(Pedido pedido_para_detallar) {
        this.pedido_para_detallar = pedido_para_detallar;

        codigo_referencia_pedido_detalle_label.setText(pedido_para_detallar.getCodigo_referencia());
        estado_pedido_detalle_label.setText(pedido_para_detallar.getEstado());

        List<ProductoPedido> productos_del_pedido = getProductosPorCodigoReferencia(
                Main.connection,
                pedido_para_detallar.getCodigo_referencia()
        );

        palets_pedido_label.setText(String.valueOf(getPaletsDelPedido(Main.connection, pedido_para_detallar.getId_pedido())));

        LOGGER.info("Renderizando productos del pedido: " + pedido_para_detallar.getCodigo_referencia());
        renderizarProductos(productos_del_pedido, grid_pendientes);

    }

    private void renderizarProductos(List<ProductoPedido> productos_del_pedido, GridPane grid) {
        limpiarGridPane(grid);
        int column = 0, row = 1;

        try {
            for (ProductoPedido producto_del_pedido : productos_del_pedido) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ITEM_DETALLE_PEDIDO_FXML));
                AnchorPane anchorPane = fxmlLoader.load();

                ItemDetallesPedidoController itemController = fxmlLoader.getController();
                itemController.setData(producto_del_pedido);
                allItemControllers.add(itemController);


                if (column == COLUMS) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            }

            LOGGER.fine("Productos renderizados correctamente: " + productos_del_pedido.size());

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar itemDetallesPedido.fxml o al renderizar productos.", e);
            e.printStackTrace();
        }
    }
}
