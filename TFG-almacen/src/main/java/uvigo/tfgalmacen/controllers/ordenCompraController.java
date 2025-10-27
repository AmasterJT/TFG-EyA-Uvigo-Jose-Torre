package uvigo.tfgalmacen.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.Palet;
import uvigo.tfgalmacen.almacenManagement.Producto;
import uvigo.tfgalmacen.Proveedor;
import uvigo.tfgalmacen.database.ProveedorProductoDAO;
import uvigo.tfgalmacen.Main;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

import javafx.animation.*;
import javafx.util.Duration;

import static java.nio.file.attribute.AclEntryPermission.DELETE;

public class ordenCompraController implements Initializable {

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
    public ListView<Parent> list_palets_agregados_oc; // <— tipado
    private final ObservableList<ItemOC> itemsOC = FXCollections.observableArrayList();

    @FXML
    private HBox windowBar;

    private ArrayList<Palet> palets_del_pedidp = null;

    private static final String PLACEHOLDER_PROVEEDOR = "Seleccionar proveedor";
    private static final String PLACEHOLDER_PRODUCTO = "Seleccionar producto";

    private final ObservableList<String> productosFiltrados = FXCollections.observableArrayList();
    private Map<String, Proveedor> proveedorPorNombre; // cache nombre -> proveedor

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });

        if (generar_compra_btn != null) {
            generar_compra_btn.setOnAction(_ -> setRecepcion());
        }

        // Cache rápida nombre -> proveedor (asumiendo nombres únicos)
        proveedorPorNombre = Almacen.TodosProveedores.stream()
                .collect(Collectors.toMap(Proveedor::getNombre, p -> p, (a, b) -> a, LinkedHashMap::new));

        inicializarComboBoxes();

        // Encadenar: cuando cambie proveedor, filtramos productos
        combo_proveedor_oc.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, nuevoProveedorNombre) -> {
            filtrarProductosPorProveedorAsync(nuevoProveedorNombre);
        });


        list_palets_agregados_oc.setCellFactory(lv -> {
            ListCell<Parent> cell = new ListCell<>() {
                @Override
                protected void updateItem(Parent item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(null);
                    setGraphic(empty ? null : item);
                }
            };

            // Menú contextual por celda
            ContextMenu menu = new ContextMenu();
            MenuItem eliminar = new MenuItem("Eliminar");

            eliminar.setOnAction(e -> {
                int index = cell.getIndex();
                if (index >= 0 && index < lv.getItems().size()) {
                    lv.getItems().remove(index); // elimina la fila actual
                }
            });

            menu.getItems().add(eliminar);

            // Solo mostrar menú si la celda NO está vacía
            cell.emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
                cell.setContextMenu(isEmpty ? null : menu);
            });

            // Al abrir el menú, selecciona la celda (útil si haces clic derecho sin seleccionar antes)
            cell.setOnContextMenuRequested(evt -> {
                if (!cell.isEmpty()) {
                    lv.getSelectionModel().select(cell.getIndex());
                }
            });

            return cell;
        });


        list_palets_agregados_oc.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Parent item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setGraphic(empty ? null : item);
            }
        });

        list_palets_agregados_oc.setOnKeyPressed(evt -> {
            if (Objects.requireNonNull(evt.getCode()) == KeyCode.DELETE) {
                eliminarSeleccionado();
            }
        });

        agregar_palet_oc_btn.setOnAction(_ -> agregarItemFXML());

    }

    // --- Inserta visualmente el ítem cargando itemOrdenCompra.fxml ---
    private void agregarItemFXML() {
        String proveedorSel = combo_proveedor_oc.getSelectionModel().getSelectedItem();
        String productoSel = combo_producto_oc.getSelectionModel().getSelectedItem();

        boolean proveedorOK = seleccionValida(proveedorSel, PLACEHOLDER_PROVEEDOR);
        boolean productoOK = seleccionValida(productoSel, PLACEHOLDER_PRODUCTO);

        if (!proveedorOK || !productoOK) {
            // parpadearErrorWindowBar();
            // (Opcional) micro-animación para llamar la atención en los combos no válidos
            if (!proveedorOK) shake(combo_proveedor_oc);
            if (!productoOK) shake(combo_producto_oc);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uvigo/tfgalmacen/itemOrdenCompra.fxml"));
            Parent itemRoot = loader.load();

            list_palets_agregados_oc.getItems().add(itemRoot);
            list_palets_agregados_oc.scrollTo(list_palets_agregados_oc.getItems().size() - 1);

        } catch (IOException e) {
            e.printStackTrace();
            parpadearErrorWindowBar();
        }
    }

    private boolean seleccionValida(String valor, String placeholder) {
        return valor != null && !valor.isBlank() && !valor.equals(placeholder);
    }


    private void shake(javafx.scene.Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(4);
        tt.setAutoReverse(true);
        tt.setCycleCount(2);
        tt.play();
    }

    private void setRecepcion() {
        System.out.println("compra generada");
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

    /**
     * Filtra productos en segundo plano para no bloquear la UI
     */
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

        // Cargar desde BD en thread aparte
        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                Connection conn = Main.connection; // o tu forma de obtener la conexión
                List<String> ids = ProveedorProductoDAO.obtenerIdentificadoresProductosPorProveedor(conn, proveedor.getIdProveedor());

                // Si quieres mantener el orden igual que en Almacen.TodosProductos:
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
            // En caso de error, dejamos solo placeholder
            productosFiltrados.setAll(PLACEHOLDER_PRODUCTO);
            combo_producto_oc.getSelectionModel().selectFirst();
            // (Opcional) muestra un parpadeo rojo en la barra para avisar
            parpadearErrorWindowBar();
        });

        new Thread(task, "FiltrarProductosProveedor").start();
    }

    private void parpadearErrorWindowBar() {
        if (windowBar == null) return;
        String original = windowBar.getStyle();
        Timeline t = new Timeline(
                new KeyFrame(Duration.ZERO, e -> windowBar.setStyle("-fx-background-color:rgba(62,36,17,0.47);")),
                new KeyFrame(Duration.millis(10), _ -> windowBar.setStyle("-fx-background-color:#3e2411;")),

                new KeyFrame(Duration.millis(10), e -> windowBar.setStyle("-fx-background-color:rgba(62,36,17,0.47);")),
                new KeyFrame(Duration.millis(40), e -> windowBar.setStyle("-fx-background-color:#3e2411;")),

                new KeyFrame(Duration.millis(10), e -> windowBar.setStyle("-fx-background-color:rgba(62,36,17,0.47);")),
                new KeyFrame(Duration.millis(10), e -> windowBar.setStyle("-fx-background-color:#3e2411;"))
        );
        t.setCycleCount(4);
        t.play();
    }


    // DTO para lo que quieres mostrar por cada ítem
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


    private void eliminarSeleccionado() {
        int idx = list_palets_agregados_oc.getSelectionModel().getSelectedIndex();
        if (idx >= 0) {
            list_palets_agregados_oc.getItems().remove(idx);
        }
    }
}



