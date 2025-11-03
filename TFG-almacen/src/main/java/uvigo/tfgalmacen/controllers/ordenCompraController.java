package uvigo.tfgalmacen.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.OrdenCompra;
import uvigo.tfgalmacen.Proveedor;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.Palet;
import uvigo.tfgalmacen.almacenManagement.Producto;
import uvigo.tfgalmacen.database.ProveedorProductoDAO;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static uvigo.tfgalmacen.Proveedor.getProveedorPorNombre;
import static uvigo.tfgalmacen.RutasFxml.ITEM_ORDEN_COMPRA_FXML;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.*;

public class ordenCompraController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(ordenCompraController.class.getName());

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


    private boolean TODO_PALETS_OK = true;

    // ----------------------------
    // Constantes / Placeholders
    // ----------------------------
    private static final String PLACEHOLDER_PROVEEDOR = "Seleccionar proveedor";
    private static final String PLACEHOLDER_PRODUCTO = "Seleccionar producto";

    private final ArrayList<Palet> palets_oc = new ArrayList<>();
    private final ArrayList<Proveedor> proveedores_oc = new ArrayList<>();

    // ----------------------------
    // FXML
    // ----------------------------
    @FXML
    private Button ExitButton;
    @FXML
    private Button agregar_palet_oc_btn;
    @FXML
    private ComboBox<String> combo_producto_oc;
    @FXML
    private ComboBox<String> combo_proveedor_oc;
    @FXML
    private Button generar_compra_btn;
    @FXML
    public ListView<Parent> list_palets_agregados_oc;

    // ----------------------------
    // Estado
    // ----------------------------
    private final ObservableList<String> productosFiltrados = FXCollections.observableArrayList();
    private Map<String, Proveedor> proveedorPorNombre;

    // ----------------------------
    // Ciclo de vida
    // ----------------------------
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarBotonesVentana();
        construirCacheProveedores();
        inicializarComboBoxes();
        encadenarFiltrosProveedorProductos();
        configurarListViewConMenuContextual();

        // No dejar selección inicial en la lista
        javafx.application.Platform.runLater(() ->
                list_palets_agregados_oc.getSelectionModel().clearSelection()
        );
    }

    // ----------------------------
    // Configuración UI
    // ----------------------------
    private void configurarBotonesVentana() {
        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });

        if (generar_compra_btn != null) {
            generar_compra_btn.setOnAction(_ -> {
                try {
                    generarCompra();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        agregar_palet_oc_btn.setOnAction(_ -> agregarItemFXML());

    }

    private void construirCacheProveedores() {
        // Cache rápida nombre -> proveedor (asumiendo nombres únicos)
        proveedorPorNombre = Almacen.TodosProveedores.stream()
                .collect(Collectors.toMap(
                        Proveedor::getNombre,
                        p -> p,
                        (a, _) -> a,
                        LinkedHashMap::new
                ));
        LOGGER.fine(() -> "Proveedores cacheados: " + proveedorPorNombre.keySet());
    }

    private void inicializarComboBoxes() {
        // Proveedores
        combo_proveedor_oc.getItems().setAll(PLACEHOLDER_PROVEEDOR);
        combo_proveedor_oc.getItems().addAll(proveedorPorNombre.keySet());
        combo_proveedor_oc.getSelectionModel().selectFirst();

        // Productos (listado controlado por 'productosFiltrados')
        productosFiltrados.setAll(PLACEHOLDER_PRODUCTO);
        combo_producto_oc.setItems(productosFiltrados);
        combo_producto_oc.getSelectionModel().selectFirst();
    }

    private void encadenarFiltrosProveedorProductos() {
        combo_proveedor_oc.getSelectionModel()
                .selectedItemProperty()
                .addListener((_, _, nuevoProveedorNombre) -> {
                    LOGGER.fine(() -> "Proveedor seleccionado: " + nuevoProveedorNombre);
                    filtrarProductosPorProveedorAsync(nuevoProveedorNombre);
                });
    }

    private void configurarListViewConMenuContextual() {
        list_palets_agregados_oc.setCellFactory(lv -> {
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
        list_palets_agregados_oc.setOnKeyPressed(evt -> {
            if (evt.getCode() == javafx.scene.input.KeyCode.DELETE) {
                eliminarSeleccionado();
            }
        });
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


    // ----------------------------
    // Acciones
    // ----------------------------
    private void agregarItemFXML() {
        String proveedorSel = combo_proveedor_oc.getSelectionModel().getSelectedItem();
        String productoSel = combo_producto_oc.getSelectionModel().getSelectedItem();

        // Validación: ambos deben ser válidos (no placeholder ni vacío)
        if (isInvalidSelection(proveedorSel, PLACEHOLDER_PROVEEDOR) ||
                isInvalidSelection(productoSel, PLACEHOLDER_PRODUCTO)) {

            if (isInvalidSelection(proveedorSel, PLACEHOLDER_PROVEEDOR)) shake(combo_proveedor_oc, SHAKE_DURATION);
            if (isInvalidSelection(productoSel, PLACEHOLDER_PRODUCTO)) shake(combo_producto_oc, SHAKE_DURATION);
            // parpadearErrorWindowBar();
            LOGGER.fine("No se añadió ítem: selección inválida. Proveedor='" + proveedorSel + "', Producto='" + productoSel + "'");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ITEM_ORDEN_COMPRA_FXML));
            Parent itemRoot = loader.load();

            ItemOrdenCompraController ctrl = loader.getController();
            // Info visual en labels del item
            ctrl.set_basic_info(proveedorSel, productoSel);

            // Guardar el controller para generar compra luego
            itemRoot.setUserData(ctrl);

            list_palets_agregados_oc.getItems().add(itemRoot);
            list_palets_agregados_oc.scrollTo(list_palets_agregados_oc.getItems().size() - 1);

            LOGGER.fine("Ítem agregado al ListView para proveedor='" + proveedorSel + "', producto='" + productoSel + "'");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error cargando itemOrdenCompra.fxml", e);
            // parpadearErrorWindowBar();
        }
    }

    private void eliminarSeleccionado() {
        int idx = list_palets_agregados_oc.getSelectionModel().getSelectedIndex();
        if (idx >= 0) {
            list_palets_agregados_oc.getItems().remove(idx);
            LOGGER.fine("Ítem eliminado con tecla Supr en índice " + idx);
        }
    }

    private void generarCompra() throws SQLException {
        palets_oc.clear();
        proveedores_oc.clear();

        for (Parent node : list_palets_agregados_oc.getItems()) {
            Object ud = node.getUserData();
            if (ud instanceof ItemOrdenCompraController ctrl) {
                try {
                    String proveedor = ctrl.get_proveedor_nombre();
                    String producto = ctrl.get_producto_nombre();
                    String estanteria = ctrl.getCombo_estanteria_itemOc();
                    String balda = ctrl.getCombo_balda_itemOc();
                    String posicion = ctrl.getCombo_posicion_itemOc();
                    boolean delante = ctrl.getDelante_checkBox();

                    if (!validar_datos_en_blanco(ctrl, estanteria, balda, posicion) || !validar_palets_misma_posicion(ctrl, estanteria, balda, posicion, delante)) {
                        TODO_PALETS_OK = false;
                        continue;
                    }
                    Proveedor proveedor_oc = getProveedorPorNombre(proveedor);

                    Palet palet_oc = ctrl.crear_palet(proveedor, producto, Integer.parseInt(estanteria), Integer.parseInt(balda), Integer.parseInt(posicion), delante);
                    palet_oc.setCantidadProducto(proveedor_oc.getUnidadesPorPaletDefault(producto));
                    System.out.println(proveedor_oc.getUnidadesPorPaletDefault(producto));


                    palets_oc.add(palet_oc);
                    proveedores_oc.add(proveedor_oc);
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Fallo al crear palet de un ítem.", ex);
                }
            } else {
                // No encontramos controller en el nodo
                LOGGER.warning("Nodo en ListView sin controller válido (userData).");
            }
        }

        if (!TODO_PALETS_OK) {
            System.out.println("-----------------------------");
            alerta();
        } else {
            OrdenCompra oc = new OrdenCompra(palets_oc, proveedores_oc);
            oc.crearCodigoOrdenCompra(Main.connection, "");
            oc.insertarDetalleOrdenCompraPorCodigo(Main.connection);
            System.out.println(oc);
        }

        // LOGGER.info("Generar compra → Palets creados OK=" + ok + " | fallos=" + fail);
        // (Opcional) si todo OK, limpiar:
        // if (fail == 0) list_palets_agregados_oc.getItems().clear();
    }

    public void alerta() {
        ventana_warning("Contenido no válido", "Error al introducir los datos", "Es necesario rellenar todos los campos para pode generar la orden de compra");
        TODO_PALETS_OK = true;
    }

    private boolean validar_datos_en_blanco(ItemOrdenCompraController ctrl, String estanteria, String balda, String posicion) {

        int fail = 0;
        // Validación simple por si el usuario dejó algo sin seleccionar

        if (estanteria == null || estanteria.isBlank()) {
            LOGGER.warning("Ítem omitido por datos incompletos (cantidad/ubicación).");
            shake(ctrl.get_combo_estanteria_itemOc(), SHAKE_DURATION);
            fail++;
        }
        if (balda == null || balda.isBlank()) {
            LOGGER.warning("Ítem omitido por datos incompletos (cantidad/ubicación).");
            shake(ctrl.get_balda_itemOc(), SHAKE_DURATION);
            fail++;
        }
        if (posicion == null || posicion.isBlank()) {
            LOGGER.warning("Ítem omitido por datos incompletos (cantidad/ubicación).");
            shake(ctrl.get_combo_posicion_itemOc(), SHAKE_DURATION);
            fail++;
        }


        if (fail != 0) {
            ctrl.setBackground_Hbox("#B09000");
        }


        return fail == 0;
    }


    private boolean validar_palets_misma_posicion(ItemOrdenCompraController ctrl, String estanteria, String balda, String posicion, boolean delante) {

        int fail = 0;

        for (Palet p : palets_oc) {
            assert estanteria != null;
            if (p.getEstanteria() == Integer.parseInt(estanteria)) {
                assert balda != null;
                if (p.getBalda() == Integer.parseInt(balda)) {
                    assert posicion != null;
                    if (p.getPosicion() == Integer.parseInt(posicion) && p.isDelante() == delante) {
                        fail++;
                    }
                }
            }

        }

        if (fail != 0) {
            ctrl.setBackground_Hbox("#B09000");
        }


        return fail == 0;
    }


    // ----------------------------
    // Lógica de filtrado (BD)
    // ----------------------------
    private void filtrarProductosPorProveedorAsync(String proveedorNombre) {
        // Si está en placeholder, reseteamos productos
        if (proveedorNombre == null || proveedorNombre.equals(PLACEHOLDER_PROVEEDOR)) {
            productosFiltrados.setAll(PLACEHOLDER_PRODUCTO);
            combo_producto_oc.getSelectionModel().selectFirst();
            return;
        }

        Proveedor proveedor = proveedorPorNombre.get(proveedorNombre);
        if (proveedor == null) {
            productosFiltrados.setAll(PLACEHOLDER_PRODUCTO);
            combo_producto_oc.getSelectionModel().selectFirst();
            return;
        }

        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                Connection conn = Main.connection;
                List<String> ids = ProveedorProductoDAO
                        .obtenerIdentificadoresProductosPorProveedor(conn, proveedor.getIdProveedor());

                // Mantener el orden del catálogo si aplica
                Set<String> idsSet = new LinkedHashSet<>(ids);
                List<String> ordenadosPorCatalogo = Almacen.TodosProductos.stream()
                        .map(Producto::getIdentificadorProducto)
                        .filter(idsSet::contains)
                        .collect(Collectors.toList());

                return ordenadosPorCatalogo.isEmpty() ? ids : ordenadosPorCatalogo;
            }
        };

        task.setOnSucceeded(_ -> {
            List<String> ids = task.getValue();
            productosFiltrados.setAll(PLACEHOLDER_PRODUCTO);
            productosFiltrados.addAll(ids);
            combo_producto_oc.getSelectionModel().selectFirst();
            LOGGER.fine(() -> "Productos filtrados para proveedor '" + proveedorNombre + "': " + ids);
        });

        task.setOnFailed(_ -> {
            productosFiltrados.setAll(PLACEHOLDER_PRODUCTO);
            combo_producto_oc.getSelectionModel().selectFirst();
            Throwable ex = task.getException();
            LOGGER.log(Level.SEVERE, "Error filtrando productos por proveedor '" + proveedorNombre + "'", ex);
            // parpadearErrorWindowBar();
        });

        new Thread(task, "FiltrarProductosProveedor").start();
    }

    // ----------------------------
    // Utilidades de UI
    // ----------------------------
    private static boolean isValidSelection(String value, String placeholder) {
        return value != null && !value.isBlank() && !value.equals(placeholder);
    }

    private static boolean isInvalidSelection(String value, String placeholder) {
        return !isValidSelection(value, placeholder);
    }

    // ----------------------------
    // DTO (reservado para cuando quieras data-driven)
    // ----------------------------
    public record ItemOC(String producto, String proveedor) {
    }
}
