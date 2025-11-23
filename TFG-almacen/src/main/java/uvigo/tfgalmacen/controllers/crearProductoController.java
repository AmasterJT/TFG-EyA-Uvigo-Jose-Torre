package uvigo.tfgalmacen.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Proveedor;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.Palet;
import uvigo.tfgalmacen.almacenManagement.Producto;
import uvigo.tfgalmacen.almacenManagement.Tipo;
import uvigo.tfgalmacen.database.ProductoDAO;
import uvigo.tfgalmacen.database.ProveedorDAO;
import uvigo.tfgalmacen.database.ProveedorProductoDAO;
import uvigo.tfgalmacen.database.TipoDAO;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.net.URL;
import java.sql.Connection;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static uvigo.tfgalmacen.utils.TerminalColors.CYAN;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.*;


public class crearProductoController implements Initializable {


    private static final Logger LOGGER = Logger.getLogger(crearProductoController.class.getName());

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


    private boolean TODO_PALETS_OK = false;

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
    private AnchorPane Pane;

    @FXML
    private ComboBox<String> combo_proveedor;

    @FXML
    private ComboBox<String> combo_tipo;

    @FXML
    private Button crear_nuevo_producto_btn;

    @FXML
    private TextField descripcicon_nuevo_producto_text;

    @FXML
    private TextField nombre_nuevo_producto_text;

    @FXML
    private TextField precio_nuevo_producto_text;

    @FXML
    private HBox windowBar;

    @FXML
    private TextField profundo_nuevo_producto_text;

    @FXML
    private TextField alto_nuevo_producto_text;

    @FXML
    private TextField ancho_nuevo_producto_text;

    @FXML
    private TextField unidades_por_defecto_text;

    // ----------------------------
    // Estado
    // ----------------------------
    private final ObservableList<String> tiposFiltrados = FXCollections.observableArrayList();
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

