package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import uvigo.tfgalmacen.almacenManagement.Palet;

import static uvigo.tfgalmacen.almacenManagement.Almacen.*;


public class ItemOrdenCompraController {

    private boolean esta_palet_anadido = false;

    @FXML
    private HBox background_Hbox;


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

    public boolean isEsta_palet_anadido() {
        return esta_palet_anadido;
    }

    public void setEsta_palet_anadido(boolean esta_palet_anadido) {
        this.esta_palet_anadido = esta_palet_anadido;
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

    public Palet crear_palet(String producto, int estanteria, int balda, int posicion, boolean delante) {

        String green_color_back = "-fx-background-color: #056705;";
        background_Hbox.setStyle(green_color_back);
        esta_palet_anadido = true;

        return new Palet(producto, estanteria, balda, posicion, delante);

    }


    private void init_comboBoxes() {

        // Añade valores fijos para estantería, balda, posición y delante/detrás
        llenarComboNumerico(combo_estanteria_itemOc, NUM_ESTANTERIAS);
        combo_balda_itemOc.getItems().addAll(
                java.util.stream.IntStream.rangeClosed(1, NUM_BALDAS_PER_ESTANTERIA).mapToObj(String::valueOf).toList()
        );
        combo_posicion_itemOc.getItems().addAll(
                java.util.stream.IntStream.rangeClosed(1, POSICIONES_PER_BALDA).mapToObj(String::valueOf).toList()
        );
    }

    private void llenarComboNumerico(ComboBox<String> combo, int max) {
        combo.getItems().clear();
        for (int i = 1; i <= max; i++) {
            combo.getItems().add(String.valueOf(i));
        }
    }


}

