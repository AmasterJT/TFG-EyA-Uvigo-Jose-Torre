package uvigo.tfgalmacen.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Proveedor;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.Palet;
import uvigo.tfgalmacen.almacenManagement.Producto;
import uvigo.tfgalmacen.database.ProveedorProductoDAO;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.shake;

public class ordenCompraController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(ordenCompraController.class.getName());
    private boolean TODO_PALETS_OK = true;

    // ----------------------------
    // Constantes / Placeholders
    // ----------------------------
    private static final String PLACEHOLDER_PROVEEDOR = "Seleccionar proveedor";
    private static final String PLACEHOLDER_PRODUCTO = "Seleccionar producto";
    private static final Duration SHAKE_DURATION = Duration.millis(50);

    private final ArrayList<Palet> palets_oc = new ArrayList<>();

    // ----------------------------
    // FXML
    // ----------------------------
    @FXML
    private Button ExitButton;
    @FXML
    private AnchorPane Pane;
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
    @FXML
    private HBox windowBar;

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
            generar_compra_btn.setOnAction(_ -> generarCompra());
        }

        agregar_palet_oc_btn.setOnAction(_ -> agregarItemFXML());

    }

    private void construirCacheProveedores() {
        // Cache rápida nombre -> proveedor (asumiendo nombres únicos)
        proveedorPorNombre = Almacen.TodosProveedores.stream()
                .collect(Collectors.toMap(
                        Proveedor::getNombre,
                        p -> p,
                        (a, b) -> a,
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
                .addListener((obs, oldVal, nuevoProveedorNombre) -> {
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
        eliminar.setOnAction(e -> {
            int index = lv.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                lv.getItems().remove(index);
                lv.getSelectionModel().clearSelection();
                LOGGER.fine("Ítem eliminado del ListView en índice " + index);
            }
            menu.hide();
        });

        // --- Acción borrar todo (con confirmación) ---
        borrarTodo.setOnAction(e -> {
            if (lv.getItems().isEmpty()) {
                return;
            }

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar borrado");
            confirmacion.setHeaderText("¿Seguro que deseas borrar todos los ítems?");
            confirmacion.setContentText("Esta acción eliminará permanentemente todos los elementos de la lista.");

            // Personalizar botones
            ButtonType btnSi = new ButtonType("Sí, borrar todo", ButtonBar.ButtonData.OK_DONE);
            ButtonType btnNo = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmacion.getButtonTypes().setAll(btnSi, btnNo);

            // Aplicar estilo (opcional)
            DialogPane dialogPane = confirmacion.getDialogPane();
            try {
                dialogPane.getStylesheets().add(
                        Objects.requireNonNull(getClass().getResource("/uvigo/tfgalmacen/Styles.css")).toExternalForm()
                );
                dialogPane.getStyleClass().add("alert-dialog");
            } catch (Exception ex) {
                LOGGER.log(Level.FINE, "No se pudo cargar Styles.css para el diálogo de confirmación.", ex);
            }

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == btnSi) {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uvigo/tfgalmacen/itemOrdenCompra.fxml"));
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

    private void generarCompra() {
        palets_oc.clear();

        for (Parent node : list_palets_agregados_oc.getItems()) {
            Object ud = node.getUserData();
            if (ud instanceof ItemOrdenCompraController ctrl) {
                try {
                    String proveedor = ctrl.get_proveedor_nombre();
                    String producto = ctrl.get_producto_nombre();
                    String cantidad = ctrl.getCant_producto_text();
                    String estanteria = ctrl.getCombo_estanteria_itemOc();
                    String balda = ctrl.getCombo_balda_itemOc();
                    String posicion = ctrl.getCombo_posicion_itemOc();
                    boolean delante = ctrl.getDelante_checkBox();

                    if (!validar_palets(ctrl, cantidad, estanteria, balda, posicion)) {
                        TODO_PALETS_OK = false;
                        continue;
                    }

                    Palet palet_oc = ctrl.crear_palet(proveedor, producto, Integer.parseInt(cantidad), Integer.parseInt(estanteria), Integer.parseInt(balda), Integer.parseInt(posicion), delante);

                    palets_oc.add(palet_oc);
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Fallo al crear palet de un ítem.", ex);
                }
            } else {
                // No encontramos controller en el nodo
                LOGGER.warning("Nodo en ListView sin controller válido (userData).");
            }
        }

        if (!TODO_PALETS_OK) {
            Alert confirmacion = new Alert(Alert.AlertType.ERROR);
            confirmacion.setTitle("Contenido no válido");
            confirmacion.setHeaderText("Error al introducir los datos");
            confirmacion.setContentText("Es necesario rellenar todos los campos para pode generar la orden de compra");

            // Aplicar estilo (opcional)
            DialogPane dialogPane = confirmacion.getDialogPane();
            try {
                dialogPane.getStylesheets().add(
                        Objects.requireNonNull(getClass().getResource("/uvigo/tfgalmacen/Styles.css")).toExternalForm()
                );
                dialogPane.getStyleClass().add("alert-dialog");
            } catch (Exception ex) {
                LOGGER.log(Level.FINE, "No se pudo cargar Styles.css para el diálogo de confirmación.", ex);
            }

            confirmacion.showAndWait();
        }

        System.out.println(palets_oc);


        // LOGGER.info("Generar compra → Palets creados OK=" + ok + " | fallos=" + fail);
        // (Opcional) si todo OK, limpiar:
        // if (fail == 0) list_palets_agregados_oc.getItems().clear();
    }

    private boolean validar_palets(ItemOrdenCompraController ctrl, String cantidad, String estanteria, String balda, String posicion) {

        int fail = 0;
        // Validación simple por si el usuario dejó algo sin seleccionar
        if (cantidad == null || cantidad.isBlank()) {
            LOGGER.warning("Ítem omitido por datos incompletos (cantidad/ubicación).");
            shake(ctrl.get_cant_producto_text(), SHAKE_DURATION);
            fail++;
        }
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


    private void parpadearErrorWindowBar() {
        if (windowBar == null) return;

        final String original = windowBar.getStyle();
        Timeline t = new Timeline(
                new KeyFrame(Duration.ZERO, e -> windowBar.setStyle("-fx-background-color:rgba(62,36,17,0.47);")),
                new KeyFrame(Duration.millis(80), e -> windowBar.setStyle("-fx-background-color:#3e2411;")),
                new KeyFrame(Duration.millis(160), e -> windowBar.setStyle("-fx-background-color:rgba(62,36,17,0.47);")),
                new KeyFrame(Duration.millis(240), e -> windowBar.setStyle("-fx-background-color:#3e2411;"))
        );
        t.setCycleCount(3);
        t.setOnFinished(e -> windowBar.setStyle(original));
        t.play();
    }

    // ----------------------------
    // DTO (reservado para cuando quieras data-driven)
    // ----------------------------
    public record ItemOC(String producto, String proveedor) {
    }
}
