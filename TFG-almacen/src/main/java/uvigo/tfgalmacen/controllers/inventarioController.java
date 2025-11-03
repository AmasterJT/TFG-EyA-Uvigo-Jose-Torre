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

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static uvigo.tfgalmacen.RutasFicheros.ITEM_INVENTARIO_FXML;

/**
 * Controlador para la vista de inventario.
 * Gestiona la carga, visualización y filtrado de los palets disponibles en el almacén
 * mediante un GridPane y ComboBoxes de filtros.
 */
public class inventarioController implements Initializable {

    // ---------------------- Logger ----------------------
    private static final Logger LOGGER = Logger.getLogger(inventarioController.class.getName());

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
    private static final List<String> ESTANTERIAS = List.of("1", "2", "3", "4");
    private static final List<String> BALDAS = IntStream.rangeClosed(1, 8).mapToObj(String::valueOf).collect(Collectors.toList());
    private static final List<String> POSICIONES = IntStream.rangeClosed(1, 24).mapToObj(String::valueOf).collect(Collectors.toList());

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

    // ---------------------- Ciclo de vida ----------------------
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarScrollYGrid();
        inicializarComboBoxes();
        configurarEventosComboBoxes();
        inicializarBotones();

        // Estado inicial: sin filtros → todos los palets
        aplicarFiltros();
    }

    // ---------------------- Setup UI ----------------------
    private void configurarScrollYGrid() {
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        grid.prefWidthProperty().bind(scroll.widthProperty());
    }

    private void inicializarComboBoxes() {
        // Limpia y añade "Todos" + valores
        productoComboBox.getItems().setAll(OPC_TODOS);
        tipoComboBox.getItems().setAll(OPC_TODOS);
        delanteComboBox.getItems().setAll(OPC_DELANTE);

        estanteriaComboBox.getItems().setAll(OPC_TODOS);
        estanteriaComboBox.getItems().addAll(ESTANTERIAS);

        baldaComboBox.getItems().setAll(OPC_TODOS);
        baldaComboBox.getItems().addAll(BALDAS);

        posicionComboBox.getItems().setAll(OPC_TODOS);
        posicionComboBox.getItems().addAll(POSICIONES);

        // Añadir productos y tipos únicos (de los palets/productos en catálogo)
        Set<String> tipos = Almacen.TodosProductos.stream()
                .map(p -> p.getTipo().getIdTipo())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        tipoComboBox.getItems().addAll(tipos);

        Set<String> idsProducto = Almacen.TodosProductos.stream()
                .map(Producto::getIdentificadorProducto)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        productoComboBox.getItems().addAll(idsProducto);

        // Seleccionar primera opción
        tipoComboBox.getSelectionModel().selectFirst();
        productoComboBox.getSelectionModel().selectFirst();
        delanteComboBox.getSelectionModel().selectFirst();
        estanteriaComboBox.getSelectionModel().selectFirst();
        baldaComboBox.getSelectionModel().selectFirst();
        posicionComboBox.getSelectionModel().selectFirst();
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
        inicializarComboBoxes();
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

        return Almacen.TodosPalets.stream()
                .filter(p -> matchesIntFilter(p.getEstanteria(), estanteria))
                .filter(p -> matchesIntFilter(p.getBalda(), balda))
                .filter(p -> matchesIntFilter(p.getPosicion(), posicion))
                .filter(p -> OPC_TODOS.equals(producto) || producto == null || producto.equals(p.getIdProducto()))
                .filter(p -> OPC_TODOS.equals(tipoProd) || tipoProd == null || tipoProd.equals(p.getProducto().getTipo().getIdTipo()))
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
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
    }
}
