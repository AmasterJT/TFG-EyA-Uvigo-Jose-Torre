package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.almacenManagement.Palet;
import uvigo.tfgalmacen.database.PaletDAO;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;


public class inventarioController implements Initializable {

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

        tablaInventario.getItems().setAll(PaletDAO.getAllPalets(conexion));
    }
}



