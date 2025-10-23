package uvigo.tfgalmacen.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.Proveedor;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.Palet;
import uvigo.tfgalmacen.almacenManagement.Producto;


import java.net.URL;
import java.util.ResourceBundle;

public class ordenCompraController implements Initializable {
    @FXML
    private Button ExitButton;

    @FXML
    private AnchorPane Pane;

    @FXML
    private Button agregar_palet_oc;

    @FXML
    private ComboBox<String> combo_producto_oc;

    @FXML
    private ComboBox<String> combo_proveedor_oc;

    @FXML
    private Button generar_compra_btn;

    @FXML
    private ListView<?> list_palets_agregados_oc;

    @FXML
    private HBox windowBar;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ExitButton.setOnMouseClicked(event -> {

            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();
        });

        if (agregar_palet_oc != null) {
            generar_compra_btn.setOnAction(_ -> setRecepcion());
        }

        inicializarComboBoxes();
    }

    private void setRecepcion() {
        System.out.println("compra generada");
    }

    private void inicializarComboBoxes() {
        combo_producto_oc.getItems().add("Seleccionar producto");
        combo_proveedor_oc.getItems().add("Seleccionar proveedor");


        // Añade productos y tipos únicos
        for (Producto producto : Almacen.TodosProductos) {
            String idProducto = producto.getIdentificadorProducto();
            if (!combo_producto_oc.getItems().contains(idProducto)) combo_producto_oc.getItems().add(idProducto);
        }

        for (Proveedor proveedor : Almacen.TodosProveedores) {
            String nombre_proveedor = proveedor.getNombre();
            if (!combo_proveedor_oc.getItems().contains(nombre_proveedor))
                combo_proveedor_oc.getItems().add(nombre_proveedor);
        }

        // Selecciona el primer valor por defecto
        combo_producto_oc.getSelectionModel().selectFirst();
        combo_proveedor_oc.getSelectionModel().selectFirst();
    }


}
