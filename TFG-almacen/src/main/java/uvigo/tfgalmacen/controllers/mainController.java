package uvigo.tfgalmacen.controllers;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.RutasFicheros.*;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.crearStageBasico;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.ventana_confirmacion;

public class mainController implements Initializable {

    // ======================= Logger =======================
    private static final Logger LOGGER = Logger.getLogger(mainController.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new ColorFormatter());
        LOGGER.addHandler(ch);

        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            h.setLevel(Level.ALL);
        }
    }

    // ======================= Carga en segundo plano =======================
    // Java 21+: hilos virtuales. Si usas una versión anterior, reemplaza por newFixedThreadPool(4)
    private static final ExecutorService FX_BG_EXEC = Executors.newVirtualThreadPerTaskExecutor();

    // ======================= FXML =======================
    @FXML
    private BorderPane BorderPane;
    @FXML
    private Button ExitButton;
    @FXML
    private Button MenuButton;
    @FXML
    private Button ajustesButton;
    @FXML
    private Button ajustes_crear_pedido_btn;
    @FXML
    private Button ajustes_crear_usuario_btn;
    @FXML
    private Button ajustes_editar_pedido_btn;
    @FXML
    private Button ajustes_editar_usuario_btn;
    @FXML
    private Button ajustes_eliminar_pedido_btn;
    @FXML
    private Button ajustes_eliminar_usuario_btn;
    @FXML
    private Button almacenButton;
    @FXML
    private AnchorPane almacenContainer;
    @FXML
    private Button cerrarSesionBtn;
    @FXML
    private Button esconder_ajustes_btn;
    @FXML
    private Label estadoConexionLabel;
    @FXML
    private Button inventarioButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button orden_compra_btn;
    @FXML
    private Button pedidosButton;
    @FXML
    private Button probarConexionBtn;
    @FXML
    private Button recepcionButton;
    @FXML
    private Label roleLabel;
    @FXML
    private AnchorPane root;
    @FXML
    private AnchorPane slider;
    @FXML
    private HBox toolBar;
    @FXML
    private AnchorPane topBar;
    @FXML
    private Label usuarioActualLabel;
    @FXML
    private HBox windowBar;

    @FXML
    private Button movimiento_btn;

    // ======================= Estado =======================
    private Button activeScene = null;
    private boolean sliderVisible = true;

    // ======================= Ciclo de vida =======================
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Oculta el slider de inicio
        slideMenu(false);

        ExitButton.setOnMouseClicked(_ -> Platform.exit());
        minimizeButton.setOnAction(_ -> minimizarVentana());

        almacenButton.setOnMouseClicked(_ -> loadAlmacenView());
        almacenButton.setTooltip(new Tooltip("Vista 3D del almacén"));

        inventarioButton.setOnMouseClicked(_ -> loadInventarioView());
        inventarioButton.setTooltip(new Tooltip("Vista del inventario de palets"));

        pedidosButton.setOnMouseClicked(_ -> loadPedidosView());
        pedidosButton.setTooltip(new Tooltip("Gestion de productos"));

        recepcionButton.setOnMouseClicked(_ -> loadRecepcionView());
        recepcionButton.setTooltip(new Tooltip("Recepciones previstas"));

        MenuButton.setOnMouseClicked(_ -> loadMenu());
        MenuButton.setTooltip(new Tooltip("Abrir menu"));

        orden_compra_btn.setOnMouseClicked(_ -> abrirVentanaOrdenCompra());
        orden_compra_btn.setTooltip(new Tooltip("Crear una orden de compra"));

        ajustes_crear_usuario_btn.setOnMouseClicked(_ -> abrirVentanaCrearUsuario());
        ajustes_crear_usuario_btn.setTooltip(new Tooltip("Crear un nuevo usuario"));

        ajustes_editar_usuario_btn.setOnMouseClicked(_ -> abrirVentanaEditarUsuario());
        ajustes_editar_usuario_btn.setTooltip(new Tooltip("Editar un usuario existente"));

        ajustes_eliminar_usuario_btn.setOnMouseClicked(_ -> abrirVentanaEliminarUsuario());
        ajustes_eliminar_usuario_btn.setTooltip(new Tooltip("Eliminar un usuario existente"));

        ajustes_editar_pedido_btn.setOnMouseClicked(_ -> abrirVentanaEditarPedido());
        ajustes_editar_pedido_btn.setTooltip(new Tooltip("Editar un pedido existente"));


        ajustes_crear_pedido_btn.setOnMouseClicked(_ -> abrirVentanaCrearPedido());
        ajustes_crear_pedido_btn.setTooltip(new Tooltip("Crear un nuevo pedido"));


        ajustes_eliminar_pedido_btn.setOnMouseClicked(_ -> abrirVentanaEliminarPedido());
        ajustes_eliminar_pedido_btn.setTooltip(new Tooltip("Eliminar pedidos"));


        movimiento_btn.setOnMouseClicked(_ -> abrirVentanaMovimiento());
        movimiento_btn.setTooltip(new Tooltip("Mover Palet"));

        esconder_ajustes_btn.setOnMouseClicked(_ -> slideMenu(false));

        if (cerrarSesionBtn != null) {
            cerrarSesionBtn.setOnAction(_ -> cerrarSesion());
        }

        // Por defecto carga la vista de almacen (asíncrono)
        loadAlmacenView();

        if (Main.currentUser != null) {
            roleLabel.setText(Main.currentUser.getRole());
        } else {
            roleLabel.setText("NO ROL");
        }


    }

    // ======================= Utilidades asíncronas =======================

    /**
     * Carga un FXML en background y lo sitúa en el centro del BorderPane.
     */
    private void loadFXMLAsync(String path) {
        Task<Parent> task = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
                return loader.load();
            }
        };

        task.setOnSucceeded(_ -> {
            Parent parent = task.getValue();
            BorderPane.setCenter(parent);
            LOGGER.fine(() -> "Cargado FXML async: " + path);
        });

        task.setOnFailed(_ -> {
            Throwable ex = task.getException();
            LOGGER.log(Level.SEVERE, "❌ Error cargando FXML: " + path, ex);
        });

        FX_BG_EXEC.submit(task);
    }

    /**
     * Abre una ventana modal cargando el FXML en background.
     */
    private void openWindowAsync(String fxmlPath, String title, Stage owner) {
        Task<Parent> task = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                return loader.load();
            }
        };

        task.setOnSucceeded(_ -> {
            Parent root = task.getValue();
            Stage win = crearStageBasico(root, true, title);
            if (owner != null) {
                win.initOwner(owner);
                win.initModality(Modality.WINDOW_MODAL);
                win.initStyle(StageStyle.TRANSPARENT);
            }
            win.showAndWait();
            LOGGER.fine(() -> "Ventana abierta: " + title);
        });

        task.setOnFailed(_ -> {
            Throwable ex = task.getException();
            LOGGER.log(Level.SEVERE, "No se pudo abrir la ventana: " + title, ex);
            ex.printStackTrace();
        });

        FX_BG_EXEC.submit(task);
    }


    // ======================= Acciones UI =======================

    private void minimizarVentana() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    private void abrirVentanaOrdenCompra() {
        Stage owner = (Stage) orden_compra_btn.getScene().getWindow();
        openWindowAsync(WINDOW_ORDEN_COMPRA_FXML, "Orden de compra", owner);
    }

    private void abrirVentanaCrearUsuario() {
        Stage owner = (Stage) ajustes_crear_usuario_btn.getScene().getWindow();
        openWindowAsync(WINDOW_AJUSTES_CREAR_USUARIOS_FXML, "Crear Usuario", owner);
    }

    private void abrirVentanaEditarUsuario() {
        Stage owner = (Stage) ajustes_crear_usuario_btn.getScene().getWindow();
        openWindowAsync(WINDOW_AJUSTES_EDITAR_USUARIOS_FXML, "Editar Usuario", owner);
    }

    private void abrirVentanaEliminarUsuario() {
        Stage owner = (Stage) ajustes_crear_usuario_btn.getScene().getWindow();
        openWindowAsync(WINDOW_AJUSTES_ELIMINAR_USUARIOS_FXML, "Eliminar Usuario", owner);
    }

    private void abrirVentanaCrearPedido() {
        Stage owner = (Stage) ajustes_crear_usuario_btn.getScene().getWindow();
        openWindowAsync(WINDOW_AJUSTES_CREAR_PEDIDOS_FXML, "Crear Pedido", owner);
    }

    private void abrirVentanaEditarPedido() {
        Stage owner = (Stage) ajustes_crear_usuario_btn.getScene().getWindow();
        openWindowAsync(WINDOW_AJUSTES_EDITAR_PEDIDOS_FXML, "Editar Pedido", owner);
    }

    private void abrirVentanaEliminarPedido() {
        Stage owner = (Stage) ajustes_crear_usuario_btn.getScene().getWindow();
        openWindowAsync(WINDOW_AJUSTES_ELIMINAR_PEDIDOS_FXML, "Eliminar Pedido", owner);
    }

    private void abrirVentanaMovimiento() {
        Stage owner = (Stage) ajustes_crear_usuario_btn.getScene().getWindow();
        openWindowAsync(WINDOW_MOVIMIENTO_FXML, "Movimiento", owner);
    }


    // ---------------------- Navegación secciones ----------------------
    @FXML
    private void loadAlmacenView() {
        loadFXMLAsync(APARTADO_ALMACEN_FXML);
        marcarBotonActivo(almacenButton);
    }

    @FXML
    private void loadInventarioView() {
        loadFXMLAsync(APARTADO_INVENTARIO_FXML);
        marcarBotonActivo(inventarioButton);
    }

    @FXML
    private void loadPedidosView() {
        loadFXMLAsync(APARTADO_PEDIDOS_FXML);
        marcarBotonActivo(pedidosButton);
    }

    @FXML
    private void loadRecepcionView() {
        loadFXMLAsync(APARTADO_RECEPCION_FXML);
        marcarBotonActivo(recepcionButton);
    }

    @FXML
    private void loadMenu() {
        slideMenu(sliderVisible);
        marcarBotonActivo(MenuButton);
    }

    // ======================= Otros =======================
    @FXML
    private void openXmlInExcel() {
        String excelPath = "\"C:\\\\Program Files (x86)\\\\Microsoft Office\\\\root\\\\Office16\\\\EXCEL.EXE\"";
        File folder = new File("output_files/");

        if (!folder.exists() || !folder.isDirectory()) {
            LOGGER.warning("Directorio no encontrado: " + folder.getAbsolutePath());
            return;
        }

        File[] xmlFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));

        if (xmlFiles == null || xmlFiles.length == 0) {
            LOGGER.info("No se encontraron archivos XML en: " + folder.getAbsolutePath());
            return;
        }

        try {
            for (File xmlFile : xmlFiles) {
                String command = excelPath + " \"" + xmlFile.getAbsolutePath() + "\"";
                Runtime.getRuntime().exec(command);
                LOGGER.fine("Abriendo en Excel: " + xmlFile.getName());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error abriendo XML en Excel", e);
        }
    }

    /**
     * Muestra/Oculta el slider lateral con animación.
     */
    private void slideMenu(boolean show) {
        // Asegura medidas reales en el primer uso
        double anchoSlider = slider.getWidth();
        if (anchoSlider <= 1) {
            slider.applyCss();
            slider.layout();
            anchoSlider = slider.getWidth();
        }

        TranslateTransition slide = new TranslateTransition(Duration.millis(400), slider);
        if (show) {
            slide.setToX(anchoSlider + 10);
            sliderVisible = false;
            LOGGER.log(Level.INFO, "➡️ Abriendo slider");
        } else {
            slide.setToX(-anchoSlider - 10);
            sliderVisible = true;
            LOGGER.log(Level.INFO, "⬅️ Cerrando slider");
        }
        slide.play();
    }

    /**
     * Marca botón activo con colores de tu mapa de colores cargado en Main.colors
     */
    private void marcarBotonActivo(Button botonSeleccionado) {
        var nav = java.util.List.of(almacenButton, inventarioButton, pedidosButton, recepcionButton);

        // quita "active" de todos
        nav.forEach(b -> b.getStyleClass().remove("active"));

        // añade "active" al seleccionado
        if (!botonSeleccionado.getStyleClass().contains("active")) {
            botonSeleccionado.getStyleClass().add("active");
        }
    }

    /**
     * Cierra la sesión del usuario actual y vuelve a la ventana de login.
     */
    private void cerrarSesion() {

        if (!ventana_confirmacion("Confirmar cierre de sesión", "¿Seguro que deseas cerrar tu sesión?")) {
            return;
        }

        try {
            LOGGER.info("Cerrando sesión del usuario actual...");
            Main.currentUser = null;

            Stage ventanaPrincipal = (Stage) cerrarSesionBtn.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(WINDOW_LOGIN_FXML));
            Parent root = loader.load();

            Stage loginStage = crearStageBasico(root, true, "Inicio de sesión");
            loginStage.initStyle(StageStyle.TRANSPARENT);

            loginStage.show();
            ventanaPrincipal.close();

            LOGGER.fine("Sesión cerrada correctamente, regresando a ventana de login.");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "❌ Error al intentar cerrar sesión y volver al login.", e);
        }
    }
}
