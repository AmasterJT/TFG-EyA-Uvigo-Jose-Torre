package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import uvigo.tfgalmacen.ProductoPedido;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

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


    @FXML
    private Button ExitButton;

    @FXML
    private AnchorPane Pane;

    @FXML
    private Button modificar_btn;

    @FXML
    private TextField nueva_cantidad_text;

    @FXML
    private Label cantidad_restante_label;

    @FXML
    private HBox windowBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ExitButton.setOnMouseClicked(event -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            LOGGER.info("Ventana de detalles de pedido cerrada.");
            stage.close();
        });

        nueva_cantidad_text.setTextFormatter(numericFormatter());

        nueva_cantidad_text.textProperty().addListener((obs, oldValue, newValue) -> {
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

    }

    public void setData(String cantidad, int id_original) {

        CANTIDAD_MAXIMA = Integer.parseInt(cantidad);
        cantidad_restante_label.setText(cantidad);

        this.id_detalle_opriginal = id_original;
    }

}
