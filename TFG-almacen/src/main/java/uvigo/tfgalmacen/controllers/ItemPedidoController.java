package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Pedido;

import java.net.URL;
import java.util.ResourceBundle;

import static uvigo.tfgalmacen.database.PedidoDAO.getPaletsDelPedido;
import static uvigo.tfgalmacen.database.UsuarioDAO.getNombreUsuarioById;
import static uvigo.tfgalmacen.database.UsuarioDAO.getRoleNameByUserId;
import static uvigo.tfgalmacen.utils.ClipboardUtils.copyLabelText;
import static uvigo.tfgalmacen.utils.TerminalColors.*;


public class ItemPedidoController implements Initializable {

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
    private Label palets_pedido_label;


    @FXML
    private Button copy_codigo_referencia_btn;

    private Pedido pedido;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        copy_codigo_referencia_btn.setOnAction(_ -> copyLabelText(copy_codigo_referencia_btn, id_pedido_label));
    }

    public void setData(Pedido pedido) {
        this.pedido = pedido;

        check_select_item_pedido.setOnMouseClicked(_ -> prueba());

        // Código para llenar los labels o nodos con la información del pedido
        id_pedido_label.setText(String.valueOf(pedido.getCodigo_referencia()));
        nombre_cliente_label.setText(pedido.getNombre_cliente());


        String colorHEX = pedido.getColorEstadoHEX();

        // estado_pedido_label.setText(pedido.getEstado());
        if ("primera_hora".equals(pedido.getHoraSalida())) {
            String colorPrimeraHora = "#ff1fa0";
            estado_pedido_label.setStyle("-fx-background-color: " + colorPrimeraHora);
            estado_pedido_label.setText("1");
        } else if ("segunda_hora".equals(pedido.getHoraSalida())) {
            String colorSegundaHora = "#1e90ff";
            estado_pedido_label.setStyle("-fx-background-color: " + colorSegundaHora);
            estado_pedido_label.setText("2");
        } else {
            estado_pedido_label.setStyle("-fx-background-color: " + colorHEX);
            estado_pedido_label.setText("-");
        }


        if ("Pendiente".equals(pedido.getEstado())) {
            id_usuario_label.setVisible(false);
            id_usuario_label.setManaged(false);

            text_usuario_label.setVisible(false);
            text_usuario_label.setManaged(false);
        } else {
            id_usuario_label.setText(getNombreUsuarioById(Main.connection, pedido.getId_usuario()) +
                    " (" + getRoleNameByUserId(Main.connection, pedido.getId_usuario()) + ")");
        }

        palets_pedido_label.setText(String.valueOf(getPaletsDelPedido(Main.connection, pedido.getId_pedido())));
    }

    private void prueba() {
        for (int i = 0; i < pedidosController.allItemControllers.size(); i++) {
            if (pedidosController.allItemControllers.get(i).getPedido().getId_pedido() == this.pedido.getId_pedido()) {
                pedidosController.allItemControllers.remove(i);
                pedidosController.allItemControllers.add(this);
            }

        }
    }

    public boolean isSelected() {
        return check_select_item_pedido.isSelected();
    }

    public void setSelected(boolean bool) {
        check_select_item_pedido.setSelected(bool);
    }

    public Pedido getPedido() {
        return pedido; // Asegúrate de guardar el Pedido en setData
    }

    @Override
    public String toString() {
        return CYAN + "Pedido #" + RESET + pedido.getCodigo_referencia();
    }


}
