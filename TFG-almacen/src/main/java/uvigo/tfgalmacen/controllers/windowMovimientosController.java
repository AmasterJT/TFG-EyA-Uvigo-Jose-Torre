package uvigo.tfgalmacen.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.Palet;
import uvigo.tfgalmacen.almacenManagement.Producto;
import uvigo.tfgalmacen.database.PaletDAO;
import uvigo.tfgalmacen.utils.ColorFormatter;
import uvigo.tfgalmacen.utils.ComboFilters;

import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.ventana_error;
import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.ventana_warning;


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
    private ComboBox<Integer> combo_nueva_balda;

    @FXML
    private ComboBox<Integer> combo_nueva_estanteria;

    @FXML
    private ComboBox<Integer> combo_nueva_posicion;

    @FXML
    private ComboBox<Palet> combo_seleccionar_palet;


    @FXML
    private Label estanteria_actual_Label;

    @FXML
    private Label balda_actual_Label;

    @FXML
    private Label posicion_actual_Label;

    @FXML
    private Label delante_actual_Label;

    @FXML
    private Label identificador_producto;

    @FXML
    private Label identificador_tipo;

    @FXML
    private Button mover_palet_btn;

    @FXML
    private CheckBox poner_delante_check;


    @FXML
    private Region spacer1;

    @FXML
    private Region spacer2;

    @FXML
    private Region spacer3;

    @FXML
    private HBox windowBar;

    @FXML
    private Button eliminar_palet_btn;

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

        combo_seleccionar_palet.valueProperty().addListener((obs, oldP, newP) -> actualizarLabelsUbicacionRobusta(newP));
        mover_palet_btn.setOnMouseClicked(_ -> aplicarMovimientoPalet());
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
        combo_nueva_estanteria.setPromptText("-");
        combo_nueva_balda.setPromptText("-");
        combo_nueva_posicion.setPromptText("-");

        combo_nueva_estanteria.getSelectionModel().clearSelection();
        combo_nueva_balda.getSelectionModel().clearSelection();
        combo_nueva_posicion.getSelectionModel().clearSelection();

    }


    private void aplicarMovimientoPalet() {
        try {
            // 1) Validaciones básicas
            var paletSel = combo_seleccionar_palet.getValue();
            if (paletSel == null) {
                LOGGER.warning("No hay palet seleccionado.");
                return;
            }

            Integer nuevaEstanteria = combo_nueva_estanteria.getValue();
            Integer nuevaBalda = combo_nueva_balda.getValue();
            Integer nuevaPosicion = combo_nueva_posicion.getValue();
            boolean delante = poner_delante_check.isSelected();

            if (nuevaEstanteria == null || nuevaBalda == null || nuevaPosicion == null) {
                LOGGER.warning("Debes seleccionar estantería, balda y posición.");
                return;
            }

            // 2) Ejecutar actualización en BBDD
            String identificador = String.valueOf(paletSel.getIdPalet()); // o paletSel.getIdPalet() si tu DAO lo requiere
            LOGGER.info(() -> String.format(
                    "Solicitud de movimiento de palet %s -> est:%d, bal:%d, pos:%d, delante:%s",
                    identificador, nuevaEstanteria, nuevaBalda, nuevaPosicion, delante
            ));

            boolean ok = PaletDAO.updateUbicacionSiLibre(
                    Main.connection,
                    identificador,
                    nuevaEstanteria,
                    nuevaBalda,
                    nuevaPosicion,
                    delante
            );

            if (!ok) {
                LOGGER.warning("No se pudo mover el palet. La ubicación puede estar ocupada o el palet no existe.");
                return;
            }

            // 3) (Opcional) Refrescar objeto/etiquetas en UI
            //    Si tu clase Palet tiene setters, actualiza el modelo local
            try {
                paletSel.setEstanteria(nuevaEstanteria);
                paletSel.setBalda(nuevaBalda);
                paletSel.setPosicion(nuevaPosicion);
                paletSel.setDelante(delante);
            } catch (Exception ignore) {
                // Por si tu clase Palet no tiene setters; no pasa nada
            }

            // Actualiza las etiquetas de ubicación actual (si ya tienes un método utilitario, úsalo)
            estanteria_actual_Label.setText(String.valueOf(nuevaEstanteria));
            balda_actual_Label.setText(String.valueOf(nuevaBalda));
            posicion_actual_Label.setText(String.valueOf(nuevaPosicion));
            delante_actual_Label.setText(delante ? "Sí" : "No");

            LOGGER.info(() -> String.format(
                    "Palet %s movido correctamente a est:%d, bal:%d, pos:%d, delante:%s",
                    identificador, nuevaEstanteria, nuevaBalda, nuevaPosicion, delante
            ));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al mover el palet desde la UI.", e);
        }

        ventana_warning("Operación completada", "Palet movido a la nueva posición", "Los cambios se han guardado correctamente.");

        Stage stage = (Stage) mover_palet_btn.getScene().getWindow();
        stage.close();
    }

    private void configurarBotonesVentana() {

        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();

            ArrayList<Palet> TodosPalets = Almacen.TodosPalets;
        });

        eliminar_palet_btn.setOnAction(_ -> {
            try {
                elimninarPalet();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private void elimninarPalet() throws SQLException {
        var paletSel = combo_seleccionar_palet.getValue();
        if (paletSel == null) {
            LOGGER.warning("No hay pedido seleccionado.");
            return;
        }


        Optional<ButtonType> decision = ventana_error(
                "Confirmar eliminación",
                "¿Deseas eliminar el palet " + paletSel.getIdPalet() + "?",
                "Se eliminará el producto del almacen.",
                "Sí, eliminar", "Cancelar"
        );

        if (decision.isEmpty() || decision.get().getButtonData() != ButtonBar.ButtonData.OK_DONE) {
            LOGGER.fine("Eliminación cancelada por el usuario (identifi=" + paletSel.getIdPalet() + ")");
            return;
        }

        PaletDAO.deletePaletByIdentificador(Main.connection, paletSel.getIdPalet());
        ventana_warning("Operación completada",
                "Palet eliminado del almacen",
                "Operacion realizada exitosamente.");
        Stage stage = (Stage) mover_palet_btn.getScene().getWindow();
        stage.close();
    }


    // -------- Helpers robustos --------
    private Integer getIntViaReflection(Object o, String... methods) {
        for (String m : methods) {
            try {
                var mm = o.getClass().getMethod(m);
                Object v = mm.invoke(o);
                if (v instanceof Number n) return n.intValue();
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private Boolean getBoolViaReflection(Object o, String... methods) {
        for (String m : methods) {
            try {
                var mm = o.getClass().getMethod(m);
                Object v = mm.invoke(o);
                if (v instanceof Boolean b) return b;
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private String getStringViaReflection(Object o, String... methods) {
        for (String m : methods) {
            try {
                var mm = o.getClass().getMethod(m);
                Object v = mm.invoke(o);
                if (v != null) return String.valueOf(v);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    // -------- Volcado a etiquetas (robusto) --------
    private void actualizarLabelsUbicacionRobusta(Palet p) {
        if (p == null) {
            estanteria_actual_Label.setText("-");
            balda_actual_Label.setText("-");
            posicion_actual_Label.setText("-");
            delante_actual_Label.setText("-");
            identificador_producto.setText("-");
            identificador_tipo.setText("-");
            LOGGER.fine("No hay palet seleccionado; etiquetas reiniciadas (reflexión).");
            return;
        }

        Integer est = getIntViaReflection(p, "getEstanteria", "getNumEstanteria", "getRack");
        Integer bal = getIntViaReflection(p, "getBalda", "getShelf", "getNumBalda");
        Integer pos = getIntViaReflection(p, "getPosicion", "getSlot", "getNumPosicion");
        Boolean del = getBoolViaReflection(p, "isDelante", "getDelante", "isFront");
        String idProd = getStringViaReflection(p, "getIdProducto", "getProductoId", "getProducto", "getIdentificador");
        String tipo = getStringViaReflection(p, "getIdTipo", "getTipoProducto", "getProductoTipo", "getIdentificadorTipo");

        for (Producto pro : Almacen.TodosProductos) {
            if (pro.getIdentificadorProducto().equals(idProd)) {
                tipo = pro.getIdTipo();
            }
        }

        estanteria_actual_Label.setText(est != null ? String.valueOf(est) : "-");
        balda_actual_Label.setText(bal != null ? String.valueOf(bal) : "-");
        posicion_actual_Label.setText(pos != null ? String.valueOf(pos) : "-");
        delante_actual_Label.setText(del != null ? (del ? "Sí" : "No") : "-");
        identificador_producto.setText(idProd != null ? idProd : "-");
        identificador_tipo.setText(tipo != null ? tipo : "-");

        String finalTipo = tipo;
        LOGGER.info(() -> "Ubicación palet " + p.getIdPalet() +
                " -> est:" + (est != null ? est : "?") +
                ", balda:" + (bal != null ? bal : "?") +
                ", pos:" + (pos != null ? pos : "?") +
                ", delante:" + (del != null ? del : "?") +
                ", prod:" + (idProd != null ? idProd : "?") +
                ", tipo:" + (finalTipo != null ? finalTipo : "?"));
    }

}
