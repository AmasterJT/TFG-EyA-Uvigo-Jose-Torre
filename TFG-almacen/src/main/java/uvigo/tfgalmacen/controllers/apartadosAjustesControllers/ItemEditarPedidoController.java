package uvigo.tfgalmacen.controllers.apartadosAjustesControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class ItemEditarPedidoController {

    @FXML
    private AnchorPane background;

    @FXML
    private TextField cantidad_producto;

    @FXML
    private ComboBox<?> combo_producto_editar_pedido;

    @FXML
    private Button editar_producto_btn;

    public TextField getCantidad_producto() {
        return cantidad_producto;
    }

    public void setCantidad_producto(TextField cantidad_producto) {
        this.cantidad_producto = cantidad_producto;
    }
}
