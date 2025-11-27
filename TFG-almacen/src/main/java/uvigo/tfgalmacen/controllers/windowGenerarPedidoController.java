package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Pedido;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static uvigo.tfgalmacen.database.PedidoDAO.*;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.ventana_success;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.ventana_warning;

public class windowGenerarPedidoController implements Initializable {

    @FXML
    private Button ExitButton;

    @FXML
    private ComboBox<Pedido> combo_pedido_terminado_hora;

    @FXML
    private Button exportar_btn;

    List<Pedido> pedidos;
    private apartadoEnvioController parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });

        // Cargar pedidos completados y no enviados
        cargarPedidosEnCombo();

        // Acción del botón: marcar pedido como enviado
        exportar_btn.setOnAction(_ -> marcarPedidoSeleccionadoComoEnviado());
    }

    private void cargarPedidosEnCombo() {
        pedidos = getPedidosCompletadosNoEnviados(Main.connection);

        combo_pedido_terminado_hora.getItems().setAll(pedidos);

        combo_pedido_terminado_hora.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pedido p) {
                if (p == null) return "";
                return p.getCodigo_referencia();
            }

            @Override
            public Pedido fromString(String s) {
                if (s == null) return null;
                return pedidos.stream()
                        .filter(p -> s.equals(p.getCodigo_referencia()))
                        .findFirst()
                        .orElse(null);
            }
        });

        combo_pedido_terminado_hora.getSelectionModel().clearSelection();
    }

    private void marcarPedidoSeleccionadoComoEnviado() {
        Pedido seleccionado = combo_pedido_terminado_hora.getValue();
        if (seleccionado == null) {
            System.out.println("No hay pedido seleccionado.");
            return;
        }

        // 1) Comprobar etiquetas mediante el padre
        if (parent != null) {
            boolean todasEtiquetas = parent.tieneTodasEtiquetasParaPedido(seleccionado.getId_pedido());
            if (!todasEtiquetas) {
                System.out.println("No se puede marcar como ENVIADO: hay palets sin etiqueta para el pedido "
                        + seleccionado.getCodigo_referencia());
                // Aquí podrías usar una ventana_warning si quieres:
                ventana_warning("No se puede enviar",
                        "Faltan etiquetas",
                        "Todos los palets del pedido deben tener etiqueta antes de enviarlo.");
                return;
            }
        } else {
            System.out.println("Advertencia: parent == null, no se ha podido comprobar etiquetas.");
        }

        // 2) Marcar como enviado en BDD
        boolean ok = marcarPedidoComoEnviado(Main.connection, seleccionado.getId_pedido());
        if (!ok) {
            System.out.println("No se pudo marcar como enviado el pedido " + seleccionado.getCodigo_referencia());
            return;
        }

        System.out.println("Pedido marcado como ENVIADO: " + seleccionado.getCodigo_referencia());

        ventana_success("Pedido enviado",
                "El pedido " + seleccionado.getCodigo_referencia() + " ha sido marcado como ENVIADO.",
                "Información");

        // 3) Refrescar combo
        cargarPedidosEnCombo();

        // 4) Pedir al padre refrescar el grid
        if (parent != null) {
            parent.refrescarGridEnvio();
        }

        // 5) Cerrar ventana
        Stage stage = (Stage) exportar_btn.getScene().getWindow();
        stage.close();
    }

    // <<< NUEVO SETTER >>>
    public void setEnvioParent(apartadoEnvioController parent) {
        this.parent = parent;
    }
}
