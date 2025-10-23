package uvigo.tfgalmacen.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.Producto;
import uvigo.tfgalmacen.Proveedor;
import uvigo.tfgalmacen.database.ProveedorProductoDAO;
import uvigo.tfgalmacen.Main;

import java.net.URL;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

import javafx.animation.*;
import javafx.util.Duration;

public class ordenCompraController implements Initializable {

    @FXML
    private Button ExitButton;
    @FXML
    private AnchorPane Pane;
    @FXML
    private Button agregar_palet_oc;
    @FXML
    private ComboBox<String> combo_producto_oc;
    @FXML
    private ComboBox<String> combo_proveedor_oc;
    @FXML
    private Button generar_compra_btn;
    @FXML
    private ListView<?> list_palets_agregados_oc;
    @FXML
    private HBox windowBar;

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

        if (agregar_palet_oc != null) {
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
                new KeyFrame(Duration.ZERO, e -> windowBar.setStyle("-fx-background-color:#b33;")),
                new KeyFrame(Duration.millis(160), e -> windowBar.setStyle(original))
        );
        t.setCycleCount(4);
        t.play();
    }
}
