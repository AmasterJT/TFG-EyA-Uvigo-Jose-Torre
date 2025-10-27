package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.List;

public class ItemOrdenCompraController {

    @FXML
    private TextField cant_producto_text;

    @FXML
    private ComboBox<String> combo_balda_itemOc;

    @FXML
    private ComboBox<String> combo_estanteria_itemOc;

    @FXML
    private ComboBox<String> combo_posicion_itemOc;

    @FXML
    private CheckBox delante_checkBox;

    @FXML
    private Label nombre_producto_label;

    @FXML
    private Label nombre_proveedor_label;

    private int cantidad = 0;
    private int estanteria = 0;
    private int balda = 0;
    private int posicion = 0;
    private boolean delante = false;

    public List<posiciones_disponibles> posiciones_libres_almacen = null;


    public String getCant_producto_text() {
        return cant_producto_text.getText();
    }

    public String getCombo_balda_itemOc() {
        return combo_balda_itemOc.getValue();
    }

    public String getCombo_estanteria_itemOc() {
        return combo_estanteria_itemOc.getValue();
    }

    public String getCombo_posicion_itemOc() {
        return combo_posicion_itemOc.getValue();
    }

    public boolean getDelante_checkBox() {
        return delante_checkBox.isSelected();
    }


    public void set_basic_info(String proveedor, String producto) {
        nombre_proveedor_label.setText(proveedor);
        nombre_producto_label.setText(producto);

        init_comboBoxes();
    }

    public void crear_palet(String cantidad_producto, String estanteria, String balda, String posicion, boolean delante) {
        this.cantidad = Integer.parseInt(cantidad_producto);
        this.estanteria = Integer.parseInt(estanteria);
        this.balda = Integer.parseInt(balda);
        this.posicion = Integer.parseInt(posicion);
        this.delante = delante;
        System.out.println("Palet: "
                + "cantidad: " + cantidad
                + " estanteria: " + estanteria
                + " balda: " + balda
                + " posicion: " + posicion
                + " delante: " + delante);

    }


    private void init_comboBoxes() {

        // Añade valores fijos para estantería, balda, posición y delante/detrás
        combo_estanteria_itemOc.getItems().addAll("1", "2", "3", "4");
        combo_balda_itemOc.getItems().addAll(
                java.util.stream.IntStream.rangeClosed(1, 8).mapToObj(String::valueOf).toList()
        );
        combo_posicion_itemOc.getItems().addAll(
                java.util.stream.IntStream.rangeClosed(1, 24).mapToObj(String::valueOf).toList()
        );
    }


    class posiciones_disponibles {

        int estanteria;
        int balda;
        int posicion;
        boolean dealnte;


    }

}

