package uvigo.tfgalmacen.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uvigo.tfgalmacen.Cliente;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.database.ClientesDAO;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.ResourceBundle;

import static uvigo.tfgalmacen.database.ClientesDAO.getClienteById;
import static uvigo.tfgalmacen.database.PedidoDAO.*;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.*;

public class windowGenerarPedidoController implements Initializable {

    @FXML
    private Button ExitButton;

    @FXML
    private ComboBox<Pedido> combo_pedido_terminado_hora;

    @FXML
    private Button exportar_btn;

    List<Pedido> pedidos;
    private apartadoEnvioController parent;

    @FXML
    private Label cliente_label;

    @FXML
    private Label direccion_label;

    @FXML
    private Label email_label;

    @FXML
    private Label telefono_label;

    @FXML
    private Button google_maps_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });

        // Cargar pedidos completados y no enviados
        cargarPedidosEnCombo();

        EventHandler<KeyEvent> onEnterPressed = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                marcarPedidoSeleccionadoComoEnviado();
            }
        };

        combo_pedido_terminado_hora.setOnKeyPressed(
                onEnterPressed
        );

        combo_pedido_terminado_hora.setOnAction(_ -> {
            ;
            actualizarDatosCliente(combo_pedido_terminado_hora.getValue());
        });

        // Acción del botón: marcar pedido como enviado
        exportar_btn.setOnAction(_ -> {

            boolean confirm = ventana_confirmacion(
                    "Confirmar envío",
                    "¿Marcar el pedido seleccionado como ENVIADO? \n" +
                            "Esta acción no se puede deshacer."
            );

            if (confirm) marcarPedidoSeleccionadoComoEnviado();
        });


        google_maps_btn.setOnAction(_ -> {
            Cliente seleccionado = ClientesDAO.getClienteById(Main.connection, combo_pedido_terminado_hora.getValue().getId_cliente());
            if (seleccionado != null) {
                abrirCoordenadasEnGoogleMaps(
                        seleccionado.getLatitud(),
                        seleccionado.getLongitud()
                );
            }
        });
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

            String userHome = System.getProperty("user.home");
            File downloads = new File(userHome, "Downloads/Etiquetas");
            parent.getCarpeta_destino_text().setText(downloads.getAbsolutePath());

            parent.getAbrir_explorador_btn().setOnAction(_ -> parent.abrirExploradorEnCarpeta(downloads));
            parent.getAbrir_explorador_btn().setDisable(false);
            parent.limpiarPdf();
        }

        // 5) Cerrar ventana
        Stage stage = (Stage) exportar_btn.getScene().getWindow();
        stage.close();
    }


    private void actualizarDatosCliente(Pedido pedido) {
        if (pedido == null) {
            cliente_label.setText("-");
            email_label.setText("-");
            telefono_label.setText("-");
            return;
        }


        Cliente cliente = getClienteById(Main.connection, pedido.getId_cliente());

        System.out.println(cliente);

        if (cliente == null) {
            cliente_label.setText("-");
            email_label.setText("-");
            telefono_label.setText("-");
            return;
        }

        cliente_label.setText(
                cliente.getNombre() != null ? cliente.getNombre() : "-"
        );
        email_label.setText(
                cliente.getEmail() != null ? cliente.getEmail() : "-"
        );
        telefono_label.setText(
                cliente.getTelefono() != null ? cliente.getTelefono() : "-"
        );
        direccion_label.setText(cliente.getDireccion() != null ? cliente.getDireccion() : "-");


    }


    // <<< NUEVO SETTER >>>
    public void setEnvioParent(apartadoEnvioController parent) {
        this.parent = parent;
    }


    public void abrirCoordenadasEnGoogleMaps(double lat, double lng) {

        String latitud = String.valueOf(lat).replace(",", ".");
        String longitud = String.valueOf(lng).replace(",", ".");

        String url = String.format(
                "https://www.google.com/maps/search/?api=1&query=%s,%s",
                latitud, longitud
        );
        Main.getAppHostServices().showDocument(url);
    }

}
