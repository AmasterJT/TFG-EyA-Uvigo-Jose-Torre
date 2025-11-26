package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.database.DetallesPedidoDAO;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.database.DetallesPedidoDAO.getIdPedidoByIdDetalle;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.*;

public class modificarCantidadPaletizarController implements Initializable {

    // 🧩 Logger con colores personalizados
    private static final Logger LOGGER = Logger.getLogger(modificarCantidadPaletizarController.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new ColorFormatter());
        LOGGER.addHandler(consoleHandler);

        // Opcional: ajustar también el root logger (evita silencios en handlers)
        for (Handler h : Logger.getLogger("").getHandlers()) {
            h.setLevel(Level.ALL);
        }
    }

    int CANTIDAD_MAXIMA = 0;
    int id_detalle_opriginal;
    int id_pedido;


    @FXML
    private Button ExitButton;

    @FXML
    private Button modificar_btn;

    @FXML
    private TextField nueva_cantidad_text;

    @FXML
    private Label cantidad_restante_label;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            LOGGER.info("Ventana de detalles de pedido cerrada.");
            stage.close();
        });

        nueva_cantidad_text.setTextFormatter(numericFormatter());

        nueva_cantidad_text.textProperty().addListener((_, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                cantidad_restante_label.setText(String.valueOf(CANTIDAD_MAXIMA));
                return;
            }

            try {
                int valor = Integer.parseInt(newValue);

                // Si se pasa del máximo...
                if (valor >= CANTIDAD_MAXIMA) {
                    // revertimos a oldValue
                    nueva_cantidad_text.setText(oldValue);

                    // shake visual
                    shake(nueva_cantidad_text, SHAKE_DURATION);

                    return;
                }

                // Actualiza la resta
                int restante = CANTIDAD_MAXIMA - valor;
                cantidad_restante_label.setText(String.valueOf(restante));

            } catch (NumberFormatException e) {
                // Si el formatter falló por algo, revertir
                nueva_cantidad_text.setText(oldValue);
            }
        });

        modificar_btn.setOnAction(_ -> splitPedido());
    }


    public void setData(String cantidad, int id_detalle_original) {

        CANTIDAD_MAXIMA = Integer.parseInt(cantidad);
        cantidad_restante_label.setText(cantidad);

        this.id_detalle_opriginal = id_detalle_original;
        this.id_pedido = getIdPedidoByIdDetalle(Main.connection, id_detalle_original);
    }

    private void splitPedido() {
        String txt = nueva_cantidad_text.getText();
        if (txt == null || txt.isBlank()) {
            shake(nueva_cantidad_text, SHAKE_DURATION);
            ventana_error("Cantidad inválida",
                    "Debes introducir una cantidad",
                    "La cantidad no puede estar vacía.");
            return;
        }

        int cantidadNueva;
        try {
            cantidadNueva = Integer.parseInt(txt.trim());
        } catch (NumberFormatException e) {
            shake(nueva_cantidad_text, SHAKE_DURATION);
            ventana_error("Cantidad inválida",
                    "Formato numérico incorrecto",
                    "Introduce un número entero válido.");
            return;
        }

        if (cantidadNueva <= 0 || cantidadNueva >= CANTIDAD_MAXIMA) {
            shake(nueva_cantidad_text, SHAKE_DURATION);
            ventana_error("Cantidad inválida",
                    "La cantidad debe ser mayor que 0 y menor que " + CANTIDAD_MAXIMA,
                    "No puedes usar toda la cantidad original ni un valor negativo.");
            return;
        }

        try {
            int nuevoIdDetalle = DetallesPedidoDAO.splitDetalle(
                    Main.connection,
                    id_detalle_opriginal,
                    cantidadNueva
            );

            if (nuevoIdDetalle <= 0) {
                ventana_error("Error al dividir",
                        "No se pudo crear el nuevo detalle",
                        "Revisa los logs para más información.");
                return;
            }

            ventana_warning("Operación completada",
                    "Detalle dividido correctamente",
                    "Se ha actualizado el detalle original y creado uno nuevo con la cantidad restante.");

            Stage stage = (Stage) modificar_btn.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al dividir el detalle del pedido", e);
            ventana_error("Error inesperado",
                    "No se pudo dividir el detalle",
                    e.getMessage());
        }
    }


}
