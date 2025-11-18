package uvigo.tfgalmacen.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import uvigo.tfgalmacen.almacenManagement.*;
import uvigo.tfgalmacen.utils.ClipboardUtils;

import java.net.URL;
import java.util.*;

import static uvigo.tfgalmacen.utils.ClipboardUtils.copyLabelText;

/**
 * Controlador de la vista del almacén en 3D.
 * Gestiona la creación de la escena, carga de datos, cámara y eventos de usuario.
 */
public class almacenController implements Initializable {

    // Grupos 3D para organizar visualmente ejes, palets y baldas
    private final Group grupo3D = new Group();
    private final Group grupoEjes = new Group();
    private final Group[] gruposPalets = {new Group(), new Group(), new Group(), new Group()};
    private final Group[] gruposBaldas = {new Group(), new Group(), new Group(), new Group()};

    // Cámara y estado del ratón
    private PerspectiveCamera camara;

    public final ArrayList<String> todosLosProductos = new ArrayList<>();


    // número total de estanterías (usa tu fuente real)
    private final int NUM_ESTANTERIAS = Almacen.NUM_ESTANTERIAS;

    // Context menu para filtrar estanterías
    private final ContextMenu estanteriasMenu = new ContextMenu();

    // Estanterías que se muestran actualmente (ej: 1,2,3,...)
    private final Set<Integer> estanteriasVisibles = new HashSet<>();

    // Mapa de id_palet -> nodo 3D (ajusta al tuyo)
    private final Map<Integer, List<Node>> nodoPaletPorId = new HashMap<>();
    private final Map<Integer, List<Node>> baldasPorEstanteria = new HashMap<>();


    @FXML
    private AnchorPane almacenContainer;

    @FXML
    private ComboBox<String> comboProductoAlmacen;

    @FXML
    private ComboBox<String> comboTipoAlmacen;


    @FXML
    private Label contenidoLabel;

    @FXML
    private Label idPaletLabel;
    @FXML
    private Label tipoProductoLabel;

    @FXML
    private Label nombreProductoLabel;

    @FXML
    private Label estanteriaLabel;

    @FXML
    private Label baldaLabel;

    @FXML
    private Label posicionLabel;

    @FXML
    private Label delanteLabel;

    @FXML
    private Button copy_idpalet_btn;

    @FXML
    private Button copy_producto_btn;

    @FXML
    private Button copy_tipo_producto_btn;


    /**
     * Inicialización automática de JavaFX cuando se carga el controlador.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarVistaAlmacen();

        copy_idpalet_btn.setOnMouseClicked(_ -> copyLabelText(copy_idpalet_btn, idPaletLabel));
        copy_producto_btn.setOnMouseClicked(_ -> copyLabelText(copy_producto_btn, nombreProductoLabel));
        copy_tipo_producto_btn.setOnMouseClicked(_ -> copyLabelText(copy_tipo_producto_btn, tipoProductoLabel));

        initContextMenuEstanterias();
        hookContextMenuEnVista3D();

    }

    private void initContextMenuEstanterias() {
        estanteriasMenu.getItems().clear();
        estanteriasVisibles.clear();

        for (int est = 1; est <= NUM_ESTANTERIAS; est++) {
            CheckMenuItem item = new CheckMenuItem("Estantería " + est);
            item.setUserData(est);
            item.setSelected(true);                 // por defecto todas visibles
            estanteriasVisibles.add(est);

            item.selectedProperty().addListener((obs, wasSel, isSel) -> {
                int idEst = (Integer) item.getUserData();
                if (isSel) {
                    estanteriasVisibles.add(idEst);
                } else {
                    estanteriasVisibles.remove(idEst);
                }
                aplicarFiltros3D();
            });

            estanteriasMenu.getItems().add(item);
        }
    }


    private void hookContextMenuEnVista3D() {
        // Usa el nodo donde quieres abrir el menú (SubScene, AnchorPane, etc.)
        Node target = subEscena; // cámbialo si tu nodo se llama distinto

        target.setOnContextMenuRequested(evt -> {
            estanteriasMenu.show(target, evt.getScreenX(), evt.getScreenY());
            evt.consume();
        });
    }

    private void aplicarFiltros3D() {
        // Primero palets y productos
        String productoSeleccionado = comboProductoAlmacen.getValue();

        for (Palet p : Almacen.TodosPalets) {
            List<Node> nodos = nodoPaletPorId.get(p.getIdPalet());
            if (nodos == null || nodos.size() < 2) continue;

            Node palet = nodos.get(0);
            Node producto = nodos.get(1);

            boolean visiblePorEstanteria = estanteriasVisibles.contains(p.getEstanteria());
            //&& (p.getProducto().getIdTipo().equals(comboTipoAlmacen.getValue()));
            //&& (!p.getProducto().getIdentificadorProducto().equals(comboTipoAlmacen.getValue()) || comboTipoAlmacen.getValue().equals("Todos"));

            boolean visiblePorTipo = comboTipoAlmacen.getValue().equals("Todos") ||
                    p.getProducto().getIdTipo().equals(comboTipoAlmacen.getValue());

            boolean visiblePorProducto = comboProductoAlmacen.getValue().equals("Todos") ||
                    p.getProducto().getIdentificadorProducto().equals(comboProductoAlmacen.getValue());


            boolean visiblePorOtrosFiltros = !cumpleFiltrosAdicionales(p);


            boolean visible = visiblePorEstanteria && visiblePorTipo && visiblePorProducto;

            palet.setVisible(visible);
            producto.setVisible(visible);
        }

        // Después baldas, una sola vez por estantería
        for (var entry : baldasPorEstanteria.entrySet()) {
            int estanteria = entry.getKey();
            boolean visibleEst = estanteriasVisibles.contains(Almacen.NUM_ESTANTERIAS + 1 - estanteria);

            for (Node balda : entry.getValue()) {
                balda.setVisible(visibleEst);
            }
        }
    }

    /**
     * Aquí puedes meter el filtro por producto / cliente que ya uses
     * en tus ComboBox. De momento devuelvo true para no romper nada.
     */
    private boolean cumpleFiltrosAdicionales(Palet p) {
        // Ejemplo (si tienes comboProducto, comboCliente, etc.)
        // return filtroProductoOk(p) && filtroClienteOk(p) && filtroEstadoOk(p);

        return true;
    }


