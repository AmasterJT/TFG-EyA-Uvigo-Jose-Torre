package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.Palet;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static uvigo.tfgalmacen.database.PedidoDAO.getPedidosEnviados;

public class windowGenerarPedidoController implements Initializable {

    @FXML
    private Button ExitButton;

    @FXML
    private AnchorPane Pane;

    @FXML
    private ComboBox<?> combo_pedido_terminado_hora;

    @FXML
    private Button exportar_btn;

    @FXML
    private HBox windowBar;

    List<Pedido> pedidos;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();

        });

        pedidos = getPedidosEnviados(Main.connection);

        System.out.println(pedidos);

    }
}
