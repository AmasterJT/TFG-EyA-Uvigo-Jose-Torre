package uvigo.tfgalmacen.controllers.apartadosAjustesControllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.Producto;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.database.DetallesPedidoDAO.borrarDetallePorId;
import static uvigo.tfgalmacen.database.DetallesPedidoDAO.updateDetalleProductoYCantidad;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.numericFormatter;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.ventana_error;

public class ItemEditarPedidoController {

    private static final Logger LOGGER = Logger.getLogger(ItemEditarPedidoController.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new ColorFormatter());
        LOGGER.addHandler(ch);
        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) h.setLevel(Level.ALL);
    }

    private static final String PLACEHOLDER_ITEM_EDITAR_PEDIDO = "Seleccionar producto";


    private int id_detalle_pedido = 0;

    @FXML
    private TextField cantidad_producto;
    @FXML
    private ComboBox<Producto> combo_producto_editar_pedido;
    @FXML
    private Button delete_item;
    @FXML
    private Button editar_producto_btn;
    @FXML
    private ImageView imagen_editar_producto_btn;


    private boolean editMode = false;

    // Callback inyectado por el padre para eliminar el item del ListView
    private Runnable onRemove;

    public void setOnRemove(Runnable onRemove) {
        this.onRemove = onRemove;
    }

    @FXML
    public void initialize() {
        LOGGER.fine("Inicializando item");
        setEditable(false);
        inicializarComboBoxes();

        editar_producto_btn.setOnAction(_ -> {
            LOGGER.fine("Click en editar/guardar (id_detalle=" + id_detalle_pedido + ")");
            toggleEdicion();
        });

        // Confirmación de borrado usando tu ventana personalizada
        delete_item.setOnAction(_ -> {
            LOGGER.info("Solicitada eliminación del item (id_detalle=" + id_detalle_pedido + ")");
            Optional<ButtonType> resultado = ventana_error(
                    "Confirmar eliminación",
                    "¿Deseas eliminar este producto del pedido?",
                    "Esta acción eliminará el producto seleccionado del pedido.",
                    "Sí, eliminar", "Cancelar"
            );

            if (resultado.isPresent() && resultado.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                boolean eliminado = borrarDetallePorId(Main.connection, id_detalle_pedido);

                if (eliminado) {
                    LOGGER.info("Item eliminado también de la base de datos. id_detalle=" + id_detalle_pedido);
                    if (onRemove != null) onRemove.run();
                } else {
                    LOGGER.warning("No se pudo eliminar el detalle en la base de datos. id_detalle=" + id_detalle_pedido);
                }
            } else {
                LOGGER.fine("Eliminación cancelada por el usuario (id_detalle=" + id_detalle_pedido + ")");
            }
        });


        cantidad_producto.setTextFormatter(numericFormatter());
    }

    private void toggleEdicion() {
        editMode = !editMode;
        setEditable(editMode);
        LOGGER.info("MODO EDICIÓN: Edit mode = " + editMode + " (id_detalle=" + id_detalle_pedido + ")");

        if (!editMode) {
            updateDetalleProductoYCantidad(Main.connection,
                    id_detalle_pedido,
                    combo_producto_editar_pedido.getValue().getIndex_BDD(),
                    Integer.parseInt(cantidad_producto.getText())
            );
        }

        // Icono dinámico
        String imagePath = editMode ? "/icons/save-50.png" : "/icons/edit-50.png";
        imagen_editar_producto_btn.setImage(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)))
        );

        // Color del botón según modo
        editar_producto_btn.setStyle(editMode
                ? "-fx-background-color: #394955 !important;"
                : "-fx-background-color: #db9f75 !important;");

        // Hover según modo
        configurarHoverModo(editMode);
    }

    /**
     * Configura el efecto hover dependiendo del modo actual.
     * Si está en modo guardar, aplica un color diferente cuando el mouse entra/sale.
     */
    private void configurarHoverModo(boolean modoGuardar) {
        editar_producto_btn.setOnMouseEntered(null);
        editar_producto_btn.setOnMouseExited(null);

        if (modoGuardar) {
            editar_producto_btn.setOnMouseEntered(e ->
                    editar_producto_btn.setStyle("-fx-background-color: #466273 !important;"));
            editar_producto_btn.setOnMouseExited(e ->
                    editar_producto_btn.setStyle("-fx-background-color: #394955 !important;"));
            LOGGER.info("Hover configurado para MODO GUARDAR");
        } else {
            editar_producto_btn.setOnMouseEntered(e ->
                    editar_producto_btn.setStyle("-fx-background-color: #e6b490 !important;"));
            editar_producto_btn.setOnMouseExited(e ->
                    editar_producto_btn.setStyle("-fx-background-color: #db9f75 !important;"));
            LOGGER.info("Hover configurado para MODO EDITAR");
        }
    }

    private void setEditable(boolean on) {
        combo_producto_editar_pedido.setDisable(!on);
        cantidad_producto.setDisable(!on);
        cantidad_producto.setEditable(on);
        if (on) cantidad_producto.requestFocus();
        LOGGER.info("Campos " + (on ? "habilitados" : "deshabilitados"));
    }

    private void inicializarComboBoxes() {
        combo_producto_editar_pedido.setPromptText(PLACEHOLDER_ITEM_EDITAR_PEDIDO);
        combo_producto_editar_pedido.getItems().setAll(Almacen.TodosProductos);
        combo_producto_editar_pedido.setConverter(new StringConverter<>() {
            @Override
            public String toString(Producto p) {
                return (p != null) ? p.getIdentificadorProducto() : "";
            }

            @Override
            public Producto fromString(String s) {
                return null;
            }
        });
        LOGGER.info("Combo de productos inicializado con " + Almacen.TodosProductos.size() + " items.");
    }

    // Getters/Setters útiles
    public TextField getCantidad_producto() {
        return cantidad_producto;
    }

    public void setCantidad_producto(TextField cantidad_producto) {
        this.cantidad_producto = cantidad_producto;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public ComboBox<Producto> getCombo_producto_editar_pedido() {
        return combo_producto_editar_pedido;
    }

    public void setCombo_producto_editar_pedido(ComboBox<Producto> combo_producto_editar_pedido) {
        this.combo_producto_editar_pedido = combo_producto_editar_pedido;
    }


    public int getId_detalle_pedido() {
        return id_detalle_pedido;
    }

    public void setId_detalle_pedido(int id_detalle_pedido) {
        this.id_detalle_pedido = id_detalle_pedido;
    }

}
