package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import uvigo.tfgalmacen.almacenManagement.Palet;

import java.net.URL;
import java.util.ResourceBundle;

import static uvigo.tfgalmacen.utils.ClipboardUtils.copyLabelText;

public class ItemInventarioController implements Initializable {

    @FXML
    private Label baldaLabel;

    @FXML
    private Label delanteLabel;

    @FXML
    private Label estanteriaLabel;

    @FXML
    private Label identificador_producto;

    @FXML
    private Label identificador_tipo;

    @FXML
    private Label idPaletLabel;

    @FXML
    private Label posicionLabel;


    @FXML
    private AnchorPane backgroundAnchorPane;

    @FXML
    private Button copy_idpalet_btn;

    public void setData(Palet palet) {
        // Código para llenar los labels o nodos con la información del palet

        identificador_producto.setText(palet.getIdProducto());
        identificador_tipo.setText(palet.getProducto().getTipo().getIdTipo());

        estanteriaLabel.setText(String.valueOf(palet.getEstanteria()));
        baldaLabel.setText(String.valueOf(palet.getBalda()));
        posicionLabel.setText(String.valueOf(palet.getPosicion()));
        idPaletLabel.setText(String.valueOf(palet.getIdPalet()));


        String del = palet.isDelante() ? " ✅" : " ❌";
        delanteLabel.setText(del);

        String colorHEX = palet.getProducto().getTipo().applyPastelFilter(palet.getProducto().getTipo().colorHEX);
        backgroundAnchorPane.setStyle("-fx-background-color: " + colorHEX);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        copy_idpalet_btn.setOnMouseClicked(_ -> copyLabelText(copy_idpalet_btn, idPaletLabel));

    }
}
