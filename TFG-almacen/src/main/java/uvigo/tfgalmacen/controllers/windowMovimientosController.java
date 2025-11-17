package uvigo.tfgalmacen.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.Palet;
import uvigo.tfgalmacen.utils.ColorFormatter;
import uvigo.tfgalmacen.utils.ComboFilters;


public class windowMovimientosController implements Initializable {


    private static final Logger LOGGER = Logger.getLogger(windowMovimientosController.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new ColorFormatter());
        LOGGER.addHandler(ch);

        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            h.setLevel(Level.ALL);
        }
    }

    private static final String PLACEHOLDER_PALET = "Seleccionar palet";


    @FXML
    private Button ExitButton;

    @FXML
    private AnchorPane Pane;

    @FXML
    private Label balda_actual_Label;

    @FXML
    private ComboBox<Integer> combo_nueva_balda;

    @FXML
    private ComboBox<Integer> combo_nueva_estanteria;

    @FXML
    private ComboBox<Integer> combo_nueva_posicion;

    @FXML
    private ComboBox<Palet> combo_seleccionar_palet;

    @FXML
    private Label delante_actual_Label;

    @FXML
    private Label estanteria_actual_Label;

    @FXML
    private Button generar_compra_btn;

    @FXML
    private Label identificador_producto;

    @FXML
    private Label identificador_tipo;

    @FXML
    private Label identificador_tipo1;

    @FXML
    private CheckBox poner_delante_check;

    @FXML
    private Label posicion_actual_Label;

    @FXML
    private Region spacer1;

    @FXML
    private Region spacer2;

    @FXML
    private Region spacer3;

    @FXML
    private HBox windowBar;

    ArrayList<Palet> TodosPalets;
    int NUM_ESTANTERIAS = 0;
    int NUM_BALDAS_PER_ESTANTERIA = 0;
    int POSICIONES_PER_BALDA = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarBotonesVentana();
        Almacen.actualizarAlmacen();
        NUM_ESTANTERIAS = Almacen.NUM_ESTANTERIAS;
        NUM_BALDAS_PER_ESTANTERIA = Almacen.NUM_BALDAS_PER_ESTANTERIA;
        POSICIONES_PER_BALDA = Almacen.POSICIONES_PER_BALDA;
        TodosPalets = Almacen.TodosPalets;

        inicializarComboBoxes();
    }

    private void inicializarComboBoxes() {

        // ===== Palets: mantener ComboBox<Palet> =====
        combo_seleccionar_palet.setEditable(true);
        combo_seleccionar_palet.getItems().clear();

        if (TodosPalets == null || TodosPalets.isEmpty()) {
            LOGGER.warning("No hay palets cargados.");
            combo_seleccionar_palet.setPromptText(PLACEHOLDER_PALET);
        } else {
            // Ordenar por id
            var ordenados = TodosPalets.stream()
                    .sorted(Comparator.comparingInt(Palet::getIdPalet))
                    .toList();

            var base = FXCollections.observableArrayList(ordenados);
            var filtered = new javafx.collections.transformation.FilteredList<>(base, _x -> true);
            combo_seleccionar_palet.setItems(filtered);

            // Mostrar solo el número en celdas y botón
            combo_seleccionar_palet.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Palet p) {
                    return (p == null) ? "" : String.valueOf(p.getIdPalet());
                }

                @Override
                public Palet fromString(String s) {
                    try {
                        int id = Integer.parseInt(s.trim());
                        return base.stream().filter(pp -> pp.getIdPalet() == id).findFirst().orElse(null);
                    } catch (Exception e) {
                        return null;
                    }
                }
            });
            combo_seleccionar_palet.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Palet p, boolean empty) {
                    super.updateItem(p, empty);
                    setText(empty || p == null ? null : String.valueOf(p.getIdPalet()));
                }
            });
            combo_seleccionar_palet.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Palet p, boolean empty) {
                    super.updateItem(p, empty);
                    setText(empty || p == null ? "" : String.valueOf(p.getIdPalet()));
                }
            });

            // Filtrado en vivo por lo tecleado
            combo_seleccionar_palet.getEditor().textProperty().addListener((obs, old, typed) -> {
                String term = typed == null ? "" : typed.trim();
                // Evita que al abrir el popup borre la lista si hay seleccionado
                var sel = combo_seleccionar_palet.getSelectionModel().getSelectedItem();
                if (sel != null && String.valueOf(sel.getIdPalet()).equals(term)) {
                    filtered.setPredicate(_x -> true);
                    return;
                }
                filtered.setPredicate(p -> term.isEmpty() || String.valueOf(p.getIdPalet()).contains(term));
                if (!combo_seleccionar_palet.isShowing()) combo_seleccionar_palet.show();
            });

            // Normaliza al perder foco
            combo_seleccionar_palet.getEditor().focusedProperty().addListener((o, was, is) -> {
                if (!is) {
                    String txt = combo_seleccionar_palet.getEditor().getText();
                    Palet match = base.stream()
                            .filter(p -> String.valueOf(p.getIdPalet()).equals(txt))
                            .findFirst().orElse(null);
                    combo_seleccionar_palet.getSelectionModel().select(match);
                    filtered.setPredicate(_x -> true);
                }
            });

            combo_seleccionar_palet.setPromptText(PLACEHOLDER_PALET);
            combo_seleccionar_palet.getSelectionModel().clearSelection();
        }

        ObservableList<Integer> estanterias = FXCollections.observableArrayList();
        for (int i = 1; i <= NUM_ESTANTERIAS; i++) estanterias.add(i);

        ObservableList<Integer> baldas = FXCollections.observableArrayList();
        for (int i = 1; i <= NUM_BALDAS_PER_ESTANTERIA; i++) baldas.add(i);

        ObservableList<Integer> posiciones = FXCollections.observableArrayList();
        for (int i = 1; i <= POSICIONES_PER_BALDA; i++) posiciones.add(i);

        // Hacerlos filtrables por lo que se teclee (p.ej. “23” -> mostrará 23, 123, 230, etc.)
        ComboFilters.makeFilterable(combo_nueva_estanteria, estanterias, Object::toString);
        ComboFilters.makeFilterable(combo_nueva_balda, baldas, Object::toString);
        ComboFilters.makeFilterable(combo_nueva_posicion, posiciones, Object::toString);

        // Prompt y selección inicial (opcional)
        combo_nueva_estanteria.setPromptText("Estantería");
        combo_nueva_balda.setPromptText("Balda");
        combo_nueva_posicion.setPromptText("Posición");

        combo_nueva_estanteria.getSelectionModel().clearSelection();
        combo_nueva_balda.getSelectionModel().clearSelection();
        combo_nueva_posicion.getSelectionModel().clearSelection();

    }

    private void configurarBotonesVentana() {

        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();

            ArrayList<Palet> TodosPalets = Almacen.TodosPalets;
        });
    }


}
