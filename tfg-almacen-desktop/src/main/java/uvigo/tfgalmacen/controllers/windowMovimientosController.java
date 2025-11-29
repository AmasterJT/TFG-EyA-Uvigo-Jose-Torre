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
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.Palet;
import uvigo.tfgalmacen.almacenManagement.Producto;
import uvigo.tfgalmacen.almacenManagement.Tipo;
import uvigo.tfgalmacen.database.PaletDAO;
import uvigo.tfgalmacen.utils.ColorFormatter;
import uvigo.tfgalmacen.utils.ComboFilters;

import static uvigo.tfgalmacen.utils.windowComponentAndFuncionalty.*;

import uvigo.tfgalmacen.database.ProductoDAO;


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

    public final ArrayList<String> todosLosProductos = new ArrayList<>();


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
    private Button eliminar_palet_btn;

    @FXML
    private Button generate_random_id_btn;

    @FXML
    private ComboBox<Integer> combo_crear_balda;

    @FXML
    private CheckBox combo_crear_check;

    @FXML
    private ComboBox<Integer> combo_crear_estanteria;

    @FXML
    private ComboBox<Integer> combo_crear_posicion;

    @FXML
    private ComboBox<String> combo_crear_producto;

    @FXML
    private ComboBox<String> combo_crear_tipo;

    @FXML
    private Button crear_palet_btn;


    @FXML
    private TextField alto_crear_palet_text;

    @FXML
    private TextField ancho_crear_palet_text;

    @FXML
    private TextField profundo_crear_palet_text;

    @FXML
    private TextField cantidad_crear_palet_text;


    @FXML
    private TextField id_crear_palet_text;


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
        inicializarComboBoxesCrearPalet();

        // >>> FILTRO TIPO -> PRODUCTO <<<
        combo_crear_tipo.valueProperty().addListener((obs, oldTipo, nuevoTipo) -> {
            filtrarProductosPorTipo(nuevoTipo);
        });

        combo_crear_producto.setOnAction(_ -> actualizarDatosProductoDefault());

        combo_seleccionar_palet.valueProperty().addListener((obs, oldP, newP) -> actualizarLabelsUbicacionRobusta(newP));
        mover_palet_btn.setOnMouseClicked(_ -> aplicarMovimientoPalet());

        alto_crear_palet_text.setTextFormatter(numericFormatter());
        ancho_crear_palet_text.setTextFormatter(numericFormatter());
        profundo_crear_palet_text.setTextFormatter(numericFormatter());
        cantidad_crear_palet_text.setTextFormatter(numericFormatter());
        id_crear_palet_text.setTextFormatter(numericFormatter());
    }

    private void inicializarComboBoxesCrearPalet() {

        ObservableList<Integer> estanterias = FXCollections.observableArrayList();
        for (int i = 1; i <= NUM_ESTANTERIAS; i++) estanterias.add(i);

        ObservableList<Integer> baldas = FXCollections.observableArrayList();
        for (int i = 1; i <= NUM_BALDAS_PER_ESTANTERIA; i++) baldas.add(i);

        ObservableList<Integer> posiciones = FXCollections.observableArrayList();
        for (int i = 1; i <= POSICIONES_PER_BALDA; i++) posiciones.add(i);

        // Hacerlos filtrables por lo que se teclee (p.ej. “23” -> mostrará 23, 123, 230, etc.)
        ComboFilters.makeFilterable(combo_crear_estanteria, estanterias, Object::toString);
        ComboFilters.makeFilterable(combo_crear_balda, baldas, Object::toString);
        ComboFilters.makeFilterable(combo_crear_posicion, posiciones, Object::toString);

        // Prompt y selección inicial (opcional)
        combo_crear_estanteria.setPromptText("-");
        combo_crear_balda.setPromptText("-");
        combo_crear_posicion.setPromptText("-");

        combo_crear_estanteria.getSelectionModel().clearSelection();
        combo_crear_balda.getSelectionModel().clearSelection();
        combo_crear_posicion.getSelectionModel().clearSelection();


        ArrayList<String> opcionesComboBoxTipo = new ArrayList<>();
        opcionesComboBoxTipo.add("Todos");
        for (Tipo tipo : Almacen.TodosTipos) {
            opcionesComboBoxTipo.add(tipo.getIdTipo());
        }
        combo_crear_tipo.setItems(FXCollections.observableArrayList(opcionesComboBoxTipo));
        combo_crear_tipo.setValue("Todos");

        // En vez de rellenar aquí todosLosProductos manualmente,
        // llamamos a la función de filtrado con "Todos":
        filtrarProductosPorTipo("Todos");
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

    /**
     * Filtra los productos del combo_crear_producto en función del tipo seleccionado.
     * Si se selecciona "Todos" o null, se muestran todos los productos.
     */
    private void filtrarProductosPorTipo(String tipoSeleccionado) {
        ObservableList<String> filtrados = FXCollections.observableArrayList();
        filtrados.add("Todos");

        if (tipoSeleccionado == null || "Todos".equals(tipoSeleccionado)) {
            // Mostrar todos los productos
            for (Producto producto : Almacen.TodosProductos) {
                filtrados.add(producto.getIdentificadorProducto());
            }
        } else {
            // Solo productos cuyo tipo coincide con el seleccionado
            for (Producto producto : Almacen.TodosProductos) {
                // Producto tiene getIdTipo() (ya lo usas en actualizarLabelsUbicacionRobusta)
                if (tipoSeleccionado.equals(producto.getIdTipo())) {
                    filtrados.add(producto.getIdentificadorProducto());
                }
            }
        }

        // Mantener selección si sigue siendo válida
        String seleccionadoAntes = combo_crear_producto.getValue();

        combo_crear_producto.setItems(filtrados);

        if (seleccionadoAntes != null && filtrados.contains(seleccionadoAntes)) {
            combo_crear_producto.setValue(seleccionadoAntes);
        } else {
            combo_crear_producto.setValue("Todos");
        }
    }


    private void actualizarDatosProductoDefault() {
        // 1) Obtener identificador del producto seleccionado (String)
        String identificadorProd = combo_crear_producto.getValue();

        // Si es "Todos" o nada, limpiamos labels y salimos
        if (identificadorProd == null || identificadorProd.isBlank() || "Todos".equals(identificadorProd)) {
            alto_crear_palet_text.setText("-");
            ancho_crear_palet_text.setText("-");
            profundo_crear_palet_text.setText("-");
            cantidad_crear_palet_text.clear();
            return;
        }

        // 2) Buscar el objeto Producto correspondiente en Almacen.TodosProductos
        Producto producto = Almacen.TodosProductos.stream()
                .filter(p -> identificadorProd.equals(p.getIdentificadorProducto()))
                .findFirst()
                .orElse(null);

        if (producto == null) {
            LOGGER.warning("No se encontró Producto en memoria para identificador_producto=" + identificadorProd);
            alto_crear_palet_text.setText("-");
            ancho_crear_palet_text.setText("-");
            profundo_crear_palet_text.setText("-");
            cantidad_crear_palet_text.clear();
            return;
        }

        int idProducto = producto.getIndex_BDD(); // asumiendo que tu clase Producto tiene getIdProducto()

        // 3) Consultar en la BDD los datos por defecto de proveedor_producto
        ProductoDAO.DefaultProductoData data =
                ProductoDAO.getDefaultData(Main.connection, idProducto);

        if (data == null) {
            LOGGER.warning("No se encontraron datos por defecto en proveedor_producto para id_producto=" + idProducto);
            alto_crear_palet_text.setText("-");
            ancho_crear_palet_text.setText("-");
            profundo_crear_palet_text.setText("-");
            cantidad_crear_palet_text.clear();
            return;
        }

        // 4) Volcar a la UI
        alto_crear_palet_text.setText(String.valueOf(data.alto()));
        ancho_crear_palet_text.setText(String.valueOf(data.ancho()));
        profundo_crear_palet_text.setText(String.valueOf(data.largo()));
        cantidad_crear_palet_text.setText(String.valueOf(data.unidadesPorPaletDefault()));

        LOGGER.info(() -> String.format(
                "Datos default para producto %s (id=%d) -> alto=%d, ancho=%d, largo=%d, unidades=%d",
                identificadorProd, idProducto,
                data.alto(), data.ancho(), data.largo(), data.unidadesPorPaletDefault()
        ));
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

        ventana_success("Operación completada", "Palet movido a la nueva posición", "Los cambios se han guardado correctamente.");

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

        crear_palet_btn.setOnAction(_ -> {
            try {
                crearPalet();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        generate_random_id_btn.setOnAction(_ -> putRandomIdInTextField(id_crear_palet_text));
        generate_random_id_btn.setTooltip(new Tooltip("Generar ID"));
    }

    private int randomIntInRange() {
        return ThreadLocalRandom.current().nextInt(1, 99999999 + 1);
    }

    private void putRandomIdInTextField(TextField idCrearPaletText) {

        int id;

        // Busca un id libre (por si acaso se encuentra alguno ya usado)
        do {
            id = randomIntInRange();
        } while (PaletDAO.iSidPaletvalido(Main.connection, id));

        idCrearPaletText.setText(String.valueOf(id));
    }

    private void crearPalet() throws SQLException {
        // 1) Leer tipo y producto seleccionados
        String tipo = combo_crear_tipo.getValue();
        String producto = combo_crear_producto.getValue();

        if (tipo == null || "Todos".equals(tipo)) {
            ventana_error("Tipo no válido", "Debes seleccionar un tipo",
                    "Selecciona un tipo concreto para crear el palet.");
            return;
        }

        if (producto == null || "Todos".equals(producto)) {
            ventana_error("Producto no válido", "Debes seleccionar un producto",
                    "Selecciona un producto concreto para crear el palet.");
            return;
        }

        Integer estanteria = combo_crear_estanteria.getValue();
        Integer balda = combo_crear_balda.getValue();
        Integer posicion = combo_crear_posicion.getValue();
        boolean delante = combo_crear_check.isSelected();

        if (estanteria == null || balda == null || posicion == null) {
            ventana_error("Ubicación incompleta",
                    "Debes seleccionar estantería, balda y posición",
                    "Rellena todos los campos de ubicación.");
            return;
        }

        // 2) Resolver objetos Tipo y Producto
        Tipo tipoObjeto = Almacen.TodosTipos.stream()
                .filter(t -> t.getIdTipo().equals(tipo))
                .findFirst()
                .orElse(null);

        Producto productoObjeto = Almacen.TodosProductos.stream()
                .filter(p -> p.getIdentificadorProducto().equals(producto))
                .findFirst()
                .orElse(null);

        if (tipoObjeto == null || productoObjeto == null) {
            ventana_error("Datos inconsistentes",
                    "No se encontró el tipo o el producto seleccionado",
                    "Revisa los datos en memoria vs BDD.");
            return;
        }

        String idTipo = tipoObjeto.getIdTipo();

        // OJO: aquí asumo que index_BDD es el id_producto (INT) de la tabla productos/proveedor_producto
        int idProductoInt = productoObjeto.getIndex_BDD();
        String idProductoCadena = productoObjeto.getIdentificadorProducto(); // para la tabla palets

        // 3) Obtener datos por defecto (alto, ancho, largo, unidades_por_palet_default)
        var data = ProductoDAO.getDefaultData(Main.connection, idProductoInt);
        if (data == null) {
            ventana_error("Error", "Sin datos por defecto",
                    "No hay configuración de alto/ancho/largo/unidades para este producto.");
            return;
        }

        int alto = data.alto();
        int ancho = data.ancho();
        int profundo = data.largo();
        int cantidadDefault = data.unidadesPorPaletDefault();

        // Si el usuario ha escrito cantidad, la usamos; si no, la por defecto
        int cantidad;
        String txtCant = cantidad_crear_palet_text.getText();
        if (txtCant == null || txtCant.isBlank()) {
            cantidad = cantidadDefault;
        } else {
            try {
                cantidad = Integer.parseInt(txtCant.trim());
            } catch (NumberFormatException e) {
                ventana_error("Cantidad no válida",
                        "La cantidad debe ser un número entero",
                        "Corrige la cantidad o deja el campo vacío para usar la predeterminada.");
                return;
            }
        }

        // 4) Comprobar que la ubicación está libre
        boolean posLibre = PaletDAO.isUbicacionLibre(
                Main.connection,
                estanteria,
                balda,
                posicion,
                delante
        );

        if (!posLibre) {
            shake(combo_crear_estanteria, SHAKE_DURATION);
            shake(combo_crear_balda, SHAKE_DURATION);
            shake(combo_crear_posicion, SHAKE_DURATION);
            shake(combo_crear_check, SHAKE_DURATION);

            ventana_error("Ubicación ocupada",
                    "La posición seleccionada ya tiene un palet",
                    "Selecciona otra estantería/balda/posición o quita el palet existente.");
            return;
        }

        // 5) Obtener / generar identificador de palet
        int id;
        String txtId = id_crear_palet_text.getText();

        if (txtId == null || txtId.isBlank()) {
            // No hay id escrito → generamos uno libre
            do {
                id = randomIntInRange();
            } while (PaletDAO.iSidPaletvalido(Main.connection, id));

            id_crear_palet_text.setText(String.valueOf(id));
        } else {
            // Hay texto → intentamos usarlo, y si está ocupado, generamos otro
            try {
                id = Integer.parseInt(txtId.trim());
            } catch (NumberFormatException e) {
                // Si no es número, generamos uno nuevo
                do {
                    id = randomIntInRange();
                } while (PaletDAO.iSidPaletvalido(Main.connection, id));

                id_crear_palet_text.setText(String.valueOf(id));
            }

            // Si es número pero está ocupado, generamos otro
            if (PaletDAO.iSidPaletvalido(Main.connection, id)) {
                do {
                    id = randomIntInRange();
                } while (PaletDAO.iSidPaletvalido(Main.connection, id));

                id_crear_palet_text.setText(String.valueOf(id));
            }
        }

        String identificadorPalet = String.valueOf(id);

        // 6) Debug
        System.out.println("Crear palet:");
        System.out.println("Tipo seleccionado: " + tipo + " (ID: " + idTipo + ")");
        System.out.println("Producto seleccionado: " + producto + " (idProductoInt: " + idProductoInt + ")");
        System.out.println("Identificador palet: " + identificadorPalet);
        System.out.println("Estantería: " + estanteria);
        System.out.println("Balda: " + balda);
        System.out.println("Posición: " + posicion);
        System.out.println("Poner delante: " + delante);
        System.out.println("Alto/Ancho/Profundo: " + alto + "/" + ancho + "/" + profundo);
        System.out.println("Cantidad: " + cantidad);

        // 7) Insertar en BDD
        boolean ok = PaletDAO.insertarPalet(
                Main.connection,
                identificadorPalet,
                idProductoCadena, // este es el que referencia productos.identificador_producto (VARCHAR)
                alto,
                ancho,
                profundo,
                cantidad,
                estanteria,
                balda,
                posicion,
                delante
        );

        if (!ok) {
            ventana_error("Error al insertar",
                    "No se pudo insertar el palet",
                    "Revisa los logs para más detalles.");
            return;
        }

        ventana_success("Operación completada",
                "Palet creado correctamente",
                "El palet se ha guardado en la base de datos.");

        Stage stage = (Stage) crear_palet_btn.getScene().getWindow();
        stage.close();
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
        ventana_success("Operación completada",
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
