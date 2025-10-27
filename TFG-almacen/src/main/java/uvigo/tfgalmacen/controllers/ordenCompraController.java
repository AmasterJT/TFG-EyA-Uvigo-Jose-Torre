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
import uvigo.tfgalmacen.almacenManagement.Producto;
import uvigo.tfgalmacen.database.ProveedorProductoDAO;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

public class ordenCompraController implements Initializable {

    // ----------------------------
    // Constantes / Placeholders
    // ----------------------------
    private static final String PLACEHOLDER_PROVEEDOR = "Seleccionar proveedor";
    private static final String PLACEHOLDER_PRODUCTO = "Seleccionar producto";
    private static final Duration SHAKE_DURATION = Duration.millis(50);

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
        configurarAcciones();

        // No dejar selección inicial en la lista
        // (hacerlo al final para que la escena ya esté lista)
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
                .addListener((obs, oldVal, nuevoProveedorNombre) -> filtrarProductosPorProveedorAsync(nuevoProveedorNombre));
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

            // Aplicar estilo (opcional, para que combine con tu app)
            DialogPane dialogPane = confirmacion.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/uvigo/tfgalmacen/Styles.css").toExternalForm()
            );
            dialogPane.getStyleClass().add("alert-dialog");

            // Mostrar y procesar la respuesta
            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == btnSi) {
                lv.getItems().clear();
                lv.getSelectionModel().clearSelection();
            }

            menu.hide();
        });

        // --- Deshabilitar cuando no procede ---
        eliminar.disableProperty().bind(lv.getSelectionModel().selectedIndexProperty().lessThan(0));

        menu.getItems().addAll(eliminar, borrarTodo);
        return menu;
    }


    private void configurarAcciones() {
        agregar_palet_oc_btn.setOnAction(_ -> agregarItemFXML());
    }

    // ----------------------------
    // Acciones
    // ----------------------------
    private void agregarItemFXML() {
        String proveedorSel = combo_proveedor_oc.getSelectionModel().getSelectedItem();
        String productoSel = combo_producto_oc.getSelectionModel().getSelectedItem();

        if (!seleccionValida(proveedorSel, PLACEHOLDER_PROVEEDOR) ||
                !seleccionValida(productoSel, PLACEHOLDER_PRODUCTO)) {
            if (!seleccionValida(proveedorSel, PLACEHOLDER_PROVEEDOR)) shake(combo_proveedor_oc);
            if (!seleccionValida(productoSel, PLACEHOLDER_PRODUCTO)) shake(combo_producto_oc);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uvigo/tfgalmacen/itemOrdenCompra.fxml"));
            Parent itemRoot = loader.load();

            ItemOrdenCompraController ctrl = loader.getController();
            // Info visual en labels del item (opcional)
            ctrl.set_basic_info(proveedorSel, productoSel);
            // Inicializar combos de ubicaciones desde la BD

            // Guardar el controller para generar compra luego (si ya lo tienes así)
            itemRoot.setUserData(ctrl);

            list_palets_agregados_oc.getItems().add(itemRoot);
            list_palets_agregados_oc.scrollTo(list_palets_agregados_oc.getItems().size() - 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void eliminarSeleccionado() {
        int idx = list_palets_agregados_oc.getSelectionModel().getSelectedIndex();
        if (idx >= 0) {
            list_palets_agregados_oc.getItems().remove(idx);
        }
    }

    private void generarCompra() {
        int ok = 0, fail = 0;

        for (Parent node : list_palets_agregados_oc.getItems()) {
            Object ud = node.getUserData();
            if (ud instanceof ItemOrdenCompraController ctrl) {
                try {
                    // Si crear_palet retorna algo (p.ej. Palet), puedes recogerlo:
                    // Palet p = ctrl.crear_palet();
                    String cantidad = ctrl.getCant_producto_text();
                    String estanteria = ctrl.getCombo_estanteria_itemOc();
                    String balda = ctrl.getCombo_balda_itemOc();
                    String posicion = ctrl.getCombo_posicion_itemOc();
                    boolean delante = ctrl.getDelante_checkBox();
                    ctrl.crear_palet(cantidad, estanteria, balda, posicion, delante);
                    ok++;
                } catch (Exception ex) {
                    fail++;
                    ex.printStackTrace();
                }
            } else {
                // No encontramos controller en el nodo
                fail++;
            }
        }

        // Feedback mínimo (ajústalo a tu UI)
        System.out.println("Palets creados: " + ok + " | fallos: " + fail);
        // (Opcional) si todo OK, limpia la lista:
        // if (fail == 0) list_palets_agregados_oc.getItems().clear();
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
        });

        task.setOnFailed(_ -> {
            productosFiltrados.setAll(PLACEHOLDER_PRODUCTO);
            combo_producto_oc.getSelectionModel().selectFirst();
        });

        new Thread(task, "FiltrarProductosProveedor").start();
    }

    // ----------------------------
    // Utilidades de UI
    // ----------------------------
    private static boolean seleccionValida(String valor, String placeholder) {
        return valor != null && !valor.isBlank() && !valor.equals(placeholder);
    }

    private void shake(javafx.scene.Node node) {
        TranslateTransition tt = new TranslateTransition(SHAKE_DURATION, node);
        tt.setFromX(0);
        tt.setByX(4);
        tt.setAutoReverse(true);
        tt.setCycleCount(2);
        tt.play();
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
    public static class ItemOC {
        private final String producto;
        private final String proveedor;

        public ItemOC(String producto, String proveedor) {
            this.producto = producto;
            this.proveedor = proveedor;
        }

        public String getProducto() {
            return producto;
        }

        public String getProveedor() {
            return proveedor;
        }
    }
}
