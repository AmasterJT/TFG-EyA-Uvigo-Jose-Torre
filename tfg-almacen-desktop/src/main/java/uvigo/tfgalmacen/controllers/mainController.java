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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
    private Button cerrarSesionBtn;
    @FXML
    private Button esconder_ajustes_btn;
    @FXML
    private Button inventarioButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button orden_compra_btn;
    @FXML
    private Button pedidosButton;
    @FXML
    private Button paletizarButton;
    @FXML
    private Label roleLabel;
    @FXML
    private AnchorPane root;
    @FXML
    private AnchorPane slider;

    @FXML
    private Button ajustes_crear_producto_btn;

    @FXML
    private Button ajustes_crear_tipo_btn;

    @FXML
    private Button actualizar_palet_btn;

    @FXML
    private Button movimiento_btn;

    @FXML
    private Button export_data_btn;

    @FXML
    private Button envioButton;

    @FXML
    private Button calendarioButton;

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
        pedidosButton.setTooltip(new Tooltip("Gestion de pedidos"));

        paletizarButton.setOnMouseClicked(_ -> loadPaletizarView());
        paletizarButton.setTooltip(new Tooltip("Crear palets"));

        envioButton.setOnMouseClicked(_ -> loadVentanaEnvioView());
        envioButton.setTooltip(new Tooltip("Gestionar envio de palets"));

        calendarioButton.setOnMouseClicked(_ -> loadVentanaCalendarioView());
        calendarioButton.setTooltip(new Tooltip("ver calendario de pedidos"));

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

        ajustes_crear_producto_btn.setOnMouseClicked(_ -> abrirVentanaCrearProducto());
        ajustes_crear_producto_btn.setTooltip(new Tooltip("Crear nuevo producto"));

        ajustes_crear_tipo_btn.setOnMouseClicked(_ -> abrirVentanaCrearTipo());
        ajustes_crear_tipo_btn.setTooltip(new Tooltip("Crear nuevo tipo"));


        actualizar_palet_btn.setOnMouseClicked(_ -> {
            try {
                abrirVentanaActualizarPalet();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        actualizar_palet_btn.setTooltip(new Tooltip("Modificar los datos del palet"));


        export_data_btn.setOnMouseClicked(_ -> {
            try {
                abrirVentanaExportData();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        export_data_btn.setTooltip(new Tooltip("Exportar datos"));

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

        // Permitir que el root capture teclas
        Platform.runLater(() -> root.requestFocus());

        // Evento de teclado (ESC para cerrar el slider)
        root.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                if (!sliderVisible) {
                    slideMenu(false);
                }
            }
        });


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
            LOGGER.log(Level.SEVERE, "Error cargando FXML: " + path, ex);
            ex.printStackTrace();
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

    private void openWindowAsyncCallback(String fxmlPath, String title, Stage owner, Runnable afterClose) {
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

            // Cuando se cierre la ventana, ejecutamos el callback
            win.setOnHidden(_ -> {
                if (afterClose != null) {
                    afterClose.run();
                }
            });

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

    private void abrirVentanaCrearTipo() {
        Stage owner = (Stage) ajustes_crear_usuario_btn.getScene().getWindow();
        openWindowAsyncCallback(WINDOW_AJUSTES_CREAR_TIPO_FXML, "Crear nuevo tipo", owner, this::loadAlmacenView);
    }

    private void abrirVentanaCrearProducto() {
        Stage owner = (Stage) ajustes_crear_usuario_btn.getScene().getWindow();
        openWindowAsync(WINDOW_AJUSTES_CREAR_PRODUCTO_FXML, "Crear nuevo porducto", owner);
    }

    private void abrirVentanaExportData() throws SQLException {

        Stage owner = (Stage) ajustes_crear_usuario_btn.getScene().getWindow();
        openWindowAsync(WINDOW_EXPORTAR_DATA_FXML, "Exportar datos", owner);
    }

    private void abrirVentanaActualizarPalet() throws SQLException {

        Stage owner = (Stage) ajustes_crear_usuario_btn.getScene().getWindow();
        openWindowAsyncCallback(WINDOW_ACTUALIZAR_PALET_FXML, "Exportar datos", owner, this::loadAlmacenView);
    }

    private void abrirVentanaMovimiento() {
        Stage owner = (Stage) ajustes_crear_usuario_btn.getScene().getWindow();
        openWindowAsyncCallback(WINDOW_MOVIMIENTO_FXML, "Actualizar Palet", owner, this::loadAlmacenView);   // callback que se ejecuta al cerrar);
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
    private void loadPaletizarView() {
        loadFXMLAsync(APARTADO_PALETIZAR_FXML);
        marcarBotonActivo(paletizarButton);
    }

    @FXML
    private void loadVentanaEnvioView() {
        loadFXMLAsync(APARTADO_ENVIO_FXML);
        marcarBotonActivo(envioButton);
    }

    @FXML
    private void loadVentanaCalendarioView() {
        loadFXMLAsync(APARTADO_CALENDARIO_FXML);
        marcarBotonActivo(calendarioButton);
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
        File folder = new File("export/");

        if (!folder.exists() || !folder.isDirectory()) {
            LOGGER.warning("Directorio no encontrado: " + folder.getAbsolutePath());
            return;
        }

        File[] xmlFiles = folder.listFiles((_, name) -> name.toLowerCase().endsWith(".xml"));

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
        var nav = java.util.List.of(almacenButton, inventarioButton, pedidosButton, paletizarButton, envioButton, calendarioButton);

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

    public Button getActiveScene() {
        return activeScene;
    }

    public void setActiveScene(Button activeScene) {
        this.activeScene = activeScene;
    }
}
