/**
 * Controlador para la vista de inventario.
 * Gestiona la carga, visualización y filtrado de los palets disponibles en el almacén
 * mediante un GridPane y ComboBoxes de filtros.
 */
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase controladora para la gestión del inventario.
 */
public class inventarioController implements Initializable {

    /** Logger para registrar errores y eventos. */
    private static final Logger LOGGER = Logger.getLogger(inventarioController.class.getName());

    /** Lista auxiliar para almacenar todos los productos. */
    private final ArrayList<String> todosLosProductos = new ArrayList<>();

    /** Número de columnas del grid de palets. */
    private final int COLUMS = 6;

    /** Número de filas del grid de palets. */
    private final int ROWS = 7;

    /** Número máximo de items a mostrar en el grid. */
    private final int NUM_ITEMS_GRID = COLUMS * ROWS;

    /** Número de palets a mostrar. */
    private int NUM_PALETS = 0;

    /** Página actual del inventario. */
    private int paginaActual = 0;

    /** Total de páginas de inventario calculado según los filtros. */
    private int total_paginas_inventario = 0;

    // Filtros y controles visuales
    @FXML private ComboBox<String> estanteriaComboBox;
    @FXML private ComboBox<String> baldaComboBox;
    @FXML private ComboBox<String> posicionComboBox;
    @FXML private ComboBox<String> productoComboBox;
    @FXML private ComboBox<String> tipoComboBox;
    @FXML private ComboBox<String> delanteComboBox;
    @FXML private GridPane grid;
    @FXML private ScrollPane scroll;
    @FXML private Button buscarButton;
    @FXML private Button inventory_reset_button;
    @FXML private Button siguienteButton;
    @FXML private Button anteriorButton;
    @FXML private Label current_page_label;

    /**
     * Inicializa la interfaz de inventario:
     * - Configura scroll, grid y ComboBoxes.
     * - Carga los datos de los palets desde el almacén.
     * - Asocia eventos para filtrar productos según el tipo o selección directa.
     *
     * @param url URL de inicialización
     * @param resourceBundle Recursos de internacionalización
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarScrollYGrid();
        inicializarComboBoxes();
        renderizarPalets(Almacen.TodosPalets);
        configurarEventosComboBoxes();
        incializarBotones();
    }

    /**
     * Inicializa los botones de búsqueda y reset.
     * Asigna tooltips y acciones a los botones.
     */
    private void incializarBotones() {
        Tooltip buscarTooltip = new Tooltip("Aplicar filtros");
        buscarButton.setTooltip(buscarTooltip);
        buscarButton.setOnAction(_ -> aplicarFiltros());

        Tooltip resetTooltip = new Tooltip("Quitar filtros");
        inventory_reset_button.setTooltip(resetTooltip);
        inventory_reset_button.setOnAction(_ -> resetFiltros());

        siguienteButton.setOnAction(_ -> siguientePagina());
        anteriorButton.setOnAction(_ -> anteriorPagina());
    }

    /**
     * Resetea los filtros aplicados y vuelve a mostrar todos los palets.
     */
    private void resetFiltros() {
        paginaActual = 0;
        inicializarComboBoxes();
        renderizarPalets(Almacen.TodosPalets);
    }

    /**
     * Configura el comportamiento del scroll y el grid.
     */
    private void configurarScrollYGrid() {
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        grid.prefWidthProperty().bind(scroll.widthProperty());
    }

    /**
     * Inicializa los ComboBoxes de filtros con valores posibles.
     */
    private void inicializarComboBoxes() {
        productoComboBox.getItems().add("Todos");
        tipoComboBox.getItems().add("Todos");
        delanteComboBox.getItems().add("Todos");
        estanteriaComboBox.getItems().add("Todos");
        baldaComboBox.getItems().add("Todos");
        posicionComboBox.getItems().add("Todos");

        // Añade productos y tipos únicos
        for (Palet palet : Almacen.TodosPalets) {
            String idProducto = palet.getIdProducto();
            String tipo = palet.getProducto().getTipo().getIdTipo();
            if (!productoComboBox.getItems().contains(idProducto)) productoComboBox.getItems().add(idProducto);
            if (!tipoComboBox.getItems().contains(tipo)) tipoComboBox.getItems().add(tipo);
        }

        // Añade valores fijos para estantería, balda, posición y delante/detrás
        estanteriaComboBox.getItems().addAll("1", "2", "3", "4");
        delanteComboBox.getItems().addAll("Delante", "Detrás");
        baldaComboBox.getItems().addAll(
                java.util.stream.IntStream.rangeClosed(1, 8).mapToObj(String::valueOf).toList()
        );
        posicionComboBox.getItems().addAll(
                java.util.stream.IntStream.rangeClosed(1, 24).mapToObj(String::valueOf).toList()
        );

        // Selecciona el primer valor por defecto
        productoComboBox.getSelectionModel().selectFirst();
        tipoComboBox.getSelectionModel().selectFirst();
        delanteComboBox.getSelectionModel().selectFirst();
        estanteriaComboBox.getSelectionModel().selectFirst();
        baldaComboBox.getSelectionModel().selectFirst();
        posicionComboBox.getSelectionModel().selectFirst();
    }

