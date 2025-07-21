package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.almacenManagement.Palet;

import static uvigo.tfgalmacen.database.UsuarioDAO.getNombreUsuarioById;
import static uvigo.tfgalmacen.database.UsuarioDAO.getRoleNameByUserId;


public class ItemPedidoController {

    // colores para los estados del pedido


    @FXML
    private CheckBox check_select_item_pedido;

    @FXML
    private Label estado_pedido_label;

    @FXML
    private Label id_pedido_label;

    @FXML
    private Label nombre_cliente_label;

    @FXML
    private Label id_usuario_label;

    @FXML
    private Label text_usuario_label;

    @FXML
    private VBox vertical_container;


    public void setData(Pedido pedido){

        // Código para llenar los labels o nodos con la información del pedido
        id_pedido_label.setText(String.valueOf(pedido.getCodigo_referencia()));
        nombre_cliente_label.setText(pedido.getNombre_cliente());
        estado_pedido_label.setText(pedido.getEstado());


        String colorHEX = pedido.getColorEstadoHEX();
        estado_pedido_label.setStyle("-fx-background-color: " +  colorHEX);

        if ("Pendiente".equals(pedido.getEstado())){
            id_usuario_label.setVisible(false);
            id_usuario_label.setManaged(false);

            text_usuario_label.setVisible(false);
            text_usuario_label.setManaged(false);
        }
        else{
            id_usuario_label.setText(getNombreUsuarioById(Main.connection, pedido.getId_usuario()) +
                    " (" + getRoleNameByUserId(Main.connection, pedido.getId_usuario())+ ")");
        }

    }

}
