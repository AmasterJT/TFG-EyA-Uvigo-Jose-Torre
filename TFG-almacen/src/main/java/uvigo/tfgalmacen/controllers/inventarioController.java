package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.almacenManagement.Palet;
import uvigo.tfgalmacen.database.PaletDAO;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;


import uvigo.tfgalmacen.almacenManagement.Almacen;

public class inventarioController implements Initializable {


    @FXML
    private ScrollPane scroll;

    @FXML
    private GridPane grid;

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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colAlto.setCellValueFactory(cellData -> cellData.getValue().altoProperty());
        colAncho.setCellValueFactory(cellData -> cellData.getValue().anchoProperty());
        colLargo.setCellValueFactory(cellData -> cellData.getValue().largoProperty());
        colIdProducto.setCellValueFactory(cellData -> cellData.getValue().idProductoProperty());
        colCantidadProducto.setCellValueFactory(cellData -> cellData.getValue().cantidadProductoProperty());
        colIdPalet.setCellValueFactory(cellData -> cellData.getValue().idPaletProperty());
        colEstanteria.setCellValueFactory(cellData -> cellData.getValue().estanteriaProperty());
        colBalda.setCellValueFactory(cellData -> cellData.getValue().baldaProperty());
        colPosicion.setCellValueFactory(cellData -> cellData.getValue().posicionProperty());
        colDelante.setCellValueFactory(cellData -> cellData.getValue().delanteProperty());

        List<Palet> palets = Almacen.TodosPalets;

        tablaInventario.getItems().setAll(palets);







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
                grid.setMinWidth(Region.USE_COMPUTED_SIZE);
                grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                grid.setMaxWidth(Region.USE_PREF_SIZE);

                //set grid height
                grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                grid.setMaxHeight(Region.USE_PREF_SIZE);

                GridPane.setMargin(anchorPane, new Insets(10));
                counter++;

                if (counter == 50) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



