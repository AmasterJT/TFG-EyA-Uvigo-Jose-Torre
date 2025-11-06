package uvigo.tfgalmacen.controllers.apartadosAjustesControllers;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uvigo.tfgalmacen.Cliente;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.Producto;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemOrdenCrearPedidoController {

    private static final Logger LOGGER = Logger.getLogger(ItemOrdenCrearPedidoController.class.getName());

    static {
        // Sube el nivel del logger
        LOGGER.setLevel(Level.ALL);

        // Evita que use los handlers del padre (que suelen estar en INFO con SimpleFormatter)
        LOGGER.setUseParentHandlers(false);

        // Crea un ConsoleHandler propio con tu ColorFormatter
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);                 // ¡importante!
        ch.setFormatter(new ColorFormatter());  // tu formatter con colores/emoji
        LOGGER.addHandler(ch);

        // (Opcional) Si quieres también afectar al root logger:
        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            h.setLevel(Level.ALL); // si decides mantenerlos
        }
    }

    private static final String PLACEHOLDER_PRODUCTOS = "Seleccionar productos";

    @FXML
    private HBox background_Hbox;

    @FXML
    private ComboBox<Producto> combo_producto_crear_pedido;


    @FXML
    public void initialize() {
        inicializarComboBoxes();

    }

    private void inicializarComboBoxes() {
        // Texto que se muestra cuando no hay selección
        combo_producto_crear_pedido.setPromptText(PLACEHOLDER_PRODUCTOS);

        // Cargar clientes en el combo
        combo_producto_crear_pedido.getItems().setAll(Almacen.TodosProductos);

        // Opcional: convertir a String legible
        combo_producto_crear_pedido.setConverter(new StringConverter<>() {
            @Override
            public String toString(Producto producto) {
                return (producto != null) ? producto.getIdentificadorProducto() : "";
            }

            @Override
            public Producto fromString(String string) {
                return null; // No necesario
            }
        });
    }
}