        unidades_por_defecto_text.setTextFormatter(numericFormatter());
        ancho_nuevo_producto_text.setTextFormatter(numericFormatter());
        alto_nuevo_producto_text.setTextFormatter(numericFormatter());
        profundo_nuevo_producto_text.setTextFormatter(numericFormatter());

    }


    // ----------------------------
    // Configuración UI
    // ----------------------------
    private void configurarBotonesVentana() {
        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });

        if (crear_nuevo_producto_btn != null) {
            crear_nuevo_producto_btn.setOnAction(_ -> {
                crearNuevoProducto();
            });
        }

    }

    private void crearNuevoProducto() {

        if (!validarCamposNumericos()) {
            LOGGER.warning("Creación de producto cancelada por datos no numéricos.");
            return;
        }
        
        String proveedor = combo_proveedor.getValue();
        String tipo = combo_tipo.getValue();
        String nombre_producto = nombre_nuevo_producto_text.getText();
        String descripcion = descripcicon_nuevo_producto_text.getText() == null ? "-" : descripcicon_nuevo_producto_text.getText();
        String precio = precio_nuevo_producto_text.getText() == null ? "-" : precio_nuevo_producto_text.getText();

        ProductoDAO.crearNuevoProducto(
                Main.connection,
                nombre_nuevo_producto_text.getText(),
                combo_tipo.getValue(),
                descripcicon_nuevo_producto_text.getText(),
                precio_nuevo_producto_text.getText()
        );

        int unidades_por_defecto = unidades_por_defecto_text.getText() == null ? 0 : parseInt(unidades_por_defecto_text.getText());
        int ancho_nuevo_producto = ancho_nuevo_producto_text.getText() == null ? 0 : parseInt(ancho_nuevo_producto_text.getText());
        int alto_nuevo_producto = alto_nuevo_producto_text.getText() == null ? 0 : parseInt(alto_nuevo_producto_text.getText());
        int profundo_nuevo_producto = profundo_nuevo_producto_text.getText() == null ? 0 : parseInt(profundo_nuevo_producto_text.getText());

        int id_nuevo_produto = ProductoDAO.getIdProductoByIdentificadorProducto(Main.connection, nombre_producto);
        int id_proveedor = ProveedorDAO.getIdProveedorByNombre(Main.connection, proveedor);

        ProveedorProductoDAO.setRelacionProductoProveedor(Main.connection, id_nuevo_produto, id_proveedor, alto_nuevo_producto, ancho_nuevo_producto, profundo_nuevo_producto, unidades_por_defecto);

        ventana_warning("Tipo nuevo creado", "tipo nuevo creado correctamente",
                String.format("Se ha creado un nuevo producto: %s  \n\t - %s (%s) \n\t - Descripcion: %s \n\t - Precio: %s", nombre_producto, proveedor, tipo, descripcion, precio));

        Stage stage = (Stage) crear_nuevo_producto_btn.getScene().getWindow();
        stage.close();
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
        combo_proveedor.getItems().setAll(PLACEHOLDER_PROVEEDOR);
        combo_proveedor.getItems().addAll(proveedorPorNombre.keySet());
        combo_proveedor.getSelectionModel().selectFirst();

        // Productos (listado controlado por 'productosFiltrados')
        tiposFiltrados.setAll(PLACEHOLDER_PRODUCTO);
        combo_tipo.setItems(tiposFiltrados);
        combo_tipo.getSelectionModel().selectFirst();
    }

    private void encadenarFiltrosProveedorProductos() {
        combo_proveedor.getSelectionModel()
                .selectedItemProperty()
                .addListener((_, _, nuevoProveedorNombre) -> {
                    LOGGER.fine(() -> "Proveedor seleccionado: " + nuevoProveedorNombre);
                    filtrarTiposPorProveedorAsync(nuevoProveedorNombre);
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

    private boolean validar_datos_en_blanco(ItemOrdenCompraController ctrl, String estanteria, String balda, String posicion) {

        int fail = 0;
        // Validación simple por si el usuario dejó algo sin seleccionar

        if (estanteria == null || estanteria.isBlank()) {
            LOGGER.warning("Ítem omitido por datos incompletos (estanteria).");
            shake(ctrl.get_combo_estanteria_itemOc(), SHAKE_DURATION);
            fail++;
        }
        if (balda == null || balda.isBlank()) {
            LOGGER.warning("Ítem omitido por datos incompletos (balda).");
            shake(ctrl.get_balda_itemOc(), SHAKE_DURATION);
            fail++;
        }
        if (posicion == null || posicion.isBlank()) {
            LOGGER.warning("Ítem omitido por datos incompletos (posicion).");
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
    private void filtrarTiposPorProveedorAsync(String proveedorNombre) {
        // Si está en placeholder, reseteamos productos
        if (proveedorNombre == null || proveedorNombre.equals(PLACEHOLDER_PROVEEDOR)) {
            tiposFiltrados.setAll(PLACEHOLDER_PRODUCTO);
            combo_tipo.getSelectionModel().selectFirst();
            return;
        }

        Proveedor proveedor = proveedorPorNombre.get(proveedorNombre);
        if (proveedor == null) {
            tiposFiltrados.setAll(PLACEHOLDER_PRODUCTO);
            combo_tipo.getSelectionModel().selectFirst();
            return;
        }

        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                Connection conn = Main.connection;
                List<String> ids = TipoDAO.getTiposByProveedor(conn, proveedor.getIdProveedor());

                // Mantener el orden del catálogo si aplica
                Set<String> idsSet = new LinkedHashSet<>(ids);
                List<String> ordenadosPorCatalogo = Almacen.TodosTipos.stream()
                        .map(Tipo::getIdTipo)
                        .filter(idsSet::contains)
                        .collect(Collectors.toList());

                return ordenadosPorCatalogo.isEmpty() ? ids : ordenadosPorCatalogo;
            }
        };

        task.setOnSucceeded(_ -> {
            List<String> ids = task.getValue();
            tiposFiltrados.setAll(PLACEHOLDER_PRODUCTO);
            tiposFiltrados.addAll(ids);
            combo_tipo.getSelectionModel().selectFirst();
            LOGGER.fine(() -> "Productos filtrados para proveedor '" + proveedorNombre + "': " + ids);
        });

        task.setOnFailed(_ -> {
            tiposFiltrados.setAll(PLACEHOLDER_PRODUCTO);
            combo_tipo.getSelectionModel().selectFirst();
            Throwable ex = task.getException();
            LOGGER.log(Level.SEVERE, "Error filtrando productos por proveedor '" + proveedorNombre + "'", ex);
            // parpadearErrorWindowBar();
        });

        new Thread(task, "FiltrarProductosProveedor").start();
    }


    // ----------------------------
    // DTO (reservado para cuando quieras data-driven)
    // ----------------------------
    public record ItemOC(String producto, String proveedor) {
    }


    /**
     * Valida que todos los TextField numéricos contengan solo números.
     * Si hay error, colorea en rojo, los sacude y muestra una alerta.
     */
    private boolean validarCamposNumericos() {
        List<TextField> camposNumericos = List.of(
                unidades_por_defecto_text,
                ancho_nuevo_producto_text,
                alto_nuevo_producto_text,
                profundo_nuevo_producto_text
        );

        boolean hayError = false;

        for (TextField campo : camposNumericos) {
            String valor = campo.getText();

            // Si el campo está vacío lo ignoramos, pero si tiene letras => error
            if (valor != null && !valor.isBlank() && !valor.matches("\\d+")) {
                hayError = true;

                // Color de error
                campo.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-background-color: #ffcccc;");

                // Sacudir
                shake(campo, SHAKE_DURATION);

                // Restaurar estilo al hacer clic
                campo.setOnMouseClicked(_ -> campo.setStyle(""));

                LOGGER.warning(() -> "Entrada no numérica detectada en campo: " + campo.getId());
            }
        }

        if (hayError) {
            ventana_warning(
                    "Error en los datos",
                    "Campos numéricos inválidos",
                    "Algunos campos contienen letras o símbolos no válidos.\n" +
                            "Por favor, revisa los valores antes de continuar."
            );
        }

        return !hayError; // true si todo correcto
    }


}
