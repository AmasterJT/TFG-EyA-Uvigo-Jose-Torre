package uvigo.tfgalmacen.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Pedido;
import uvigo.tfgalmacen.database.PaletSalidaDAO;
import uvigo.tfgalmacen.utils.ColorFormatter;
import uvigo.tfgalmacen.utils.PdfUtils;
import uvigo.tfgalmacen.utils.windowComponentAndFuncionalty;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.RutasFicheros.*;
import static uvigo.tfgalmacen.database.PedidoDAO.*;
import static uvigo.tfgalmacen.database.UsuarioDAO.getNombreUsuarioById;
import static uvigo.tfgalmacen.database.UsuarioDAO.getUsernameById;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.crearStageBasico;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.ventana_success;


import java.awt.Desktop;


public class apartadoEnvioController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(apartadoEnvioController.class.getName());

    // Sustituye virtual threads por un pool normal:
    private static final ExecutorService FX_BG_EXEC = Executors.newFixedThreadPool(4);  // por ejemplo

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


    @FXML
    private ImageView pdfImageView;

    @FXML
    private TextField carpeta_destino_text;


    private final List<ItemEnvioController> listaItemsEnvio = new ArrayList<>();

    @FXML
    private Button generar_etiquetas_btn;

    @FXML
    private Button abrir_explorador_btn;

    @FXML
    private GridPane grid_envio;

    @FXML
    private ScrollPane pedidosEnCursoPrimeraHoraScroll; // scroll del envío

    @FXML
    private Button print_etiqueta_btn;

    @FXML
    private Button enviar_pedido;
    // nº de columnas "útiles" para palets (además de la columna 0 con el id_pedido)
    // Realmente no hace falta fijar un máximo, el GridPane crece según uses columnas.
    // Lo dejo como constante por si luego quieres reordenar o cambiar diseño.
    private static final int COLUMNS_START_PALET = 1;

    private ItemEnvioController itemMostrandoEtiqueta;
    private File pdfMostrado;


    private final ObjectProperty<ItemEnvioController> ultimoItemContexto =
            new SimpleObjectProperty<>(null);

    private ContextMenu menuSeleccion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarScrollYGrid();
        cargarPaletsSalidaEnGrid();

        generar_etiquetas_btn.setOnAction(_ -> procesarEtiquetasSeleccionadas());

        enviar_pedido.setOnAction(_ -> abrirVetanaEnviarPedido());

        if (print_etiqueta_btn != null) {
            print_etiqueta_btn.setOnAction(_ -> abrirEtiquetaEnVisor());
            print_etiqueta_btn.setTooltip(new Tooltip("Imprimir etiqueta"));

        }

        if (abrir_explorador_btn != null) {
            abrir_explorador_btn.setOnAction(_ -> abrirExploradorEnEtiqueta());
            abrir_explorador_btn.setTooltip(new Tooltip("Abrir ubicación de la etiqueta"));

            abrir_explorador_btn.setDisable(true);   // deshabilitado al inicio
        }

        if (carpeta_destino_text != null) {
            carpeta_destino_text.setEditable(false); // opcional: solo lectura
            carpeta_destino_text.clear();            // sin ruta al inicio
        }

        configurarContextMenuSeleccion();

    }


    private void configurarContextMenuSeleccion() {
        menuSeleccion = new ContextMenu();

        MenuItem seleccionarTodos = new MenuItem("Seleccionar todos los palets");
        MenuItem seleccionarFila = new MenuItem("Seleccionar palets de este pedido");
        MenuItem deseleccionarTodos = new MenuItem("Deseleccionar todos los palets");
        MenuItem invertirSeleccion = new MenuItem("Invertir selección");
        MenuItem seleccionarSinEtiqueta = new MenuItem("Seleccionar todos los palets sin etiqueta");


        // ✅ Seleccionar TODOS los items del grid
        seleccionarTodos.setOnAction(_ -> {
            for (ItemEnvioController item : listaItemsEnvio) {
                item.setSeleccionado(true);
            }
        });

        // ✅ Seleccionar SOLO los items de la fila (pedido)
        seleccionarFila.setOnAction(_ -> {
            ItemEnvioController base = ultimoItemContexto.get();
            if (base == null) return;

            int idPedidoBase = base.getIdPedido();
            for (ItemEnvioController item : listaItemsEnvio) {
                if (item.getIdPedido() == idPedidoBase) {
                    item.setSeleccionado(true);
                }
            }
        });

        deseleccionarTodos.setOnAction(_ -> {
            for (ItemEnvioController item : listaItemsEnvio) {
                item.setSeleccionado(false);
            }
        });


        invertirSeleccion.setOnAction(_ -> {
            for (ItemEnvioController item : listaItemsEnvio) {
                item.setSeleccionado(!item.estaSeleccionado());
            }
        });


        seleccionarSinEtiqueta.setOnAction(_ -> {
            for (ItemEnvioController item : listaItemsEnvio) {

                if (!item.isTieneEtiqueta()) item.setSeleccionado(true);
            }
        });

        // Añadir todos los items al menú
        menuSeleccion.getItems().addAll(
                seleccionarTodos,
                seleccionarFila,
                deseleccionarTodos,
                invertirSeleccion,
                seleccionarSinEtiqueta
        );
    }

    private void abrirVetanaEnviarPedido() {

        Stage owner = (Stage) enviar_pedido.getScene().getWindow();
        openWindowAsync(owner);
    }

    private void openWindowAsync(Stage owner) {
        // Creamos el loader AQUÍ para poder usarlo luego
        FXMLLoader loader = new FXMLLoader(getClass().getResource(uvigo.tfgalmacen.RutasFicheros.WINDOW_GENERAR_ENVIO_FXML));

        Task<Parent> task = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                // Cargamos el FXML en background
                return loader.load();
            }
        };

        task.setOnSucceeded(_ -> {
            try {
                Parent root = task.getValue();

                // <<< OBTENEMOS EL CONTROLLER DE LA VENTANA HIJA >>>
                windowGenerarPedidoController controllerHijo = loader.getController();
                if (controllerHijo != null) {
                    controllerHijo.setEnvioParent(this);  // pasamos referencia al padre
                }

                Stage win = crearStageBasico(root, true, "Crear nuevo porducto");
                if (owner != null) {
                    win.initOwner(owner);
                    win.initModality(Modality.WINDOW_MODAL);
                    win.initStyle(StageStyle.TRANSPARENT);
                }
                win.showAndWait();
                LOGGER.fine(() -> "Ventana abierta: " + "Crear nuevo porducto");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al inicializar ventana hija: " + "Crear nuevo porducto", e);
            }
        });

        task.setOnFailed(_ -> {
            Throwable ex = task.getException();
            LOGGER.log(Level.SEVERE, "No se pudo abrir la ventana: " + "Crear nuevo porducto", ex);
            ex.printStackTrace();
        });

        FX_BG_EXEC.submit(task);
    }

    private void limpiarGrid(GridPane grid) {
        windowComponentAndFuncionalty.limpiarGridPane(grid);
        limpiarPdf();
    }

    private void configurarScrollYGrid() {
        if (pedidosEnCursoPrimeraHoraScroll != null) {
            pedidosEnCursoPrimeraHoraScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            pedidosEnCursoPrimeraHoraScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            if (grid_envio != null) {
                grid_envio.prefWidthProperty().unbind();
                grid_envio.setMinWidth(Region.USE_COMPUTED_SIZE);
                grid_envio.setPrefWidth(Region.USE_COMPUTED_SIZE);
                grid_envio.setMaxWidth(Region.USE_COMPUTED_SIZE);

                grid_envio.setHgap(10);
                grid_envio.setVgap(10);
            }
        }
    }


    private void procesarEtiquetasSeleccionadas() {
        System.out.println("---- Etiquetas seleccionadas ----");

        boolean alguno = false;

        for (ItemEnvioController item : listaItemsEnvio) {
            if (item.estaSeleccionado()) {
                System.out.println("esta seleccionado → SSCC: " + item.getSscc());
                item.generarEtiqueta();
                alguno = true;


            }
        }

        if (!alguno) {
            System.out.println("Ningún item seleccionado.");
        } else {
            ventana_success("Etiqueta del palet",
                    "Etiquetas generadas correctamente", ""
            );
        }
    }


    private static final double ANCHO_COLUMNA_PEDIDO = 200;
    private static final double ANCHO_ITEM_PALET = 180;
    private static final double ALTO_ITEM_PALET = 205; // ajusta a tu diseño

    private void cargarPaletsSalidaEnGrid() {
        limpiarGrid(grid_envio);
        listaItemsEnvio.clear();

        Connection conn = Main.connection;
        Map<Integer, List<Integer>> mapa =
                PaletSalidaDAO.getPaletsSalidaAgrupadosPorPedido(conn);

        int row = 0;

        for (Map.Entry<Integer, List<Integer>> entry : mapa.entrySet()) {
            int idPedido = entry.getKey();
            List<Integer> paletsSalida = entry.getValue();

            String codigo_referencia_pedido = getCodigoReferenciaById(Main.connection, idPedido);
            boolean esta_completado = isPedidoCompletado(Main.connection, codigo_referencia_pedido);

            Pedido p = getPedidoPorCodigo(Main.connection, codigo_referencia_pedido);

            Label lblPedido = new Label();
            lblPedido.setWrapText(false);
            if (esta_completado) {
                lblPedido.setText("✅ " + codigo_referencia_pedido);
            } else {
                assert p != null;
                lblPedido.setText("⌛ " + codigo_referencia_pedido + "\n " +
                        getNombreUsuarioById(Main.connection, p.getId_usuario()) + " (" +
                        getUsernameById(Main.connection, p.getId_usuario()) + ")");
            }

            // ✅ tamaño fijo para la celda de pedido
            lblPedido.setMinWidth(ANCHO_COLUMNA_PEDIDO);
            lblPedido.setPrefWidth(ANCHO_COLUMNA_PEDIDO);
            lblPedido.setMaxWidth(ANCHO_COLUMNA_PEDIDO);

            // que corte el texto si no cabe (sin wrap)
            lblPedido.setEllipsisString("…");

            lblPedido.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white");

            grid_envio.add(lblPedido, 0, row);
            GridPane.setMargin(lblPedido, new Insets(10, 10, 10, 10));

            int col = 1;
            for (Integer idPaletSalida : paletsSalida) {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource(ITEM_ENVIO_FXML)
                    );
                    AnchorPane itemRoot = loader.load();

                    itemRoot.setMinWidth(ANCHO_ITEM_PALET);
                    itemRoot.setPrefWidth(ANCHO_ITEM_PALET);
                    itemRoot.setMaxWidth(ANCHO_ITEM_PALET);

                    itemRoot.setMinHeight(ALTO_ITEM_PALET);
                    itemRoot.setPrefHeight(ALTO_ITEM_PALET);
                    itemRoot.setMaxHeight(ALTO_ITEM_PALET);

                    ItemEnvioController ctrl = loader.getController();
                    listaItemsEnvio.add(ctrl);

                    ctrl.setData(
                            Objects.requireNonNull(getPedidoPorCodigo(Main.connection, codigo_referencia_pedido)),
                            Objects.requireNonNull(PaletSalidaDAO.getPaletSalidaById(Main.connection, idPaletSalida))
                    );
                    ctrl.setApartadoEnvioParent(this);

                    // 🔹 Asignar el menú contextual a este item
                    itemRoot.setOnContextMenuRequested(evt -> {
                        ultimoItemContexto.set(ctrl);
                        menuSeleccion.show(itemRoot, evt.getScreenX(), evt.getScreenY());
                        evt.consume();
                    });

                    grid_envio.add(itemRoot, col, row);
                    GridPane.setMargin(itemRoot, new Insets(10));
                    col++;

                } catch (Exception e) {
                    System.err.println("Error cargando ItemEnvio: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            row++;
        }
    }

    public void refrescarGridEnvio() {
        limpiarPdf();              // ← al refrescar, quitamos la imagen
        cargarPaletsSalidaEnGrid();
    }

    /**
     * Devuelve true si TODOS los palets_salida del pedido indicado
     * tienen ya su etiqueta generada (tieneEtiqueta == true).
     * Si no hay ningún palet para ese pedido, devuelve false.
     */
    public boolean tieneTodasEtiquetasParaPedido(int idPedido) {
        // Filtramos los items que pertenecen a ese pedido
        List<ItemEnvioController> itemsDePedido = listaItemsEnvio.stream()
                .filter(it -> it.getIdPedido() == idPedido)
                .toList();

        if (itemsDePedido.isEmpty()) {
            // no hay palets_salida para este pedido → no se puede enviar
            System.out.println("No hay palets_salida en el grid para el pedido id=" + idPedido);
            return false;
        }

        // Todos deben tener etiqueta
        for (ItemEnvioController it : itemsDePedido) {
            if (!it.isTieneEtiqueta()) {
                System.out.println("Pedido id=" + idPedido +
                        " aún tiene palets sin etiqueta. Ejemplo SSCC=" + it.getSscc());
                return false;
            }
        }
        return true;
    }


    private ItemEnvioController itemQueMostroPdf;

    public void mostrarPdf(File pdfFile, ItemEnvioController origen) {
        try {
            Image img = PdfUtils.cargarPrimeraPaginaComoImagen(pdfFile);
            pdfImageView.setImage(img);
            pdfImageView.setPreserveRatio(true);
            pdfImageView.setFitWidth(600);

            // Guardamos el pdf actual y el item que lo originó
            this.pdfMostrado = pdfFile;
            this.itemQueMostroPdf = origen;

            // Habilitar botón de explorador
            if (abrir_explorador_btn != null) {
                abrir_explorador_btn.setDisable(false);
            }

            // Mostrar ruta completa en el textfield
            if (carpeta_destino_text != null) {
                carpeta_destino_text.setText(pdfFile.getAbsolutePath());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void limpiarPdfSiMostradoDesde(ItemEnvioController item) {
        if (itemQueMostroPdf == item) {
            limpiarPdf();
        }
    }

    public void mostrarPdf(File pdfFile) {
        mostrarPdf(pdfFile, null);
    }

    public void limpiarPdf() {
        pdfImageView.setImage(null);
        itemMostrandoEtiqueta = null;
        pdfMostrado = null;
    }


    private void abrirEtiquetaEnVisor() {
        // 1) Comprobar si hay PDF cargado
        if (pdfMostrado == null || !pdfMostrado.exists()) {
            // No hay etiqueta actual → aviso
            windowComponentAndFuncionalty.ventana_warning(
                    "Sin etiqueta",
                    "No hay ninguna etiqueta seleccionada.",
                    "Haz clic en un palet con etiqueta generada para poder abrirla."
            );
            return;
        }

        // 2) Comprobar soporte Desktop
        if (!Desktop.isDesktopSupported()) {
            windowComponentAndFuncionalty.ventana_warning(
                    "Función no soportada",
                    "Tu sistema no soporta apertura directa de archivos.",
                    "No se puede abrir el visor PDF por defecto automáticamente."
            );
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            windowComponentAndFuncionalty.ventana_warning(
                    "Función no soportada",
                    "La acción de abrir archivos no está soportada.",
                    "No se puede abrir el visor PDF por defecto automáticamente."
            );
            return;
        }

        // 3) Intentar abrir el PDF
        try {
            desktop.open(pdfMostrado);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error abriendo PDF en visor por defecto: " + pdfMostrado, e);
            windowComponentAndFuncionalty.ventana_warning(
                    "Error al abrir etiqueta",
                    "No se ha podido abrir la etiqueta.",
                    "Comprueba que el archivo sigue existiendo:\n" + pdfMostrado.getAbsolutePath()
            );
        }
    }

    private void abrirExploradorEnEtiqueta() {
        if (pdfMostrado == null || !pdfMostrado.exists()) {
            return;
        }

        try {
            String os = System.getProperty("os.name").toLowerCase();
            String absolutePath = pdfMostrado.getAbsolutePath();

            if (os.contains("win")) {
                // Windows: abrir el explorador con el archivo seleccionado
                // explorer.exe /select,"C:\ruta\al\archivo.pdf"
                String comando = String.format("explorer.exe /select,\"%s\"",
                        absolutePath.replace("/", "\\"));
                new ProcessBuilder("cmd", "/c", comando).start();

            } else if (os.contains("mac")) {
                // 🍏 macOS: abrir Finder resaltando el archivo
                new ProcessBuilder("open", "-R", absolutePath).start();

            } else {
                // 🐧 Linux / otros: mejor esfuerzo → abrir carpeta contenedora
                File parent = pdfMostrado.getParentFile();
                if (parent != null && parent.exists()) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(parent);
                    } else {
                        System.out.println("Desktop no soportado en este sistema.");
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error abriendo explorador para: " + pdfMostrado, e);
        }
    }


}
