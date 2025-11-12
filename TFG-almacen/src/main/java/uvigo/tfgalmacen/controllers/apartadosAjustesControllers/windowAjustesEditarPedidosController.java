package uvigo.tfgalmacen.controllers.apartadosAjustesControllers;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.Main;

import uvigo.tfgalmacen.database.DetallesPedidoDAO.*;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.database.RolePermissionDAO;
import uvigo.tfgalmacen.database.UsuarioDAO;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Integer.parseInt;
import static uvigo.tfgalmacen.RutasFicheros.*;
import static uvigo.tfgalmacen.database.DetallesPedidoDAO.getIdsDetallePorPedido;
import static uvigo.tfgalmacen.database.DetallesPedidoDAO.insertarDetallePedido;
import static uvigo.tfgalmacen.database.PedidoDAO.*;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.*;

public class windowAjustesEditarPedidosController {

    private static final Logger LOGGER = Logger.getLogger(windowAjustesEditarPedidosController.class.getName());

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


    private static final String PLACEHOLDER_EDITAR_PEDIDO = "Seleccionar pedido";


    @FXML
    private Button ExitButton;

    @FXML
    private Label cliente_label;

    @FXML
    private ComboBox<Pedido> combo_pedidos_existentes;

    @FXML
    private Button crear_pedidio_btn;

    @FXML
    private Button agregar_producto_btn;


    @FXML
    private ListView<Parent> list_productos_del_pedido;

    @FXML
    private HBox windowBar;

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

        // Bloquear combo_pedidos_existentes
        //y fecha_entrega_pickerdate si la lista NO está vacía
       /* combo_pedidos_existentes.disableProperty().bind(
                Bindings.isNotEmpty(list_productos_del_pedido.getItems())
        );
*/

        // Listener: al cambiar usuario seleccionado, rellenar campos
        combo_pedidos_existentes.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            if (nv != null && !nv.equals(PLACEHOLDER_EDITAR_PEDIDO)) setData(nv);

        });

        agregar_producto_btn.setOnMouseClicked(_ -> agregarItemFXML2());

    }

    private void setData(Pedido nv) {

        cliente_label.setText(nv.getNombre_cliente());

        int id = nv.getId_pedido();
        for (int i = 0; i < getIdsDetallePorPedido(Main.connection, id).size(); i++) {
            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            agregarItemFXML();
        }
    }

    private void configurarListViewConMenuContextual() {
        list_productos_del_pedido.setCellFactory(lv -> {
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
        list_productos_del_pedido.setOnKeyPressed(evt -> {
            if (evt.getCode() == javafx.scene.input.KeyCode.DELETE) {
                eliminarSeleccionado();
            }
        });
    }


    private void eliminarSeleccionado() {
        int idx = list_productos_del_pedido.getSelectionModel().getSelectedIndex();
        if (idx >= 0) {
            list_productos_del_pedido.getItems().remove(idx);
            LOGGER.fine("Ítem eliminado con tecla Supr en índice " + idx);
        }
    }


    private void inicializarComboBoxes() {
        // Texto que se muestra cuando no hay selección
        combo_pedidos_existentes.setPromptText(PLACEHOLDER_EDITAR_PEDIDO);

        // Cargar clientes en el combo
        combo_pedidos_existentes.getItems().setAll(getTodosLosPedidos(Main.connection));

        // Opcional: convertir a String legible
        combo_pedidos_existentes.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pedido pedido) {
                return (pedido != null) ? pedido.getCodigo_referencia() : "";
            }

            @Override
            public Pedido fromString(String string) {
                return null; // No necesario
            }
        });
    }

    public boolean validar_datos_en_blanco(ComboBox<Pedido> combo_pedidos_existentes) {
        boolean datos_en_blanco = true;


        if (combo_pedidos_existentes.getValue() == null || combo_pedidos_existentes.getValue().getCodigo_referencia().isBlank()) {
            shake(combo_pedidos_existentes, SHAKE_DURATION);
            datos_en_blanco = false;
        }


        return datos_en_blanco;

    }

    private void agregarItemFXML() {

        if (!validar_datos_en_blanco(combo_pedidos_existentes)) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(WINDOW_AJUSTES_ITEM_EDITAR_PEDIDOS_FXML));
            Parent itemRoot = loader.load();

            ItemEditarPedidoController controller = loader.getController();
            itemRoot.setUserData(controller);

            controller.getCantidad_producto().setStyle("-fx-background-color: " + Main.colors.get("-amaster-light-gray") + "; -fx-text-fill: black;");

            // ItemOrdenCrearPedidoController itemController = loader.getController(); // si necesitas setear datos
            list_productos_del_pedido.getItems().add(itemRoot);
            list_productos_del_pedido.scrollTo(list_productos_del_pedido.getItems().size() - 1);

        } catch (IOException ex) {
            // Loguea el problema de carga del FXML del item
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "No se pudo cargar el item", ex);
            ex.printStackTrace();
        }

    }


    private void agregarItemFXML2() {

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
