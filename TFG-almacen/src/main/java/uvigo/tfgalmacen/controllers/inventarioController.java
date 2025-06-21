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
import javafx.scene.layout.Region;
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
    /** Número máximo de items a mostrar en el grid. */
    private final int NUM_ITEMS_GRID = 50;
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
    }

    /**
     * Resetea los filtros aplicados y vuelve a mostrar todos los palets.
     */
    private void resetFiltros() {
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
     * Renderiza una lista de palets en el GridPane.
     * @param palets Lista de palets a mostrar
     */
    private void renderizarPalets(List<Palet> palets) {
        limpiarGridPane(grid);
        int column = 0, row = 1, counter = 0;
        try {
            for (Palet palet : palets) {
                if (counter == NUM_ITEMS_GRID) break;
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
                grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                grid.setMaxHeight(Region.USE_PREF_SIZE);
                counter++;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error cargando los palets en el grid", e);
        }
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
                    productosFiltrados.add(producto.getIdProducto());
                }
            }
            productoComboBox.setItems(FXCollections.observableArrayList(productosFiltrados));
            productoComboBox.setValue(productosFiltrados.getFirst());
            mostrarPaletsPorTipo(tipoSeleccionado);
        } else {
            todosLosProductos.clear();
            for (Producto producto : Almacen.TodosProductos) {
                todosLosProductos.add(producto.getIdProducto());
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
        limpiarGridPane(grid);
        List<Palet> paletsFiltrados = filtrarPalets();
        agregarPaletsAGrid(paletsFiltrados);
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
            }
        }
        return resultado;
    }

    /**
     * Agrega los palets filtrados al GridPane para su visualización.
     * @param palets Lista de palets a agregar
     */
    private void agregarPaletsAGrid(List<Palet> palets) {
        int counter = 0;
        int column = 0;
        int row = 1;

        try {
            for (Palet palet : palets) {
                if (counter == NUM_ITEMS_GRID) break;

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
                counter++;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error aplicando filtros", e);
        }
    }
}