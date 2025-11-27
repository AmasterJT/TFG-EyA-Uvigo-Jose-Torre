package uvigo.tfgalmacen.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import uvigo.tfgalmacen.almacenManagement.Palet;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.Producto;
import uvigo.tfgalmacen.utils.ColorFormatter;
import uvigo.tfgalmacen.utils.windowComponentAndFuncionalty;


import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static uvigo.tfgalmacen.RutasFicheros.ITEM_INVENTARIO_FXML;
import static uvigo.tfgalmacen.almacenManagement.Almacen.*;

/**
 * Controlador para la vista de inventario.
 * Gestiona la carga, visualización y filtrado de los palets disponibles en el almacén
 * mediante un GridPane y ComboBoxes de filtros.
 */
public class apartadoInventarioController implements Initializable {

    // ---------------------- Logger ----------------------
    private static final Logger LOGGER = Logger.getLogger(apartadoInventarioController.class.getName());

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

    // ---------------------- Constantes UI ----------------------
    private static final String OPC_TODOS = "Todos";
    private static final List<String> OPC_DELANTE = List.of(OPC_TODOS, "Delante", "Detrás");

    private static final int COLUMNS = 7;
    private static final int ROWS = 7;
    private static final int ITEMS_PER_PAGE = COLUMNS * ROWS;

    // Rango fijo de estanterías/baldas/posiciones
    private static final List<String> BALDAS = IntStream.rangeClosed(1, NUM_BALDAS_PER_ESTANTERIA).mapToObj(String::valueOf).toList();
    private static final List<String> POSICIONES = IntStream.rangeClosed(1, POSICIONES_PER_BALDA).mapToObj(String::valueOf).toList();

    private static final String PLACEHOLDER_PALET = "Seleccionar palet";


    // ---------------------- Estado ----------------------
    private List<Palet> filteredPalets = new ArrayList<>();
    private int currentPage = 0;
    private int totalPages = 1;

    // ---------------------- FXML ----------------------
    @FXML
    private ComboBox<String> estanteriaComboBox;
    @FXML
    private ComboBox<String> baldaComboBox;
    @FXML
    private ComboBox<String> posicionComboBox;
    @FXML
    private ComboBox<String> productoComboBox;
    @FXML
    private ComboBox<String> tipoComboBox;
    @FXML
    private ComboBox<String> delanteComboBox;

    @FXML
    private GridPane grid;
    @FXML
    private ScrollPane scroll;

    @FXML
    private Button buscarButton;
    @FXML
    private Button inventory_reset_button;
    @FXML
    private Button siguienteButton;
    @FXML
    private Button anteriorButton;

    @FXML
    private Label current_page_label;

    @FXML
    private ComboBox<Palet> combo_seleccionar_palet;


    // ---------------------- Ciclo de vida ----------------------
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarScrollYGrid();
        inicializarComboBoxes();
        configurarEventosComboBoxes();
        inicializarBotones();

        // Estado inicial: sin filtros → todos los palets
        aplicarFiltros();