    SubScene subEscena;

    /**
     * Carga y muestra el almacén en la vista.
     * Se recrea desde cero cada vez que se entra a esta escena.
     */
    private void cargarVistaAlmacen() {
        //Almacen almacen = new Almacen("almacen.xml");
        Almacen almacen = new Almacen();
        almacen.GenerarAlmacen();

        // Se crea una instancia nueva que contiene la SubScene con todo renderizado
        subEscena = this.crearVistaAlmacen(almacen);

        subEscena.setOnMouseEntered(_ -> subEscena.requestFocus());

        // Envuelve la SubScene para que se redimensione automáticamente
        BorderPane content = new BorderPane();
        content.setCenter(subEscena);
        subEscena.widthProperty().bind(content.widthProperty());
        subEscena.heightProperty().bind(content.heightProperty());

        // Limpia y reemplaza el contenido anterior
        almacenContainer.getChildren().clear();
        almacenContainer.getChildren().add(content);
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setLeftAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);


        ArrayList<String> opcionesComboBoxTipo = new ArrayList<>();
        opcionesComboBoxTipo.add("Todos");
        for (Tipo tipo : Almacen.TodosTipos) {
            opcionesComboBoxTipo.add(tipo.getIdTipo());
        }
        comboTipoAlmacen.setItems(FXCollections.observableArrayList(opcionesComboBoxTipo));
        comboTipoAlmacen.setValue("Todos");


