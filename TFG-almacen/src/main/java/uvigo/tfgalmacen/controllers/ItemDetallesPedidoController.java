package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.ProductoPedido;

import static java.lang.Integer.parseInt;
import static uvigo.tfgalmacen.database.PedidoDAO.getPaletsDelPedido;

public class ItemDetallesPedidoController {
    boolean esta_en_palet = false;
    int id_BDD;
    @FXML
    private Label cantidad_detalle_pedido_label;

    @FXML
    private Label producto_detalle_pedido_label;

    @FXML
    private CheckBox producto_listo_en_pedido_check;

    @FXML
    private Circle tipo_producto_color_ldentifier;

    @FXML
    private VBox vertical_container;

    @FXML
    private Label palets_pedido_label;

    public Label getCantidad_detalle_pedido_label() {
        return cantidad_detalle_pedido_label;
    }

    public void setCantidad_detalle_pedido_label(Label cantidad_detalle_pedido_label) {
        this.cantidad_detalle_pedido_label = cantidad_detalle_pedido_label;
    }

    public Label getProducto_detalle_pedido_label() {
        return producto_detalle_pedido_label;
    }

    public void setProducto_detalle_pedido_label(Label producto_detalle_pedido_label) {
        this.producto_detalle_pedido_label = producto_detalle_pedido_label;
    }

    public CheckBox getProducto_listo_en_pedido_check() {
        return producto_listo_en_pedido_check;
    }

    public void setProducto_listo_en_pedido_check(CheckBox producto_listo_en_pedido_check) {
        this.producto_listo_en_pedido_check = producto_listo_en_pedido_check;
    }


    public void setData(ProductoPedido productoDelPedido) {
        producto_detalle_pedido_label.setText(productoDelPedido.getIdentificadorProducto());
        cantidad_detalle_pedido_label.setText(String.valueOf(productoDelPedido.getCantidad()));
        id_BDD = productoDelPedido.getId_detalle_BDD();
        

        tipo_producto_color_ldentifier.setFill(Paint.valueOf(productoDelPedido.colorHEX));
        producto_listo_en_pedido_check.setSelected(productoDelPedido.isComplete);
    }
}