        combo_seleccionar_palet.valueProperty().addListener((_, _, newV) -> {
            boolean hayPaletSeleccionado = (newV != null);

            // Deshabilitar / habilitar el resto de combos
            estanteriaComboBox.setDisable(hayPaletSeleccionado);
            baldaComboBox.setDisable(hayPaletSeleccionado);
            posicionComboBox.setDisable(hayPaletSeleccionado);
            tipoComboBox.setDisable(hayPaletSeleccionado);
            productoComboBox.setDisable(hayPaletSeleccionado);
            delanteComboBox.setDisable(hayPaletSeleccionado);

            // Aplicar filtros automáticamente siempre que cambie la selección
            // aplicarFiltros();
        });
    }


    // ---------------------- Setup UI ----------------------
    private void configurarScrollYGrid() {
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        grid.prefWidthProperty().bind(scroll.widthProperty());
    }

    private void inicializarComboBoxes() {

        // --- ESTANTERÍA ---
        estanteriaComboBox.setItems(
                FXCollections.observableArrayList(
                        Stream.concat(
                                Stream.of(OPC_TODOS),
                                IntStream.rangeClosed(1, NUM_ESTANTERIAS).mapToObj(String::valueOf)
                        ).toList()
                )
        );
        estanteriaComboBox.getSelectionModel().selectFirst();

        // --- BALDA ---
        baldaComboBox.setItems(
                FXCollections.observableArrayList(OPC_TODOS)
        );
        baldaComboBox.getItems().addAll(BALDAS);
        baldaComboBox.getSelectionModel().selectFirst();

        // --- POSICIÓN ---
        posicionComboBox.setItems(
                FXCollections.observableArrayList(OPC_TODOS)
        );
        posicionComboBox.getItems().addAll(POSICIONES);
        posicionComboBox.getSelectionModel().selectFirst();

        // --- TIPO ---
        tipoComboBox.setItems(FXCollections.observableArrayList(OPC_TODOS));
        tipoComboBox.getItems().addAll(
                Almacen.TodosProductos.stream()
                        .map(p -> p.getTipo().getIdTipo())
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
        tipoComboBox.getSelectionModel().selectFirst();

        // --- PRODUCTO ---
        productoComboBox.setItems(FXCollections.observableArrayList(OPC_TODOS));
        productoComboBox.getItems().addAll(
                Almacen.TodosProductos.stream()
                        .map(Producto::getIdentificadorProducto)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
        productoComboBox.getSelectionModel().selectFirst();

        // --- DELANTE ---
        delanteComboBox.setItems(FXCollections.observableArrayList(OPC_DELANTE));
        delanteComboBox.getSelectionModel().selectFirst();

        // --- PALET (mantener lógica actual) ---
        inicializarComboSeleccionarPalet();
    }

    private void inicializarComboSeleccionarPalet() {


        // ===== Palets: mantener ComboBox<Palet> =====
        combo_seleccionar_palet.setEditable(true);
        combo_seleccionar_palet.getItems().clear();

        if (TodosPalets == null || TodosPalets.isEmpty()) {
            LOGGER.warning("No hay palets cargados.");
            combo_seleccionar_palet.setPromptText(PLACEHOLDER_PALET);
        } else {
            // Ordenar por id
            var ordenados = TodosPalets.stream()
                    .sorted(Comparator.comparingInt(Palet::getIdPalet))
                    .toList();

            var base = FXCollections.observableArrayList(ordenados);
            var filtered = new javafx.collections.transformation.FilteredList<>(base, _ -> true);
            combo_seleccionar_palet.setItems(filtered);

            // Mostrar solo el número en celdas y botón
            combo_seleccionar_palet.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Palet p) {
                    return (p == null) ? "" : String.valueOf(p.getIdPalet());
                }

                @Override
                public Palet fromString(String s) {
                    try {
                        int id = Integer.parseInt(s.trim());
                        return base.stream().filter(pp -> pp.getIdPalet() == id).findFirst().orElse(null);
                    } catch (Exception e) {
                        return null;
                    }
                }
            });
            combo_seleccionar_palet.setCellFactory(_ -> new ListCell<>() {
                @Override
                protected void updateItem(Palet p, boolean empty) {
                    super.updateItem(p, empty);
                    setText(empty || p == null ? null : String.valueOf(p.getIdPalet()));
                }
            });
            combo_seleccionar_palet.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Palet p, boolean empty) {
                    super.updateItem(p, empty);
                    setText(empty || p == null ? "" : String.valueOf(p.getIdPalet()));
                }
            });

            // Filtrado en vivo por lo tecleado
            combo_seleccionar_palet.getEditor().textProperty().addListener((_, _, typed) -> {
                String term = typed == null ? "" : typed.trim();
                // Evita que al abrir el popup borre la lista si hay seleccionado
                var sel = combo_seleccionar_palet.getSelectionModel().getSelectedItem();
                if (sel != null && String.valueOf(sel.getIdPalet()).equals(term)) {
                    filtered.setPredicate(_ -> true);
                    return;
                }
                filtered.setPredicate(p -> term.isEmpty() || String.valueOf(p.getIdPalet()).contains(term));
                if (!combo_seleccionar_palet.isShowing()) combo_seleccionar_palet.show();
            });

            // Normaliza al perder foco
            combo_seleccionar_palet.getEditor().focusedProperty().addListener((_, _, is) -> {
                if (!is) {
                    String txt = combo_seleccionar_palet.getEditor().getText();
                    Palet match = base.stream()
                            .filter(p -> String.valueOf(p.getIdPalet()).equals(txt))
                            .findFirst().orElse(null);
                    combo_seleccionar_palet.getSelectionModel().select(match);
                    filtered.setPredicate(_ -> true);
                }
            });

            combo_seleccionar_palet.setPromptText(PLACEHOLDER_PALET);
            combo_seleccionar_palet.getSelectionModel().clearSelection();
        }

    }

    private void configurarEventosComboBoxes() {
        // Cambiar tipo → repoblar productos y aplicar
        tipoComboBox.setOnAction(_ -> {
            repoblarProductosPorTipo();
            //plicarFiltros();
        });

        // Cambiar producto → aplicar filtros
        //productoComboBox.setOnAction(_ -> aplicarFiltros());

        // Otros combos → aplicar filtros al pulsar "Buscar" (para no saturar)
        // Si quieres que sean reactivos, descomenta:
        // estanteriaComboBox.setOnAction(_ -> aplicarFiltros());
        // baldaComboBox.setOnAction(_ -> aplicarFiltros());
        // posicionComboBox.setOnAction(_ -> aplicarFiltros());
        // delanteComboBox.setOnAction(_ -> aplicarFiltros());
    }

    private void inicializarBotones() {
        buscarButton.setTooltip(new Tooltip("Aplicar filtros"));
        buscarButton.setOnAction(_ -> aplicarFiltros());

        inventory_reset_button.setTooltip(new Tooltip("Quitar filtros"));
        inventory_reset_button.setOnAction(_ -> resetFiltros());

        siguienteButton.setOnAction(_ -> siguientePagina());
        anteriorButton.setOnAction(_ -> anteriorPagina());
    }

    private void repoblarProductosPorTipo() {
        String tipo = tipoComboBox.getValue();
        List<String> productos = new ArrayList<>();
        productos.add(OPC_TODOS);

        if (tipo == null || OPC_TODOS.equals(tipo)) {
            productos.addAll(
                    Almacen.TodosProductos.stream()
                            .map(Producto::getIdentificadorProducto)
                            .collect(Collectors.toCollection(LinkedHashSet::new))
            );
        } else {
            productos.addAll(
                    Almacen.TodosProductos.stream()
                            .filter(p -> tipo.equals(p.getTipo().getIdTipo()))
                            .map(Producto::getIdentificadorProducto)
                            .collect(Collectors.toCollection(LinkedHashSet::new))
            );
        }

        productoComboBox.setItems(FXCollections.observableArrayList(productos));
        productoComboBox.getSelectionModel().selectFirst();
    }

    private void resetFiltros() {
        currentPage = 0;
        // ---- reset combos de filtros ----
        estanteriaComboBox.getSelectionModel().selectFirst();  // "Todos"
        baldaComboBox.getSelectionModel().selectFirst();       // "Todos"
        posicionComboBox.getSelectionModel().selectFirst();    // "Todos"
        tipoComboBox.getSelectionModel().selectFirst();        // "Todos"
        repoblarProductosPorTipo();                            // repone productoComboBox y selecciona "Todos"
        delanteComboBox.getSelectionModel().selectFirst();     // "Todos"

        // ---- reset combo de palet (sin tocar sus items) ----
        combo_seleccionar_palet.getSelectionModel().clearSelection();
        combo_seleccionar_palet.getEditor().clear();

        estanteriaComboBox.setDisable(false);
        baldaComboBox.setDisable(false);
        posicionComboBox.setDisable(false);
        tipoComboBox.setDisable(false);
        productoComboBox.setDisable(false);
        delanteComboBox.setDisable(false);

        // ---- volver a mostrar todo ----
        aplicarFiltros();

    }

    // ---------------------- Filtro + Render -------------------º---
    private void aplicarFiltros() {
        currentPage = 0;
        filteredPalets = filtrarPalets();
        calcularTotalPaginas();
        renderizarPagina();
    }

    private List<Palet> filtrarPalets() {
        String estanteria = estanteriaComboBox.getValue();
        String balda = baldaComboBox.getValue();
        String posicion = posicionComboBox.getValue();
        String producto = productoComboBox.getValue();
        String tipoProd = tipoComboBox.getValue();
        String delante = delanteComboBox.getValue();

        // ==== 1) Resolver id de palet a partir del combo ====
        Palet seleccionado = combo_seleccionar_palet.getSelectionModel().getSelectedItem();
        Integer idPaletFiltro = null;

        if (seleccionado != null) {
            // Caso normal: hay un palet realmente seleccionado
            idPaletFiltro = seleccionado.getIdPalet();
        } else {
            // Caso: el usuario ha escrito un id en el editor pero no ha seleccionado
            String texto = combo_seleccionar_palet.getEditor().getText();
            if (texto != null && !texto.isBlank()) {
                try {
                    idPaletFiltro = Integer.parseInt(texto.trim());
                } catch (NumberFormatException e) {
                    // texto no numérico → ignoramos filtro por id
                    LOGGER.fine("Texto no numérico en combo_seleccionar_palet: " + texto);
                }
            }
        }

        // Log de depuración (opcional)
        LOGGER.fine("Filtrando. idPaletFiltro = " + idPaletFiltro);

        Integer finalIdPaletFiltro = idPaletFiltro;
        return TodosPalets.stream()
                // ---- filtro por idPalet (solo si hay filtro) ----
                .filter(p -> finalIdPaletFiltro == null || p.getIdPalet() == finalIdPaletFiltro)

                // ---- filtros por ubicación ----
                .filter(p -> matchesIntFilter(p.getEstanteria(), estanteria))
                .filter(p -> matchesIntFilter(p.getBalda(), balda))
                .filter(p -> matchesIntFilter(p.getPosicion(), posicion))

                // ---- filtro por producto ----
                .filter(p -> OPC_TODOS.equals(producto)
                        || producto == null
                        || producto.equals(p.getIdProducto()))

                // ---- filtro por tipo ----
                .filter(p -> OPC_TODOS.equals(tipoProd)
                        || tipoProd == null
                        || tipoProd.equals(p.getProducto().getTipo().getIdTipo()))

                // ---- filtro delante/detrás ----
                .filter(p -> {
                    if (delante == null || OPC_TODOS.equals(delante)) return true;
                    boolean isDelante = "Delante".equalsIgnoreCase(delante);
                    return p.isDelante() == isDelante;
                })
                .collect(Collectors.toList());
    }

    private static boolean matchesIntFilter(int value, String filter) {
        if (filter == null || OPC_TODOS.equals(filter)) return true;
        try {
            return value == Integer.parseInt(filter);
        } catch (NumberFormatException e) {
            return true; // si algo raro llega, no filtra por ese campo
        }
    }

    private void renderizarPagina() {
        limpiarGridPane(grid);

        int fromIndex = currentPage * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredPalets.size());

        int col = 0;
        int row = 1;

        try {
            for (int i = fromIndex; i < toIndex; i++) {
                Palet palet = filteredPalets.get(i);

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ITEM_INVENTARIO_FXML));
                AnchorPane anchorPane = fxmlLoader.load();

                ItemInventarioController itemController = fxmlLoader.getController();
                itemController.setData(palet);

                if (col == COLUMNS) {
                    col = 0;
                    row++;
                }
                grid.add(anchorPane, col++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error renderizando la página del inventario", e);
        }

        actualizarNavegacionYPagina();
    }

    private void calcularTotalPaginas() {
        totalPages = Math.max(1, (int) Math.ceil((double) filteredPalets.size() / ITEMS_PER_PAGE));
    }

    private void actualizarNavegacionYPagina() {
        anteriorButton.setDisable(currentPage <= 0);
        siguienteButton.setDisable(currentPage >= totalPages - 1);
        current_page_label.setText((currentPage + 1) + "/" + totalPages);
    }

    // ---------------------- Paginación ----------------------
    @FXML
    private void siguientePagina() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            renderizarPagina();
        }
    }

    @FXML
    private void anteriorPagina() {
        if (currentPage > 0) {
            currentPage--;
            renderizarPagina();
        }
    }

    // ---------------------- Util ----------------------
    private void limpiarGridPane(GridPane gridPane) {
        windowComponentAndFuncionalty.limpiarGridPane(gridPane);
    }

    // -------- Volcado a etiquetas (robusto) --------

}