        todosLosProductos.add("Todos");
        for (Producto producto : Almacen.TodosProductos) {
            todosLosProductos.add(producto.getIdentificadorProducto());
        }
        comboProductoAlmacen.setItems(FXCollections.observableArrayList(todosLosProductos));
        comboProductoAlmacen.setValue("Todos");

    }

    /**
     * Crea la vista del almacén 3D a partir del objeto `Almacen`.
     */
    public SubScene crearVistaAlmacen(Almacen almacen) {
        grupo3D.getChildren().add(grupoEjes);
        showAxis(true);  // Mostrar ejes X, Y, Z

        // Crear baldas y añadir palets a cada una
        for (int k = 0; k < 4; k++) {

            List<Node> baldasEst = baldasPorEstanteria.computeIfAbsent(k + 1, _ -> new ArrayList<>());
            //int offsetEstanteria = (k >= 2) ? 3700 : 0;
            for (int i = 0; i < 8; i++) {
                //Box balda = MisElementoGraficos.CreaParalelepipedo(3600, 100, 36300, 0, -6300 * k + offsetEstanteria, 2000 * i, Color.web("#2F3A32"));
                Box balda = MisElementoGraficos.CreaParalelepipedo(3350, 100, 36300, 0, -6300 * k, 2000 * i, Color.web("#2F3A32"));
                if (gruposBaldas[k].getChildren().size() <= 7) gruposBaldas[k].getChildren().add(balda);
                baldasEst.add(balda);
                // Agrega palets adelante y atrás
                for (int j = 0; j < 24; j++) {
                    for (boolean esDelante : new boolean[]{true, false}) {
                        Palet palet = almacen.getPalet(k + 1, i + 1, j + 1, esDelante);
                        if (palet != null) {
                            Box paletBox = palet.CreaPalet();
                            Box productoBox = palet.CreaProducto();
                            //System.out.println(palet.getColorProducto());
                            palet.setPaletBox(paletBox);
                            palet.setProductBox(productoBox);
                            gruposPalets[k].getChildren().addAll(paletBox, productoBox);


                            nodoPaletPorId.put(palet.getIdPalet(), List.of(paletBox, productoBox));


                        }
                    }
                }
            }
        }

        // Añadir baldas y palets al grupo principal
        for (int i = 0; i < 4; i++) {
            grupo3D.getChildren().addAll(gruposBaldas[i], gruposPalets[i]);
        }

        //grupo3D.getTransforms().add(new Translate(-1500, 1000, 3000));

        // Configurar cámara 3D
        camara = new PerspectiveCamera(false);
        camara.setNearClip(0.1);
        camara.setFarClip(300000.0);
        camara.getTransforms().addAll(
                new Rotate(160, Rotate.X_AXIS),
                new Rotate(-30, Rotate.Y_AXIS),
                new Rotate(-10, Rotate.Z_AXIS),
                new Translate(-500, -9500, -45000)
        );

        // Crear SubScene con anti-aliasing para suavizar bordes
        SubScene subEscena3D = new SubScene(grupo3D, 0, 0, true, SceneAntialiasing.BALANCED);
        subEscena3D.setCamera(camara);
        subEscena3D.setFill(Color.web("#EEE"));

        // Añadir luces
        grupo3D.getChildren().addAll(
                new PointLight(new Color(0.6, 0.6, 0.6, 1)) {{
                    setTranslateX(10000);
                    setTranslateY(20000);
                    setTranslateZ(30000);
                }},
                new AmbientLight(new Color(0.3, 0.3, 0.3, 1))
        );

        // Configurar eventos de interacción
        configurarEventos(subEscena3D);


        // Añadir múltiples luces para cubrir desde varios ángulos

        int translate = 2;
        PointLight luz1 = new PointLight(Color.web("#2F3A32"));
        luz1.setTranslateX(10000 * translate);
        luz1.setTranslateY(20000 * translate);
        luz1.setTranslateZ(30000 * translate);

        PointLight luz2 = new PointLight(Color.web("#2F3A32"));
        luz2.setTranslateX(-10000 * translate);
        luz2.setTranslateY(20000 * translate);
        luz2.setTranslateZ(-30000 * translate);

        PointLight luz3 = new PointLight(Color.web("#2F3A32"));
        luz3.setTranslateX(0);
        luz3.setTranslateY(-20000 * translate);
        luz3.setTranslateZ(0);

        PointLight luz4 = new PointLight(Color.web("#2F3A32"));
        luz4.setTranslateX(0);
        luz4.setTranslateY(30000 * translate);
        luz4.setTranslateZ(0);

        //AmbientLight luzAmbiente = new AmbientLight(new Color(0.4, 0.4, 0.4, 1));

        // Añadir luces al grupo 3D
        grupo3D.getChildren().addAll(luz1, luz2, luz3, luz4);

        return subEscena3D;
    }

    /**
     * Reinicia la cámara y la rotación del grupo 3D a su estado inicial.
     */
    private void reiniciarCamara() {
        camara.getTransforms().clear();
        camara.getTransforms().addAll(
                new Rotate(160, Rotate.X_AXIS),
                new Rotate(-30, Rotate.Y_AXIS),
                new Rotate(-10, Rotate.Z_AXIS),
                new Translate(-2500, -9500, -45000));
        // Añadir múltiples luces para cubrir desde varios ángulos


        // Restablecer rotación del grupo 3D (almacén)
        grupo3D.getTransforms().clear(); // Quita todas las rotaciones previas
        grupo3D.getChildren().clear();   // Limpiamos para evitar elementos duplicados
        grupo3D.getChildren().add(grupoEjes); // Añadimos de nuevo los ejes

        // Añadir baldas y palets otra vez al grupo principal
        for (int i = 0; i < 4; i++) {
            grupo3D.getChildren().addAll(gruposBaldas[i], gruposPalets[i]);
        }

        // Añadir luces otra vez si es necesario
        grupo3D.getChildren().addAll(
                new PointLight(new Color(0.6, 0.6, 0.6, 1)) {{
                    setTranslateX(10000);
                    setTranslateY(20000);
                    setTranslateZ(30000);
                }},
                new AmbientLight(new Color(0.3, 0.3, 0.3, 1))
        );
        //grupo3D.getTransforms().add(new Translate(-1500, 1000, 3000));


        // Añadir múltiples luces para cubrir desde varios ángulos
        PointLight luz1 = new PointLight(Color.DARKGRAY);
        luz1.setTranslateX(10000);
        luz1.setTranslateY(20000);
        luz1.setTranslateZ(30000);

        PointLight luz2 = new PointLight(Color.DARKGRAY);
        luz2.setTranslateX(-10000);
        luz2.setTranslateY(20000);
        luz2.setTranslateZ(-30000);

        PointLight luz3 = new PointLight(Color.DARKGRAY);
        luz3.setTranslateX(0);
        luz3.setTranslateY(-20000);
        luz3.setTranslateZ(0);

        PointLight luz4 = new PointLight(Color.DARKGRAY);
        luz4.setTranslateX(0);
        luz4.setTranslateY(30000);
        luz4.setTranslateZ(0);

        //AmbientLight luzAmbiente = new AmbientLight(new Color(0.4, 0.4, 0.4, 1));

        // Añadir luces al grupo 3D
        grupo3D.getChildren().addAll(luz1, luz2, luz3, luz4);

    }

    private void configurarEventos(SubScene escena) {
        configurarEventosTeclado(escena);
        configurarEventosScroll(escena);
        configurarEventosMouseClick(escena);
        configurarEventosMousePress(escena);
        configurarComboTipo();
        configurarComboProducto();
    }

    private void configurarEventosTeclado(SubScene escena) {
        escena.setOnKeyPressed(event -> {
            double desplazamiento = event.isShiftDown() ? 60 : 200;
            Translate tr = switch (event.getCode()) {
                case S, DOWN -> new Translate(0, -desplazamiento, 0);
                case W, UP, C -> new Translate(0, desplazamiento, 0);
                case A, LEFT -> new Translate(-desplazamiento, 0, 0);
                case D, RIGHT -> new Translate(desplazamiento, 0, 0);
                case PLUS, EQUALS -> new Translate(0, 0, 500);  // Zoom in
                case MINUS -> new Translate(0, 0, -500);        // Zoom out
                case R -> {
                    reiniciarCamara();
                    yield null;
                }
                default -> null;
            };
            if (tr != null) camara.getTransforms().add(tr);
        });
    }

    private void configurarEventosScroll(SubScene escena) {
        escena.setOnScroll(event -> {
            if (event.isControlDown()) {
                double zoomAmount = event.getDeltaY() > 0 ? 500 : -500;
                camara.getTransforms().add(new Translate(0, 0, zoomAmount));
            } else {
                double factor = 0.06;
                double roll = grupo3D.getRotate() + event.getDeltaY() * factor;
                grupo3D.getTransforms().add(new Rotate(
                        roll,
                        -(3600 * 4 + 3700 * 2) / 2.0,
                        18150,
                        -36300 / 2.0,
                        Rotate.Y_AXIS
                ));
            }
        });
    }

    private void configurarEventosMousePress(SubScene escena) {
        escena.setOnMousePressed(_ -> {
        });
    }

    private void configurarEventosMouseClick(SubScene escena) {
        escena.setOnMouseClicked(event -> {
            Node node = event.getPickResult().getIntersectedNode();
            if (node instanceof Box) {
                for (Palet palet : Almacen.TodosPalets) {
                    if (palet.getProductBox() == node) {
                        idPaletLabel.setText(String.valueOf(palet.getIdPalet()));
                        nombreProductoLabel.setText(palet.getIdProducto());
                        contenidoLabel.setText(palet.getCantidadProducto() + "L");
                        tipoProductoLabel.setText(palet.getIdTipo());

                        estanteriaLabel.setText(String.valueOf(palet.getEstanteria()));
                        baldaLabel.setText(String.valueOf(palet.getBalda()));
                        posicionLabel.setText(String.valueOf(palet.getPosicion()));
                        delanteLabel.setText(palet.isDelante() ? "Delante" : "Detrás");
                        break;
                    }
                }
            }
        });
    }

    private void configurarComboTipo() {
        comboTipoAlmacen.setOnAction(_ -> {
            String tipoSeleccionado = comboTipoAlmacen.getSelectionModel().getSelectedItem();

            if (!tipoSeleccionado.equals("Todos")) {
                actualizarComboProductoFiltrado(tipoSeleccionado);
                actualizarVisibilidadPorTipo(tipoSeleccionado);
            } else {
                mostrarTodosProductos();
                mostrarTodosPalets();
            }
        });
    }

    private void actualizarComboProductoFiltrado(String tipoSeleccionado) {
        List<String> productosFiltrados = new ArrayList<>();
        productosFiltrados.add("Todos");

        for (Producto producto : Almacen.TodosProductos) {
            if (producto.getIdTipo().equals(tipoSeleccionado)) {
                productosFiltrados.add(producto.getIdentificadorProducto());
            }
        }

        comboProductoAlmacen.setItems(FXCollections.observableArrayList(productosFiltrados));
        comboProductoAlmacen.setValue(productosFiltrados.getFirst());
    }

    private void actualizarVisibilidadPorTipo(String tipoSeleccionado) {
        for (Palet palet : Almacen.TodosPalets) {
            boolean visible = palet.getIdTipo().equals(tipoSeleccionado) && estanteriasVisibles.contains(palet.getEstanteria());
            palet.getProductBox().setVisible(visible);
            palet.getPaletBox().setVisible(visible);
        }
    }

    private void mostrarTodosProductos() {
        todosLosProductos.clear();

        for (Producto producto : Almacen.TodosProductos) {
            todosLosProductos.add(producto.getIdentificadorProducto());
        }

        todosLosProductos.add("Todos");
        comboProductoAlmacen.setItems(FXCollections.observableArrayList(todosLosProductos));
        comboProductoAlmacen.setValue("Todos");
    }

    private void mostrarTodosPalets() {
        for (Palet palet : Almacen.TodosPalets) {
            boolean visible = estanteriasVisibles.contains(palet.getEstanteria());
            palet.getProductBox().setVisible(visible);
            palet.getPaletBox().setVisible(visible);
        }
    }


    private void configurarComboProducto() {
        comboProductoAlmacen.setOnAction(_ -> {
            try {
                String productoSeleccionado = comboProductoAlmacen.getSelectionModel().getSelectedItem();

                if (!productoSeleccionado.equals("Todos")) {
                    for (Producto producto : Almacen.TodosProductos) {
                        if (producto.getIdentificadorProducto().equals(productoSeleccionado)) break;
                    }

                    for (Palet palet : Almacen.TodosPalets) {
                        boolean visible = palet.getIdProducto().equals(productoSeleccionado) && estanteriasVisibles.contains(palet.getEstanteria());
                        palet.getProductBox().setVisible(visible);
                        palet.getPaletBox().setVisible(visible);
                    }
                } else {
                    String tipoSeleccionado = comboTipoAlmacen.getSelectionModel().getSelectedItem();
                    for (Palet palet : Almacen.TodosPalets) {
                        boolean visible = palet.getIdTipo().equals(tipoSeleccionado) && estanteriasVisibles.contains(palet.getEstanteria());
                        palet.getProductBox().setVisible(visible);
                        palet.getPaletBox().setVisible(visible);
                    }
                }
            } catch (Exception ignored) {
            }
        });
    }


    /**
     * Dibuja los ejes del espacio 3D si está habilitado.
     */
    public void showAxis(Boolean show) {
        if (show) {
            Box ejeY = new Box(1500, 10, 10);
            ejeY.setTranslateX(750);
            ejeY.setMaterial(new PhongMaterial(Color.YELLOW));

            Box ejeZ = new Box(10, 1500, 10);
            ejeZ.setTranslateY(750);
            ejeZ.setMaterial(new PhongMaterial(Color.GREEN));

            Box ejeX = new Box(10, 10, 1500);
            ejeX.setTranslateZ(750);
            ejeX.setMaterial(new PhongMaterial(Color.BLUE));

            grupoEjes.getChildren().addAll(ejeX, ejeY, ejeZ);
        }
    }
}


