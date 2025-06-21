package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.almacenManagement.Palet;

public class ItemPedidoController {

    // colores para los estados del pedido
    private String pendiente = "#bfbfbf";
    private String en_curso = "#bfbfbf";
    private String cancelado = "#bfbfbf";
    private String completado = "#bfbfbf";
    -indicador-pedido-en-curso-color:  #4bb030;
    -indicador-pedido-cancelado-color: #9e3a2c;

    @FXML
    private CheckBox check_select_item_pedido;

    @FXML
    private Label estado_pedido_label;

    @FXML
    private Label id_pedido_label;

    @FXML
    private Label nombre_cliente_label;


    public void setData(Pedido pedido){

        // Código para llenar los labels o nodos con la información del pedido
        id_pedido_label.setText(String.valueOf(pedido.getCodigo_referencia()));
        nombre_cliente_label.setText(pedido.getNombre_cliente());
        estado_pedido_label.setText(pedido.getEstado());

        // Aquí podrías agregar más lógica si necesitas mostrar más información del pedido

    }

}
