package uvigo.tfgalmacen.controllers.apartadosAjustesControllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uvigo.tfgalmacen.Cliente;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.RutasFicheros.WINDOW_AJUSTES_ITEM_CREAR_PEDIDOS_FXML;

public class windowAjustesCrearPedidosController {

    private static final Logger LOGGER = Logger.getLogger(windowAjustesCrearPedidosController.class.getName());

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


    private static final String PLACEHOLDER_CLIENTES = "Seleccionar cliente";


    @FXML
    private Button ExitButton;

    @FXML
    private Button agregar_producto_btn;

    @FXML
    private ComboBox<Cliente> combo_clientes;

    @FXML
    private Button crear_pedidio_btn;

    @FXML
    private DatePicker fecha_entrega_pickerdate;

    @FXML
    private ListView<Parent> list_productos_agregados_crear_pedido;

    @FXML
    private HBox windowBar;


    @FXML
    public void initialize() {
        inicializarComboBoxes();
        // Aserción de inyección correcta
        if (list_productos_agregados_crear_pedido == null) {
            throw new IllegalStateException("list_productos_agregados_crear_pedido no inyectado. Revisa fx:id='list_productos_agregados_crear_pedido' y el fx:controller.");
        }
        if (agregar_producto_btn == null) {
            throw new IllegalStateException("agregar_producto_btn no inyectado. Revisa fx:id='agregar_producto_btn'.");
        }

        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });

        // ListView de nodos gráficos
        list_productos_agregados_crear_pedido.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Parent item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setGraphic(empty ? null : item);
            }
        });

        agregar_producto_btn.setOnAction(e -> agregarItemFXML());
    }

    private void inicializarComboBoxes() {
        // Texto que se muestra cuando no hay selección
        combo_clientes.setPromptText(PLACEHOLDER_CLIENTES);

        // Cargar clientes en el combo
        combo_clientes.getItems().setAll(Almacen.TodosClientes);

        // Opcional: convertir a String legible
        combo_clientes.setConverter(new StringConverter<>() {
            @Override
            public String toString(Cliente cliente) {
                return (cliente != null) ? cliente.getNombre() : "";
            }

            @Override
            public Cliente fromString(String string) {
                return null; // No necesario
            }
        });
    }


    private void agregarItemFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(WINDOW_AJUSTES_ITEM_CREAR_PEDIDOS_FXML));
            Parent itemRoot = loader.load();
            // ItemOrdenCrearPedidoController itemController = loader.getController(); // si necesitas setear datos
            list_productos_agregados_crear_pedido.getItems().add(itemRoot);
        } catch (IOException ex) {
            // Loguea el problema de carga del FXML del item
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "No se pudo cargar el item", ex);
        }
    }
}
