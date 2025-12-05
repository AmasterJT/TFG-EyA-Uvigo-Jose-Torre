package uvigo.tfgalmacen.controllers.apartadosAjustesControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uvigo.tfgalmacen.models.Pedido;
import uvigo.tfgalmacen.Main;

import uvigo.tfgalmacen.almacenManagement.Producto;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.io.IOException;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Integer.parseInt;
import static uvigo.tfgalmacen.RutasFicheros.*;
import static uvigo.tfgalmacen.database.DetallesPedidoDAO.*;
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
            LOGGER.info("Pedido seleccionado para editar: " + nv.getCodigo_referencia() + " (id: " + nv.getId_pedido() + ")");
            if (!nv.equals(PLACEHOLDER_EDITAR_PEDIDO)) setData(nv);

        });

        agregar_producto_btn.setOnMouseClicked(_ -> agregarItemFXML2());
        crear_pedidio_btn.setOnMouseClicked(_ -> {
            if (editarPedido()) {
                // Ventana de aviso (usa tu util existente)
                ventana_warning("Operación completada", "Pedido actualizado", "Los cambios se han guardado correctamente.");

                // Cerrar la ventana actual
                Stage stage = (Stage) crear_pedidio_btn.getScene().getWindow();
                stage.close();
            } else {
                // Si quieres, muestra también un aviso de error
                ventana_error("Error", "No se pudo guardar el pedido", "Revisa los datos y vuelve a intentarlo.");
            }

            // Cerrar la ventana actual
            Stage stage = (Stage) crear_pedidio_btn.getScene().getWindow();
            stage.close();
        });

    }


    private boolean editarPedido() {
        var pedidoSel = combo_pedidos_existentes.getValue();
        if (pedidoSel == null) {
            LOGGER.warning("No hay pedido seleccionado.");
            return false;
        }
        int idPedido = pedidoSel.getId_pedido();
        LOGGER.info(() -> "Inicio edición de pedido " + pedidoSel.getCodigo_referencia() + " (id=" + idPedido + ")");

        try {
            Main.connection.setAutoCommit(false);

            Map<Integer, Integer> totalesEditados = agruparEditadosPorProducto();
            Map<Integer, Integer> totalesNuevos = agruparNuevosPorProducto();
            Map<Integer, Integer> totales = mergeCantidades(totalesEditados, totalesNuevos);

            replaceDetallesWithTotals(Main.connection, idPedido, totales);

            Main.connection.commit();
            LOGGER.info("Cambios guardados correctamente.");
            return true;

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error guardando cambios. Se realiza rollback.", ex);
            try {
                Main.connection.rollback();
            } catch (Exception ignored) {
            }
            return false;

        } finally {
            try {
                Main.connection.setAutoCommit(true);
            } catch (Exception ignored) {
            }
        }
    }


    /* ====================== Agrupaciones ====================== */

    /**
     * Suma cantidades por id_producto a partir de los ítems EXISTENTES en edición (respetando cambios de producto).
     */
    private Map<Integer, Integer> agruparEditadosPorProducto() {
        Map<Integer, Integer> res = new HashMap<>();
        for (ItemEditarPedidoController it : controllersDeDetallesExistentes()) {
            var combo = it.getCombo_producto_editar_pedido();
            var tf = it.getCantidad_producto();
            if (combo == null || tf == null) continue;

            var prod = combo.getValue();
            String cantStr = tf.getText();
            if (prod == null || cantStr == null || cantStr.isBlank()) continue;

            Integer cantidad = parseCantidadPositiva(cantStr);
            if (cantidad == null) {
                LOGGER.warning(() -> "Cantidad inválida en detalle " + it.getId_detalle_pedido() + ": " + cantStr);
                continue;
            }

            // Independientemente de isEditMode, tomamos el valor final visible
            int idProducto = prod.getIndex_BDD();
            res.merge(idProducto, cantidad, Integer::sum);
        }
        return res;
    }

    /**
     * Suma cantidades por id_producto a partir de los ítems NUEVOS.
     */
    private Map<Integer, Integer> agruparNuevosPorProducto() {
        Map<Integer, Integer> res = new HashMap<>();
        for (ItemOrdenCrearPedidoController it : controllersDeAltasNuevas()) {
            var combo = it.getCombo_producto_crear_pedido();
            var tf = it.getCantidad_product();
            if (combo == null || tf == null) continue;

            var prod = combo.getValue();
            String cantStr = tf.getText();
            if (prod == null || cantStr == null || cantStr.isBlank()) continue;

            Integer cantidad = parseCantidadPositiva(cantStr);
            if (cantidad == null) {
                LOGGER.warning(() -> "Cantidad inválida en alta nueva: " + cantStr);
                continue;
            }

            int idProducto = prod.getIndex_BDD();
            res.merge(idProducto, cantidad, Integer::sum);
        }
        return res;
    }

    /**
     * Une dos mapas de cantidades por id_producto.
     */
    private Map<Integer, Integer> mergeCantidades(Map<Integer, Integer> a, Map<Integer, Integer> b) {
        Map<Integer, Integer> out = new HashMap<>(a);
        for (var e : b.entrySet()) {
            out.merge(e.getKey(), e.getValue(), Integer::sum);
        }
        return out;
    }

    /* ====================== Helpers existentes ====================== */

    private Integer parseCantidadPositiva(String txt) {
        try {
            int n = Integer.parseInt(txt.trim());
            return n > 0 ? n : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private List<ItemEditarPedidoController> controllersDeDetallesExistentes() {
        return list_productos_del_pedido.getItems().stream()
                .map(n -> (ItemEditarPedidoController) n.getUserData())
                .filter(Objects::nonNull)
                .toList();
    }

    private List<ItemOrdenCrearPedidoController> controllersDeAltasNuevas() {
        return list_productos_agregados_crear_pedido.getItems().stream()
                .map(n -> (ItemOrdenCrearPedidoController) n.getUserData())
                .filter(Objects::nonNull)
                .toList();
    }

    private void setData(Pedido nv) {

        cliente_label.setText(nv.getNombre_cliente());
        list_productos_del_pedido.getItems().clear();

        int id = nv.getId_pedido();
        for (int i : getIdsDetallePorPedido(Main.connection, id)) {
            agregarItemFXML(i);
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


        list_productos_agregados_crear_pedido.setOnKeyPressed(evt -> {
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

    private void agregarItemFXML(int id_detalle_producto) {

        String[] info = getIdentificadorYCantidadPorDetalle(Main.connection, id_detalle_producto);

        String identificador_producto = info[0];
        String cantidad = info[1];

        if (!validar_datos_en_blanco(combo_pedidos_existentes)) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(WINDOW_AJUSTES_ITEM_EDITAR_PEDIDOS_FXML));
            Parent itemRoot = loader.load();

            ItemEditarPedidoController controller = loader.getController();
            itemRoot.setUserData(controller);

            controller.setId_detalle_pedido(id_detalle_producto);
            controller.getCantidad_producto().setText(cantidad);
            ComboBox<Producto> combo = controller.getCombo_producto_editar_pedido();

            // Buscar el producto cuyo identificador coincida
            for (Producto p : combo.getItems()) {
                if (p.getIdentificadorProducto().equals(identificador_producto)) {
                    combo.getSelectionModel().select(p);
                    break;
                }
            }

            LOGGER.fine("Informacion cargada correctamente en el item (id_detalle=" + id_detalle_producto + ")");

            // callback que borra ESTE nodo concreto de la ListView
            controller.setOnRemove(() -> list_productos_del_pedido.getItems().remove(itemRoot));

            // (opcional) guardar referencia si te hace falta
            itemRoot.setUserData(controller);


            controller.getCantidad_producto().setStyle("-fx-background-color: " + Main.colors.get("-amaster-light-gray") + "; -fx-text-fill: black;");

            // ItemOrdenCrearPedidoController itemController = loader.getController(); // si necesitas setear datos
            list_productos_del_pedido.getItems().add(itemRoot);
            //list_productos_del_pedido.scrollTo(list_productos_del_pedido.getItems().size() - 1); // para hacer scroll hasta el final

        } catch (IOException ex) {
            // Loguea el problema de carga del FXML del item
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                    "No se pudo cargar el item: \n" + ex.getMessage(),
                    ex);
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
            if (index < 0) {
                menu.hide();
                return;
            }

            Optional<ButtonType> resultado = ventana_error(
                    "Confirmar borrado",
                    "¿Seguro que deseas borrar el item?",
                    "Esta acción eliminará el producto seleccionado del pedido.",
                    "Sí, borrar", "Cancelar"
            );

            if (resultado.isPresent() && resultado.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                Parent itemRoot = lv.getItems().get(index);
                Object ud = (itemRoot != null) ? itemRoot.getUserData() : null;

                boolean esListaExistentes = (lv == list_productos_del_pedido);

                if (esListaExistentes && ud instanceof ItemEditarPedidoController ctrl) {
                    int idDetalle = ctrl.getId_detalle_pedido();

                    boolean ok = borrarDetallePorId(Main.connection, idDetalle);
                    if (!ok) {
                        LOGGER.warning("No se pudo borrar en BD el detalle id=" + idDetalle + ". No se elimina de la UI.");
                        menu.hide();
                        return;
                    }
                    LOGGER.info("Detalle eliminado en BD (id_detalle=" + idDetalle + ")");
                }

                // Siempre quitar de la UI
                lv.getItems().remove(index);
                lv.getSelectionModel().clearSelection();
                LOGGER.fine("Ítem eliminado del ListView en índice " + index);
            }


            menu.hide();
        });

        // --- Acción borrar todo (con confirmación) ---
        borrarTodo.setOnAction(_ -> {
            if (lv.getItems().isEmpty()) {
                menu.hide();
                return;
            }

            Optional<ButtonType> resultado = ventana_error(
                    "Confirmar borrado",
                    "¿Seguro que deseas borrar todos los ítems?",
                    "Esta acción eliminará permanentemente todos los elementos del pedido",
                    "Sí, borrar todo", "Cancelar"
            );

            if (resultado.isPresent() && resultado.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                boolean esListaExistentes = (lv == list_productos_del_pedido);

                if (esListaExistentes) {
                    // Recolectar ids y borrar en BD por lote
                    List<Integer> ids = lv.getItems().stream()
                            .map(n -> n != null ? n.getUserData() : null)
                            .filter(ItemEditarPedidoController.class::isInstance)
                            .map(ItemEditarPedidoController.class::cast)
                            .map(ItemEditarPedidoController::getId_detalle_pedido)
                            .toList();

                    if (!ids.isEmpty()) {
                        int borrados = borrarDetallesPorIds(Main.connection, ids);
                        LOGGER.info("Detalles eliminados en BD (list_productos_del_pedido): " + borrados + " de " + ids.size());
                    } else {
                        LOGGER.fine("No había ids_detalle para borrar en BD.");
                    }
                }

                // Limpiar UI en cualquier caso
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
