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
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.almacenManagement.Palet;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.Producto;
import uvigo.tfgalmacen.almacenManagement.Tipo;

public class inventarioController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(inventarioController.class.getName());

    private ArrayList<String> todosLosProductos = new ArrayList<>();





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


    /*
    @FXML
    private TableView<Palet> tablaInventario;
    Connection conexion = Main.connection;

    @FXML private TableColumn<Palet, String> colAlto;
    @FXML private TableColumn<Palet, String> colAncho;
    @FXML private TableColumn<Palet, String> colLargo;
    @FXML private TableColumn<Palet, String> colIdProducto;
    @FXML private TableColumn<Palet, String> colCantidadProducto;
    @FXML private TableColumn<Palet, String> colIdPalet;
    @FXML private TableColumn<Palet, Number> colEstanteria;
    @FXML private TableColumn<Palet, Number> colBalda;
    @FXML private TableColumn<Palet, String> colPosicion;
    @FXML private TableColumn<Palet, String> colDelante;
    */


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        grid.prefWidthProperty().bind(scroll.widthProperty());

        //colAlto.setCellValueFactory(cellData -> cellData.getValue().altoProperty());
        //colAncho.setCellValueFactory(cellData -> cellData.getValue().anchoProperty());
        //colLargo.setCellValueFactory(cellData -> cellData.getValue().largoProperty());
        //colIdProducto.setCellValueFactory(cellData -> cellData.getValue().idProductoProperty());
        //colCantidadProducto.setCellValueFactory(cellData -> cellData.getValue().cantidadProductoProperty());
        //colIdPalet.setCellValueFactory(cellData -> cellData.getValue().idPaletProperty());
        //colEstanteria.setCellValueFactory(cellData -> cellData.getValue().estanteriaProperty());
        //colBalda.setCellValueFactory(cellData -> cellData.getValue().baldaProperty());
        //colPosicion.setCellValueFactory(cellData -> cellData.getValue().posicionProperty());
        //colDelante.setCellValueFactory(cellData -> cellData.getValue().delanteProperty());

        List<Palet> palets = Almacen.TodosPalets;

        //tablaInventario.getItems().setAll(palets);





        limpiarGridPane(grid);

        int column = 0;
        int row = 1;
        int counter = 0;
        try {
            for (Palet palet : palets) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/uvigo/tfgalmacen/itemInventario.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                ItemInventarioController itemController = fxmlLoader.getController();
                itemController.setData(palet);


                if (column == 5) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row); //(child,column,row)
                //set grid width
                //grid.setMinWidth(Region.USE_COMPUTED_SIZE);
                //grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                //grid.setMaxWidth(Region.USE_PREF_SIZE);
                GridPane.setMargin(anchorPane, new Insets(10));

                //set grid height
                grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                grid.setMaxHeight(Region.USE_PREF_SIZE);


                GridPane.setMargin(anchorPane, new Insets(10));
                counter++;

                if (counter == 100) {
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error ejecutando someMethod", e);
        }

        // Agregar "Todos" al principio
        productoComboBox.getItems().add("Todos");
        tipoComboBox.getItems().add("Todos");
        delanteComboBox.getItems().add("Todos");

        estanteriaComboBox.getItems().add("Todos");
        baldaComboBox.getItems().add("Todos");
        posicionComboBox.getItems().add("Todos");


        // Llenar productos únicos y tipos si aplica
        for (Palet palet : Almacen.TodosPalets) {
            String idProducto = palet.getIdProducto();
            String tipo = palet.getProducto().getTipo().getIdTipo();

            if (!productoComboBox.getItems().contains(idProducto)) {
                productoComboBox.getItems().add(idProducto);
            }

            if (!tipoComboBox.getItems().contains(tipo)) {
                tipoComboBox.getItems().add(tipo);
            }
        }

        estanteriaComboBox.getItems().addAll("1", "2", "3", "4");
        delanteComboBox.getItems().addAll("Delante", "Detrás");
        // Para baldaComboBox con números del 1 al 8
        baldaComboBox.getItems().addAll(
                java.util.stream.IntStream.rangeClosed(1, 8)
                        .mapToObj(String::valueOf)
                        .toList()
        );

        // Para posicionComboBox con números del 1 al 24
        posicionComboBox.getItems().addAll(
                java.util.stream.IntStream.rangeClosed(1, 24)
                        .mapToObj(String::valueOf)
                        .toList()
        );

        // Listeners para filtros
        /*
        estanteriaComboBox.setOnAction(e -> aplicarFiltros());
        baldaComboBox.setOnAction(e -> aplicarFiltros());
        posicionComboBox.setOnAction(e -> aplicarFiltros());
        productoComboBox.setOnAction(e -> aplicarFiltros());
        tipoComboBox.setOnAction(e -> aplicarFiltros());
        delanteCheck.setOnAction(e -> aplicarFiltros());
        */


        // Selección por defecto: "Todos"
        productoComboBox.getSelectionModel().selectFirst();
        tipoComboBox.getSelectionModel().selectFirst();
        delanteComboBox.getSelectionModel().selectFirst();
        estanteriaComboBox.getSelectionModel().selectFirst();
        baldaComboBox.getSelectionModel().selectFirst();
        posicionComboBox.getSelectionModel().selectFirst();



        // Evento cuando se selecciona un tipo
        tipoComboBox.setOnAction(_ -> {
            String tipoSeleccionado = tipoComboBox.getSelectionModel().getSelectedItem();

            if (!tipoSeleccionado.equals("Todos")) {
                // Filtrar productos por el tipo seleccionado
                ArrayList<String> productosFiltrados = new ArrayList<>();
                productosFiltrados.add("Todos");
                for (Producto producto : Almacen.TodosProductos) {
                    if (producto.getIdTipo().equals(tipoSeleccionado)) {
                        productosFiltrados.add(producto.getIdProducto());

                    }
                }
                productoComboBox.setItems(FXCollections.observableArrayList(productosFiltrados));
                productoComboBox.setValue(productosFiltrados.getFirst());

                //llenamos la etiqueta con la informacion correspondiente
                for (Tipo tipo : Almacen.TodosTipos) {
                    if (tipo.getIdTipo().equals(tipoSeleccionado)) {
                        //etiquetaComboBox.setText("Palets totales: " + tipo.getNumPalets() + " = " + tipo.getCantidadDeTipo() + " L");
                        break;
                    }
                }

                //Mostramos los prodcutos correspondientes
                for (Palet palet : Almacen.TodosPalets) {
                    palet.getProductBox().setVisible(palet.getIdTipo().equals(tipoSeleccionado));
                    palet.getPaletBox().setVisible(palet.getIdTipo().equals(tipoSeleccionado));
                }

            } else {

                for (Producto producto : Almacen.TodosProductos) {
                    todosLosProductos.add(producto.getIdProducto());
                }
                todosLosProductos.add("Todos");
                productoComboBox.setItems(FXCollections.observableArrayList(todosLosProductos));
                productoComboBox.setValue("Todos");
                for (Palet palet : Almacen.TodosPalets) {
                    palet.getProductBox().setVisible(true);
                    palet.getPaletBox().setVisible(true);
                }
            }
        });

        // Evento cuando se selecciona un producto
        productoComboBox.setOnAction(_ -> {

            try {
                String productoSeleccionado = productoComboBox.getSelectionModel().getSelectedItem();
                //etiquetaComboBox.setText(productoSeleccionado);
                System.out.println(productoSeleccionado);


                if (!productoSeleccionado.equals("Todos")) {

                    for (Producto producto : Almacen.TodosProductos) {
                        if (producto.getIdProducto().equals(productoSeleccionado)) {
                            //etiquetaComboBox.setText("Palet totales: " + producto.getNumPalets() + " = " + producto.getCantidadDeProducto() + " L");
                            break;
                        }
                    }

                    for (Palet palet : Almacen.TodosPalets) {
                        palet.getProductBox().setVisible(palet.getIdProducto().equals(productoSeleccionado));
                        palet.getPaletBox().setVisible(palet.getIdProducto().equals(productoSeleccionado));
                    }


                } else {
                    for (Palet palet : Almacen.TodosPalets) {
                        palet.getProductBox().setVisible(palet.getIdTipo().equals(tipoComboBox.getSelectionModel().getSelectedItem()));
                        palet.getPaletBox().setVisible(palet.getIdTipo().equals(tipoComboBox.getSelectionModel().getSelectedItem()));
                    }
                }

            } catch (Exception ignore) {

            }
        });

        buscarButton.setOnAction(_ -> aplicarFiltros());

    }


    public void limpiarGridPane(GridPane gridPane) {
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
    }

    private void aplicarFiltros() {
        String estanteria = estanteriaComboBox.getValue();
        String balda = baldaComboBox.getValue();
        String posicion = posicionComboBox.getValue();
        String producto = productoComboBox.getValue();
        String tipoProducto = tipoComboBox.getValue();
        String delante = delanteComboBox.getValue();

        limpiarGridPane(grid);
        int counter = 0;
        int column = 0;
        int row = 1;

        try {
            for (Palet palet : Almacen.TodosPalets) {
                if ((estanteria == null || estanteria.equals("Todos") ||  palet.getEstanteria() == Integer.parseInt(estanteria)) &&
                        (balda == null || balda.equals("Todos") ||  palet.getBalda() == Integer.parseInt(balda)) &&
                        (posicion == null || posicion.equals("Todos") ||  palet.getPosicion() == Integer.parseInt(posicion)) &&
                        (producto == null || producto.equals("Todos") || producto.equals(palet.getIdProducto())) &&
                        (tipoProducto == null || tipoProducto.equals("Todos") || tipoProducto.equals(palet.getProducto().getTipo().getIdTipo())) &&
                        (delante == null || delante.equals("Todos") || palet.isDelante() == delante.equalsIgnoreCase("delante"))) {

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/uvigo/tfgalmacen/itemInventario.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();

                    ItemInventarioController itemController = fxmlLoader.getController();
                    itemController.setData(palet);

                    if (column == 5) {
                        column = 0;
                        row++;
                    }

                    grid.add(anchorPane, column++, row);
                    GridPane.setMargin(anchorPane, new Insets(10));

                    counter++;
                    if (counter == 100) {
                        break;
                    }
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error ejecutando someMethod", e);
        }


    }

}

