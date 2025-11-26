package uvigo.tfgalmacen.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.Palet;
import uvigo.tfgalmacen.almacenManagement.Producto;
import uvigo.tfgalmacen.utils.ColorFormatter;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import uvigo.tfgalmacen.database.PaletDAO;

import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.*;

public class windowModificarPaletController implements Initializable {


    private static final Logger LOGGER = Logger.getLogger(windowModificarPaletController.class.getName());


    private static final String PLACEHOLDER_PALET = "Seleccionar palet";

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

    @FXML
    private Button ExitButton;


    @FXML
    private TextField alto_crear_palet_text;

    @FXML
    private TextField ancho_crear_palet_text;

    @FXML
    private Label balda_actual_Label;

    @FXML
    private TextField cantidad_crear_palet_text;

    @FXML
    private ComboBox<Palet> combo_seleccionar_palet;

    @FXML
    private Label delante_actual_Label;

    @FXML
    private Label estanteria_actual_Label;

    @FXML
    private Label identificador_producto;

    @FXML
    private Label identificador_tipo;

    @FXML
    private Button actualizar_palet_btn;

    @FXML
    private Label posicion_actual_Label;

    @FXML
    private TextField profundo_crear_palet_text;

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

        alto_crear_palet_text.setTextFormatter(numericFormatter());
        ancho_crear_palet_text.setTextFormatter(numericFormatter());
        profundo_crear_palet_text.setTextFormatter(numericFormatter());
        cantidad_crear_palet_text.setTextFormatter(numericFormatter());
    }

    private void configurarBotonesVentana() {

        ExitButton.setOnMouseClicked(_ -> {
            Stage stage = (Stage) ExitButton.getScene().getWindow();
            stage.close();

            ArrayList<Palet> TodosPalets = Almacen.TodosPalets;
        });

        actualizar_palet_btn.setOnMouseClicked(_ -> aplicarActualizacionPalet());
    }

    private void aplicarActualizacionPalet() {
        Palet seleccionado = combo_seleccionar_palet.getValue();
        if (seleccionado == null) {
            LOGGER.warning("No hay palet seleccionado para actualizar.");
            new Alert(Alert.AlertType.WARNING, "Debes seleccionar un palet primero.").showAndWait();
            return;
        }

        // Leer y validar los campos numéricos
        String sCantidad = cantidad_crear_palet_text.getText();
        String sAlto = alto_crear_palet_text.getText();
        String sAncho = ancho_crear_palet_text.getText();
        String sLargo = profundo_crear_palet_text.getText();

        int cantidad, alto, ancho, largo;

        try {
            cantidad = Integer.parseInt(sCantidad);
            alto = Integer.parseInt(sAlto);
            ancho = Integer.parseInt(sAncho);
            largo = Integer.parseInt(sLargo);

            if (cantidad <= 0 || alto <= 0 || ancho <= 0 || largo <= 0) {
                throw new NumberFormatException("Valores deben ser > 0");
            }
        } catch (NumberFormatException e) {
            LOGGER.warning("Valores numéricos inválidos al actualizar palet: " + e.getMessage());

            // feedback visual sencillo (puedes mejorar estilos si quieres)
            shake(cantidad_crear_palet_text, SHAKE_DURATION);
            shake(alto_crear_palet_text, SHAKE_DURATION);
            shake(ancho_crear_palet_text, SHAKE_DURATION);
            shake(profundo_crear_palet_text, SHAKE_DURATION);

            new Alert(Alert.AlertType.ERROR,
                    "Revisa los valores numéricos (cantidad, alto, ancho, profundo).").showAndWait();
            return;
        }

        System.out.println("Actualizar palet ID " + seleccionado.getIdPalet() +
                " con cantidad=" + cantidad +
                ", alto=" + alto +
                ", ancho=" + ancho +
                ", largo=" + largo);

        // Llamar al DAO
        boolean ok = PaletDAO.updatePaletDatosBasicos(
                Main.connection,
                seleccionado.getIdPalet(),
                cantidad,
                alto,
                ancho,
                largo
        );

        if (!ok) {
            new Alert(Alert.AlertType.ERROR,
                    "No se pudo actualizar el palet en la base de datos.").showAndWait();
            return;
        }

        // Actualizar objeto en memoria (si tu clase Palet tiene estos setters)
        try {
            seleccionado.setCantidadProducto(cantidad);
            seleccionado.setAlto(alto);
            seleccionado.setAncho(ancho);
            seleccionado.setLargo(largo);
        } catch (Exception ex) {
            // Por si algún setter no existe; no es crítico
            LOGGER.log(Level.FINE, "No se pudieron actualizar todos los campos en el objeto Palet", ex);
        }

        LOGGER.info(() -> "Palet " + seleccionado.getIdPalet() + " actualizado correctamente.");
        new Alert(Alert.AlertType.INFORMATION,
                "Palet actualizado correctamente.").showAndWait();

        // Opcional: cerrar la ventana tras actualizar
        Stage stage = (Stage) actualizar_palet_btn.getScene().getWindow();
        stage.close();
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

        cantidad_crear_palet_text.setText(String.valueOf(p.getCantidadProducto()));
        alto_crear_palet_text.setText(String.valueOf(p.getAlto()));
        ancho_crear_palet_text.setText(String.valueOf(p.getAncho()));
        profundo_crear_palet_text.setText(String.valueOf(p.getLargo()));

        String finalTipo = tipo;
        LOGGER.info(() -> "Ubicación palet " + p.getIdPalet() +
                " -> est:" + (est != null ? est : "?") +
                ", balda:" + (bal != null ? bal : "?") +
                ", pos:" + (pos != null ? pos : "?") +
                ", delante:" + (del != null ? del : "?") +
                ", prod:" + (idProd != null ? idProd : "?") +
                ", tipo:" + (finalTipo != null ? finalTipo : "?"));
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

}
