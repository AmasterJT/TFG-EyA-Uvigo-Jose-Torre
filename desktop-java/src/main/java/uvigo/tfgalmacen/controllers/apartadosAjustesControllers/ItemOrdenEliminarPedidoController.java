package uvigo.tfgalmacen.controllers.apartadosAjustesControllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.Producto;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.database.DetallesPedidoDAO.borrarDetallePorId;
import static uvigo.tfgalmacen.database.DetallesPedidoDAO.updateDetalleProductoYCantidad;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.ventana_error;

public class ItemOrdenEliminarPedidoController {


    private static final Logger LOGGER = Logger.getLogger(ItemOrdenEliminarPedidoController.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new ColorFormatter());
        LOGGER.addHandler(ch);
        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) h.setLevel(Level.ALL);
    }

    private static final String PLACEHOLDER_ITEM_ELIMINAR_PEDIDO = "Seleccionar producto";


    private int id_detalle_pedido = 0;

    @FXML
    private Label producto_eliminar_pedido;
    

    @FXML
    private Label cantidad_producto;


    // Callback inyectado por el padre para eliminar el item del ListView
    private Runnable onRemove;

    public void setOnRemove(Runnable onRemove) {
        this.onRemove = onRemove;
    }

    @FXML
    public void initialize() {
        // Ayuda a detectar mismatches de fx:id al instante:
        assert producto_eliminar_pedido != null : "fx:id 'producto_eliminar_pedido' no fue inyectado. Revisa itemEliminarPedido.fxml";

    }


    // Getters/Setters útiles
    public Label getCantidad_producto() {
        return cantidad_producto;
    }

    public void setCantidad_producto(Label cantidad_producto) {
        this.cantidad_producto = cantidad_producto;
    }


    public Label getProducto_eliminar_pedido() {
        return producto_eliminar_pedido;
    }

    public void setProducto_eliminar_pedido(Label producto_eliminar_pedido) {
        this.producto_eliminar_pedido = producto_eliminar_pedido;
    }


    public int getId_detalle_pedido() {
        return id_detalle_pedido;
    }

    public void setId_detalle_pedido(int id_detalle_pedido) {
        this.id_detalle_pedido = id_detalle_pedido;
    }


}