    /**
     * Lista de palets actualmente mostrados en el GridPane.
     * Se utiliza para mantener el estado de los palets visibles.
     */
    private List<Palet> paletsMostrados = new ArrayList<>();

    /**
     * Renderiza una lista de palets en el GridPane.
     * @param palets Lista de palets a mostrar
     */
    private void renderizarPalets(List<Palet> palets) {
        paletsMostrados = palets;
        limpiarGridPane(grid);
        int totalPalets = palets.size();
        NUM_PALETS = totalPalets;
        int inicio = paginaActual * NUM_ITEMS_GRID;
        int fin = Math.min(inicio + NUM_ITEMS_GRID, totalPalets);

        int column = 0, row = 1;
        try {
            for (int i = inicio; i < fin; i++) {
                Palet palet = palets.get(i);
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/uvigo/tfgalmacen/itemInventario.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                ItemInventarioController itemController = fxmlLoader.getController();
                itemController.setData(palet);

                if (column == COLUMS) {
                    column = 0;
                    row++;
                }
                grid.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
                NUM_PALETS ++;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error cargando los palets en el grid", e);
        }
        actualizarBotonesNavegacion(totalPalets);
        total_paginas_inventario = totalPalets / NUM_ITEMS_GRID;

        actualizarLabelPagina();
    }

    /**
     * Navega a la siguiente página de palets en el GridPane.
     * Incrementa la página actual y renderiza los palets correspondientes.
     */
    @FXML private void siguientePagina() {
        paginaActual++;
        renderizarPalets(Almacen.TodosPalets);

        actualizarLabelPagina();
    }

    /**
     * Navega a la página anterior de palets en el GridPane.
     * Decrementa la página actual y renderiza los palets correspondientes.
     */
    @FXML private void anteriorPagina() {
        if (paginaActual > 0) {
            paginaActual--;
            renderizarPalets(Almacen.TodosPalets);

            actualizarLabelPagina();
        }
    }

    /**
     * Actualiza el estado de los botones de navegación según la página actual y el total de palets.
     * Deshabilita el botón "Anterior" si estamos en la primera página
     * y el botón "Siguiente" si no hay más palets para mostrar.
     * @param totalPalets Total de palets disponibles
     */
    private void actualizarBotonesNavegacion(int totalPalets) {
        anteriorButton.setDisable(paginaActual == 0);
        siguienteButton.setDisable((paginaActual + 1) * NUM_ITEMS_GRID >= totalPalets);
    }
    /**
     * Configura los eventos de los ComboBoxes para filtrar por tipo o producto.
     */
    private void configurarEventosComboBoxes() {
        tipoComboBox.setOnAction(_ -> filtrarPorTipo());
        productoComboBox.setOnAction(_ -> filtrarPorProducto());
    }

    /**
     * Filtra los productos según el tipo seleccionado y actualiza el ComboBox de productos.
     */
    private void filtrarPorTipo() {
        String tipoSeleccionado = tipoComboBox.getSelectionModel().getSelectedItem();
        if (!tipoSeleccionado.equals("Todos")) {
            ArrayList<String> productosFiltrados = new ArrayList<>();
            productosFiltrados.add("Todos");
            for (Producto producto : Almacen.TodosProductos) {
                if (producto.getIdTipo().equals(tipoSeleccionado)) {
                    productosFiltrados.add(producto.getIdentificadorProducto());
                }
            }
            productoComboBox.setItems(FXCollections.observableArrayList(productosFiltrados));
            productoComboBox.setValue(productosFiltrados.getFirst());
            mostrarPaletsPorTipo(tipoSeleccionado);
        } else {
            todosLosProductos.clear();
            for (Producto producto : Almacen.TodosProductos) {
                todosLosProductos.add(producto.getIdentificadorProducto());
            }
            todosLosProductos.add("Todos");
            productoComboBox.setItems(FXCollections.observableArrayList(todosLosProductos));
            productoComboBox.setValue("Todos");
            mostrarTodosLosPalets();
        }
    }

    /**
     * Filtra los palets según el producto seleccionado.
     */
    private void filtrarPorProducto() {
        String productoSeleccionado = productoComboBox.getSelectionModel().getSelectedItem();
        if (!productoSeleccionado.equals("Todos")) {
            for (Palet palet : Almacen.TodosPalets) {
                boolean visible = palet.getIdProducto().equals(productoSeleccionado);
                palet.getProductBox().setVisible(visible);
                palet.getPaletBox().setVisible(visible);
            }
        } else {
            String tipoSeleccionado = tipoComboBox.getSelectionModel().getSelectedItem();
            mostrarPaletsPorTipo(tipoSeleccionado);
        }
    }

    /**
     * Muestra solo los palets del tipo seleccionado.
     * @param tipoSeleccionado Tipo de producto seleccionado
     */
    private void mostrarPaletsPorTipo(String tipoSeleccionado) {
        for (Palet palet : Almacen.TodosPalets) {
            boolean visible = palet.getIdTipo().equals(tipoSeleccionado) || tipoSeleccionado.equals("Todos");
            palet.getProductBox().setVisible(visible);
            palet.getPaletBox().setVisible(visible);
        }
    }

    /**
     * Muestra todos los palets disponibles.
     */
    private void mostrarTodosLosPalets() {
        for (Palet palet : Almacen.TodosPalets) {
            palet.getProductBox().setVisible(true);
            palet.getPaletBox().setVisible(true);
        }
    }

    /**
     * Elimina todo el contenido del GridPane.
     * @param gridPane GridPane a limpiar
     */
    public void limpiarGridPane(GridPane gridPane) {
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
    }

    /**
     * Aplica los filtros seleccionados en los ComboBoxes
     * para mostrar solo los palets que cumplen las condiciones.
     */
    private void aplicarFiltros() {
        NUM_PALETS = 0;
        paginaActual = 0;
        limpiarGridPane(grid);
        List<Palet> paletsFiltrados = filtrarPalets();
        agregarPaletsAGrid(paletsFiltrados);
        total_paginas_inventario = NUM_PALETS / NUM_ITEMS_GRID;

        actualizarLabelPagina();

    }

    /**
     * Filtra la lista de palets según los valores seleccionados en los ComboBoxes.
     * @return Lista de palets que cumplen los filtros
     */
    private List<Palet> filtrarPalets() {

        String estanteria = estanteriaComboBox.getValue();
        String balda = baldaComboBox.getValue();
        String posicion = posicionComboBox.getValue();
        String producto = productoComboBox.getValue();
        String tipoProducto = tipoComboBox.getValue();
        String delante = delanteComboBox.getValue();

        List<Palet> resultado = new ArrayList<>();
        for (Palet palet : Almacen.TodosPalets) {
            if ((estanteria == null || estanteria.equals("Todos") || palet.getEstanteria() == Integer.parseInt(estanteria)) &&
                (balda == null || balda.equals("Todos") || palet.getBalda() == Integer.parseInt(balda)) &&
                (posicion == null || posicion.equals("Todos") || palet.getPosicion() == Integer.parseInt(posicion)) &&
                (producto == null || producto.equals("Todos") || producto.equals(palet.getIdProducto())) &&
                (tipoProducto == null || tipoProducto.equals("Todos") || tipoProducto.equals(palet.getProducto().getTipo().getIdTipo())) &&
                (delante == null || delante.equals("Todos") || palet.isDelante() == delante.equalsIgnoreCase("Delante"))) {
                resultado.add(palet);
                NUM_PALETS ++;
            }
        }
        return resultado;
    }

    /**
     * Agrega los palets filtrados al GridPane para su visualización.
     * @param palets Lista de palets a agregar
     */
    private void agregarPaletsAGrid(List<Palet> palets) {
        int inicio = paginaActual * NUM_ITEMS_GRID;
        int fin = Math.min(inicio + NUM_ITEMS_GRID, palets.size());
        int column = 0;
        int row = 1;

        NUM_PALETS = palets.size();

        try {
            for (int i = inicio; i < fin; i++) {
                Palet palet = palets.get(i);

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/uvigo/tfgalmacen/itemInventario.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                ItemInventarioController itemController = fxmlLoader.getController();
                itemController.setData(palet);

                if (column == COLUMS) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error aplicando filtros", e);
        }
    total_paginas_inventario = NUM_PALETS / NUM_ITEMS_GRID;
    }


    private void actualizarLabelPagina() {
        int pag = paginaActual + 1;
        current_page_label.setText(pag + "/" + total_paginas_inventario);
    }
}