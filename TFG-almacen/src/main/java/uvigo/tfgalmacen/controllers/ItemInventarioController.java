package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import uvigo.tfgalmacen.almacenManagement.Palet;

public class ItemInventarioController {

    @FXML
    private Label identificador_producto;

    @FXML
    private Label identificador_tipo;


    @FXML
    private AnchorPane backgroundAnchorPane;

    public void setData(Palet palet) {
        // Código para llenar los labels o nodos con la información del palet

        identificador_producto.setText(palet.getIdProducto());
        identificador_tipo.setText(palet.getProducto().getTipo().getIdTipo());

        String colorHEX = palet.getProducto().getTipo().applyPastelFilter(palet.getProducto().getTipo().colorHEX);
        System.out.println("Color:" + colorHEX);

        backgroundAnchorPane.setStyle("-fx-background-color: " +  colorHEX);
    }
}
