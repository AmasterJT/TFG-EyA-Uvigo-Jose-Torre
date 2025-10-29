package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import uvigo.tfgalmacen.almacenManagement.Palet;

import java.util.List;

import static uvigo.tfgalmacen.utils.TerminalColors.GREEN;

public class ItemOrdenCompraController {

    private boolean esta_palet_añadido = false;

    @FXML
    private HBox background_Hbox;

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

    public TextField get_cant_producto_text() {
        return cant_producto_text;
    }

    public ComboBox<String> get_balda_itemOc() {
        return combo_balda_itemOc;
    }

    public ComboBox<String> get_combo_estanteria_itemOc() {
        return combo_estanteria_itemOc;
    }

    public ComboBox<String> get_combo_posicion_itemOc() {
        return combo_posicion_itemOc;
    }

    public CheckBox get_delante_checkBox() {
        return delante_checkBox;
    }

    public boolean isEsta_palet_añadido() {
        return esta_palet_añadido;
    }

    public void setEsta_palet_añadido(boolean esta_palet_añadido) {
        this.esta_palet_añadido = esta_palet_añadido;
    }

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

    public void setBackground_Hbox(String background_color) {
        String green_color_back = "-fx-background-color: " + background_color + ";";
        background_Hbox.setStyle(green_color_back);
    }


    public void set_basic_info(String proveedor, String producto) {
        nombre_proveedor_label.setText(proveedor);
        nombre_producto_label.setText(producto);

        init_comboBoxes();
    }

    public String get_producto_nombre() {
        return nombre_producto_label.getText();
    }

    public String get_proveedor_nombre() {
        return nombre_proveedor_label.getText();
    }

    public Palet crear_palet(String proveedor, String producto, int cantidad_producto, int estanteria, int balda, int posicion, boolean delante) {
        
        String green_color_back = "-fx-background-color: #056705;";
        background_Hbox.setStyle(green_color_back);
        esta_palet_añadido = true;

        return new Palet(producto, cantidad_producto, estanteria, balda, posicion, delante);

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

