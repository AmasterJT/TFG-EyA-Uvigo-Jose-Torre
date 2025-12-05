package uvigo.tfgalmacen.controllers.apartadosAjustesControllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uvigo.tfgalmacen.models.Cliente;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.utils.ColorFormatter;

import javafx.beans.binding.Bindings;


import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Integer.parseInt;
import static uvigo.tfgalmacen.RutasFicheros.WINDOW_AJUSTES_ITEM_CREAR_PEDIDOS_FXML;
import static uvigo.tfgalmacen.database.DetallesPedidoDAO.insertarDetallePedido;
import static uvigo.tfgalmacen.database.PedidoDAO.crearPedidoYObtenerCodigo;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.*;

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
    public void initialize() {
        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });

        inicializarComboBoxes();
        // ... tus checks de inyección y resto de código ...

        configurarListViewConMenuContextual();

        // Bloquear combo_clientes y fecha_entrega_pickerdate si la lista NO está vacía
        combo_clientes.disableProperty().bind(
                Bindings.isNotEmpty(list_productos_agregados_crear_pedido.getItems())
        );
        fecha_entrega_pickerdate.disableProperty().bind(
                Bindings.isNotEmpty(list_productos_agregados_crear_pedido.getItems())
        );


        agregar_producto_btn.setOnMouseClicked(_ -> agregarItemFXML());
        crear_pedidio_btn.setOnMouseClicked(_ -> {
            try {
                onCrearPedidoClick();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void configurarListViewConMenuContextual() {
        list_productos_agregados_crear_pedido.setCellFactory(lv -> {
            final ContextMenu menu = crearContextMenuEliminar(lv);

            final ListCell<Parent> cell = new ListCell<>() {
                @Override
                protected void updateItem(Parent item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        setContextMenu(null); // limpiar celdas vacías
                    } else {
                        setText(null);
                        setGraphic(item);
                        setContextMenu(menu); // asignar menú a celdas con contenido
                    }
                }
            };

            // Capturar apertura de menú de contexto antes que los hijos del graphic
            cell.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, evt -> {
                if (!cell.isEmpty()) {
                    lv.getSelectionModel().select(cell.getIndex());
                    menu.show(cell, evt.getScreenX(), evt.getScreenY());
                }
                evt.consume();
            });

            // Fallback: botón secundario
            cell.setOnMouseClicked(evt -> {
                if (evt.getButton() == MouseButton.SECONDARY && !cell.isEmpty()) {
                    lv.getSelectionModel().select(cell.getIndex());
                    menu.show(cell, evt.getScreenX(), evt.getScreenY());
                    evt.consume();
                }
            });

            return cell;
        });

        // Borrar con tecla Supr
        list_productos_agregados_crear_pedido.setOnKeyPressed(evt -> {
            if (evt.getCode() == javafx.scene.input.KeyCode.DELETE) {
                eliminarSeleccionado();
            }
        });
    }


    private void eliminarSeleccionado() {
        int idx = list_productos_agregados_crear_pedido.getSelectionModel().getSelectedIndex();
        if (idx >= 0) {
            list_productos_agregados_crear_pedido.getItems().remove(idx);
            LOGGER.fine("Ítem eliminado con tecla Supr en índice " + idx);
        }
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

    public boolean validar_datos_en_blanco(ComboBox<Cliente> combo_clientes, DatePicker fecha_envio) {
        boolean datos_en_blanco = true;


        if (combo_clientes.getValue() == null || combo_clientes.getValue().getNombre().isBlank()) {
            shake(combo_clientes, SHAKE_DURATION);
            datos_en_blanco = false;
        }

        if (fecha_entrega_pickerdate.getValue() == null) {
            shake(fecha_entrega_pickerdate, SHAKE_DURATION);
            datos_en_blanco = false;
        }

        return datos_en_blanco;

    }

    private void agregarItemFXML() {

        if (!validar_datos_en_blanco(combo_clientes, fecha_entrega_pickerdate)) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(WINDOW_AJUSTES_ITEM_CREAR_PEDIDOS_FXML));
            Parent itemRoot = loader.load();

            ItemOrdenCrearPedidoController controller = loader.getController();
            itemRoot.setUserData(controller);

            // ItemOrdenCrearPedidoController itemController = loader.getController(); // si necesitas setear datos
            list_productos_agregados_crear_pedido.getItems().add(itemRoot);
            list_productos_agregados_crear_pedido.scrollTo(list_productos_agregados_crear_pedido.getItems().size() - 1);

        } catch (IOException ex) {
            // Loguea el problema de carga del FXML del item
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "No se pudo cargar el item", ex);
            ex.printStackTrace();
        }

    }


    @FXML
    private void onCrearPedidoClick() throws SQLException {
        System.out.println("=== Creando pedido ===");

        String nombre_cliente = combo_clientes.getSelectionModel().getSelectedItem().getNombre();
        int id_cliente = combo_clientes.getSelectionModel().getSelectedItem().getId_cliente();

        String codigo = "";
        int idPedido = 0;

        Map<String, Integer> REF_CODE = crearPedidoYObtenerCodigo(Main.connection, id_cliente, Date.valueOf(fecha_entrega_pickerdate.getValue()));

        codigo = REF_CODE.keySet().iterator().next();
        idPedido = REF_CODE.get(codigo);

        System.out.println("✅ Pedido creado:");
        System.out.printf("CODIGO REFERENCIA: %s (%d) %n", codigo, idPedido);

        System.out.printf("Cliente: %s (%d)| fecha de envio: %s %n",
                nombre_cliente,
                id_cliente,
                fecha_entrega_pickerdate.getValue().toString());


        for (Parent itemRoot : list_productos_agregados_crear_pedido.getItems()) {
            // Cada item es un nodo cargado desde el FXML del ítem
            if (itemRoot == null) continue;

            // Obtener el controlador asociado
            ItemOrdenCrearPedidoController controller = (ItemOrdenCrearPedidoController) itemRoot.getUserData();

            // Si no lo guardaste como userData al crear el ítem, puedes hacerlo ahora:
            if (controller == null) {
                // Intenta obtenerlo de nuevo (opcional)
                continue;
            }

            // Obtener el producto y cantidad
            var combo = controller.getCombo_producto_crear_pedido();
            var textField = controller.getCantidad_product();

            int id_producto = combo.getValue().getIndex_BDD();

            String cantidadTexto = (textField.getText() != null && !textField.getText().isBlank())
                    ? textField.getText()
                    : "(sin cantidad)";


            insertarDetallePedido(Main.connection, idPedido, id_producto, parseInt(cantidadTexto));


            String green_color_back = "-fx-background-color: #056705;";
            AnchorPane background = controller.getBackground();
            background.setStyle(green_color_back);

        }


        System.out.println("======================");
    }


    private ContextMenu crearContextMenuEliminar(ListView<Parent> lv) {
        final ContextMenu menu = new ContextMenu();
        final MenuItem eliminar = new MenuItem("Eliminar");
        final MenuItem borrarTodo = new MenuItem("Borrar todo");

        // --- Acción eliminar elemento seleccionado ---
        eliminar.setOnAction(_ -> {
            int index = lv.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                lv.getItems().remove(index);
                lv.getSelectionModel().clearSelection();
                LOGGER.fine("Ítem eliminado del ListView en índice " + index);
            }
            menu.hide();
        });

        // --- Acción borrar todo (con confirmación) ---
        borrarTodo.setOnAction(_ -> {
            if (lv.getItems().isEmpty()) {
                return;
            }

            Optional<ButtonType> resultado = ventana_error("Confirmar borrado", "¿Seguro que deseas borrar todos los ítems?", "Esta acción eliminará permanentemente todos los elementos de la lista.", "Si, borrar todo");

            if (resultado.isPresent() && resultado.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                lv.getItems().clear();
                lv.getSelectionModel().clearSelection();
                LOGGER.info("Se han borrado todos los ítems del ListView.");
            }

            menu.hide();
        });

        // --- Deshabilitar cuando no procede ---
        eliminar.disableProperty().bind(lv.getSelectionModel().selectedIndexProperty().lessThan(0));
        menu.getItems().addAll(eliminar, borrarTodo);
        return menu;
    }

}
