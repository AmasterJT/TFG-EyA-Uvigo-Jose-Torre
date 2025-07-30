package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import uvigo.tfgalmacen.ProductoPedido;

public class ItemDetallesPedidoController {

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

    public void setData(ProductoPedido procutoDelPedido) {
        producto_detalle_pedido_label.setText(procutoDelPedido.getIdentificadorProducto());
        cantidad_detalle_pedido_label.setText(String.valueOf(procutoDelPedido.getCantidad()));

        tipo_producto_color_ldentifier.setFill(Paint.valueOf(procutoDelPedido.colorHEX));
        producto_listo_en_pedido_check.setSelected(procutoDelPedido.isComplete);
    }
}
