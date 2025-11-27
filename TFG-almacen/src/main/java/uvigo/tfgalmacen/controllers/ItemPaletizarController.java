package uvigo.tfgalmacen.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.ProductoPedido;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.database.DetallesPedidoDAO.actualizarEstadoProductoPedido;
import static uvigo.tfgalmacen.database.ProductoDAO.getIdProductoByIdentificadorProducto;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.crearStageBasico;

public class ItemPaletizarController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(ItemPaletizarController.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new ColorFormatter());
        LOGGER.addHandler(consoleHandler);

        // Opcional: ajustar también el root logger (evita silencios en handlers)
        for (Handler h : Logger.getLogger("").getHandlers()) {
            h.setLevel(Level.ALL);
        }
    }

    private static final ExecutorService FX_BG_EXEC = Executors.newVirtualThreadPerTaskExecutor();


    private paletizarController paletizarParent;

    boolean esta_en_palet = false;
    int id_detalle_BDD;
    int idProductoBDD;   // id_producto (FK productos.id_producto)

    @FXML
    private Label cantidad_detalle_pedido_label;

    @FXML
    private Label producto_detalle_pedido_label;


    @FXML
    private Button mover_producto_listo_en_pedido_btn;

    @FXML
    private Button split_btn;

    @FXML
    private Circle tipo_producto_color_ldentifier;

    @FXML
    private VBox vertical_container;

    @FXML
    private Region region;


    @FXML
    private AnchorPane base_pane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        split_btn.setOnAction(_ -> modificarCantidadProducto());
        split_btn.setTooltip(new Tooltip("Dividir cantidad de producto"));

        if (mover_producto_listo_en_pedido_btn != null) {
            mover_producto_listo_en_pedido_btn.setOnAction(_ -> ocultar());
            mover_producto_listo_en_pedido_btn.setTooltip(new Tooltip("Mover este producto al palet"));
        }


        configurarContextMenu();
    }

    public void ocultar() {
        if (paletizarParent != null) {
            paletizarParent.moverItemAProductosEnPalet(this);

            actualizarEstadoProductoPedido(Main.connection, id_detalle_BDD, true);

        } else {
            LOGGER.warning("paletizarParent es null en ItemPaletizarController al intentar mover el item.");
        }
    }

    private void modificarCantidadProducto() {

        abrirVentanaMovimiento();
    }

    public void setData(ProductoPedido productoDelPedido) {
        producto_detalle_pedido_label.setText(productoDelPedido.getIdentificadorProducto());
        cantidad_detalle_pedido_label.setText(String.valueOf(productoDelPedido.getCantidad()));

        this.id_detalle_BDD = productoDelPedido.getId_detalle_BDD();


        idProductoBDD = getIdProductoByIdentificadorProducto(Main.connection, productoDelPedido.getIdentificadorProducto());
        tipo_producto_color_ldentifier.setFill(Paint.valueOf(productoDelPedido.colorHEX));
    }

    public String getCantidad() {
        return cantidad_detalle_pedido_label.getText();
    }

    public String getProductoId() {
        return producto_detalle_pedido_label.getText();
    }

    private void abrirVentanaMovimiento() {
        Stage owner = (Stage) split_btn.getScene().getWindow();
        openWindowAsync(owner);   // callback que se ejecuta al cerrar);
    }


    private void openWindowAsync(Stage owner) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(uvigo.tfgalmacen.RutasFicheros.WINDOW_MODIFICAR_CANTIDAD_FXML));

        Task<Parent> task = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                return loader.load();
            }
        };

        task.setOnSucceeded(_ -> {
            try {
                Parent root = task.getValue();

                modificarCantidadPaletizarController controller = loader.getController();
                controller.setData(cantidad_detalle_pedido_label.getText(), id_detalle_BDD);

                Stage win = crearStageBasico(root, true, "Crear nuevo Proveedor");
                if (owner != null) {
                    win.initOwner(owner);
                    win.initModality(Modality.WINDOW_MODAL);
                    win.initStyle(StageStyle.TRANSPARENT);
                }

                // 🔹 Cuando se cierre esta ventana, refrescamos el grid de origen
                win.setOnHidden(_ -> {
                    if (paletizarParent != null) {
                        paletizarParent.refrescarGridDeOrigenTrasSplit(ItemPaletizarController.this);
                    } else {
                        LOGGER.warning("paletizarParent es null al intentar refrescar tras split.");
                    }
                });

                win.showAndWait();
                LOGGER.fine(() -> "Ventana abierta: " + "Crear nuevo Proveedor");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al inicializar la ventana: " + "Crear nuevo Proveedor", e);
            }
        });

        task.setOnFailed(_ -> {
            Throwable ex = task.getException();
            LOGGER.log(Level.SEVERE, "No se pudo abrir la ventana: " + "Crear nuevo Proveedor", ex);
        });

        FX_BG_EXEC.submit(task);
    }


    public void setPaletizarController(paletizarController parent) {
        this.paletizarParent = parent;
    }

    public void mostrarComoProductoEnPalet(boolean enPalet) {
        this.esta_en_palet = enPalet;

        boolean visibleAcciones = !enPalet;

        setNodeVisible(split_btn, visibleAcciones);
        setNodeVisible(mover_producto_listo_en_pedido_btn, visibleAcciones);
        setNodeVisible(region, visibleAcciones);

        // Ajustar el contenedor base para que se recalculen las medidas
        if (base_pane != null) {
            base_pane.setPrefHeight(Region.USE_COMPUTED_SIZE);
            base_pane.setMinHeight(Region.USE_PREF_SIZE);
            base_pane.setMaxHeight(Region.USE_PREF_SIZE);

            base_pane.setPrefWidth(Region.USE_COMPUTED_SIZE);
            base_pane.setMinWidth(Region.USE_PREF_SIZE);
            base_pane.setMaxWidth(Region.USE_PREF_SIZE);

            base_pane.requestLayout();  // pide un nuevo layout

            System.out.println(base_pane.getWidth());
        }
    }

    private void setNodeVisible(Node node, boolean visible) {
        if (node != null) {
            node.setVisible(visible);
            node.setManaged(visible); // esto es lo que hace que el layout lo tenga o no en cuenta
        }
    }

    private void configurarContextMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem quitarDePalet = new MenuItem("Quitar de palet");

        quitarDePalet.setOnAction(_ -> {
            if (paletizarParent != null && esta_en_palet) {
                paletizarParent.devolverItemAOrigen(this);
                actualizarEstadoProductoPedido(Main.connection, id_detalle_BDD, false);
            } else {
                LOGGER.fine("ContextMenu: 'Quitar de palet' ignorado (no está en palet o parent null).");
            }
        });

        menu.getItems().add(quitarDePalet);

        // Mostrar menú al hacer clic derecho sobre todo el item
        vertical_container.setOnContextMenuRequested(e ->
                menu.show(vertical_container, e.getScreenX(), e.getScreenY())
        );
    }


    public int getCantidadInt() {
        try {
            return Integer.parseInt(cantidad_detalle_pedido_label.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getProductoIdTexto() {
        return producto_detalle_pedido_label.getText();
    }

    public int getIdDetalleBDD() {
        return id_detalle_BDD;
    }

    public int getIdProductoBDD() {
        return idProductoBDD;
    }

    public Button getMover_producto_listo_en_pedido_btn() {
        return mover_producto_listo_en_pedido_btn;
    }

    public void setMover_producto_listo_en_pedido_btn(Button mover_producto_listo_en_pedido_btn) {
        this.mover_producto_listo_en_pedido_btn = mover_producto_listo_en_pedido_btn;
    }


}
