package uvigo.tfgalmacen.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import uvigo.tfgalmacen.almacenManagement.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
    private double ratonX, ratonY;
    private double ratonXVentanaAntes, ratonYVentanaAntes;

    @FXML
    private AnchorPane almacenContainer;

    @FXML
    private ComboBox<String> comboProductoAlmacen;

    @FXML
    private ComboBox<String> comboTipoAlmacen;



    @FXML
    private Label contenidoLabel;

    @FXML
    private Label idPalelLabel;
    @FXML
    private Label tipoProductoLabel;

    @FXML
    private AnchorPane infoCardAlmacen;

    @FXML
    private Label nombreProductoLabel;



    /**
     * Inicialización automática de JavaFX cuando se carga el controlador.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarVistaAlmacen();
    }

    /**
     * Carga y muestra el almacén en la vista.
     * Se recrea desde cero cada vez que se entra a esta escena.
     */
    private void cargarVistaAlmacen() {
        //Almacen almacen = new Almacen("almacen.xml");
        Almacen almacen = new Almacen();
        almacen.GenerarAlmacen();

        // Se crea una instancia nueva que contiene la SubScene con todo renderizado
        almacenController almacen3DController = new almacenController();
        SubScene subEscena = this.crearVistaAlmacen(almacenContainer, almacen);

        subEscena.setOnMouseEntered(e -> subEscena.requestFocus());

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
        for (Tipo tipo : almacen.TodosTipos) {
            opcionesComboBoxTipo.add(tipo.getIdTipo());
        }
        comboTipoAlmacen.setItems(FXCollections.observableArrayList(opcionesComboBoxTipo));
        comboTipoAlmacen.setValue("Todos");


        ArrayList<String> todosLosProductos = new ArrayList<>();
        todosLosProductos.add("Todos");
        for (Producto producto : almacen.TodosProductos) {
            todosLosProductos.add(producto.getIdProducto());
        }
        comboProductoAlmacen.setItems(FXCollections.observableArrayList(todosLosProductos));
        comboProductoAlmacen.setValue("Todos");

    }

    /**
     * Crea la vista del almacén 3D a partir del objeto `Almacen`.
     */
    public SubScene crearVistaAlmacen(AnchorPane container, Almacen almacen) {
        grupo3D.getChildren().add(grupoEjes);
        showAxis(true);  // Mostrar ejes X, Y, Z

        // Crear baldas y añadir palets a cada una
        for (int k = 0; k < 4; k++) {
            int offsetEstanteria = (k >= 2) ? 3700 : 0;
            for (int i = 0; i < 8; i++) {
                Box balda = MisElementoGraficos.CreaParalelepipedo(3600, 100, 36300, 0, -6300 * k + offsetEstanteria, 2000 * i, Color.GRAY);
                if (gruposBaldas[k].getChildren().size() <= 7) gruposBaldas[k].getChildren().add(balda);

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
        configurarEventos(subEscena3D, almacen);


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

    /**
     * Configura todos los eventos de teclado, ratón y scroll de la escena 3D.
     */
    private void configurarEventos(SubScene escena, Almacen almacen) {
        // Evento de teclado para mover cámara o hacer zoom
        escena.setOnKeyPressed(event -> {
            double desplazamiento = event.isShiftDown() ? 60 : 200;
            Translate tr = null;

            switch (event.getCode()) {
                case S, DOWN    -> tr = new Translate(0, -desplazamiento, 0);
                case W, UP      -> tr = new Translate(0, desplazamiento, 0);
                case A, LEFT    -> tr = new Translate(-desplazamiento, 0, 0);
                case D, RIGHT   -> tr = new Translate(desplazamiento, 0, 0);
                case C          -> tr = new Translate(0, desplazamiento, 0);
                case R          -> reiniciarCamara();
                case PLUS, EQUALS -> tr = new Translate(0, 0, 500);  // Zoom in
                case MINUS        -> tr = new Translate(0, 0, -500); // Zoom out
                default         -> {}
            }

            if (tr != null) camara.getTransforms().add(tr);
        });

        // Evento de scroll: Zoom si CTRL está presionado, si no, rotación del grupo
        escena.setOnScroll(event -> {
            if (event.isControlDown()) {
                double zoomAmount = event.getDeltaY() > 0 ? 500 : -500;
                Translate zoom = new Translate(0, 0, zoomAmount);
                camara.getTransforms().add(zoom);
            } else {
                double factor = 0.06;
                double roll = grupo3D.getRotate() + event.getDeltaY() * factor;
                Rotate rot = new Rotate(
                        roll,
                        (double) -(3600 * 4 + 3700 * 2) / 2,
                        18150,
                        (double) -36300 / 2,
                        Rotate.Y_AXIS
                );
                grupo3D.getTransforms().add(rot);
            }
        });

        // Guardar posición del ratón al presionar
        escena.setOnMousePressed(event -> {
            ratonXVentanaAntes = event.getSceneX();
            ratonYVentanaAntes = event.getSceneY();
        });

        // Rotación de cámara con arrastre del ratón
        /*escena.setOnMouseDragged(event -> {
            double dx = event.getSceneX() - ratonXVentanaAntes;
            double dy = event.getSceneY() - ratonYVentanaAntes;

            if (event.isPrimaryButtonDown()) {
                camara.getTransforms().addAll(new Rotate(-dy * 0.05, Rotate.X_AXIS));
                camara.getTransforms().addAll(new Rotate(dx * 0.05, Rotate.Y_AXIS));
            }
            ratonXVentanaAntes = event.getSceneX();
            ratonYVentanaAntes = event.getSceneY();
        });*/

        // Mostrar información de un palet al hacer clic sobre él
        escena.setOnMouseClicked(event -> {
            Node node = event.getPickResult().getIntersectedNode();
            if (node instanceof Box) {
                for (Palet palet : almacen.TodosPalets) {
                    if (palet.getProductBox() == node) {
                        /*Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Información del Palet");
                        alert.setHeaderText("Detalle");
                        alert.setContentText("Palet: " + palet.getIdProducto() + " = " +
                                palet.getCantidadProducto() + "L (" + palet.getEstanteria() + ", " +
                                palet.getBalda() + ", " + palet.getPosicion() + ", " + palet.isDelante() + ")");
                        alert.showAndWait();
                        */

                        idPalelLabel.setText(String.valueOf(palet.getIdPalet()));
                        nombreProductoLabel.setText(palet.getIdProducto());
                        contenidoLabel.setText(palet.getCantidadProducto() + "L");
                        tipoProductoLabel.setText(palet.getIdTipo());


                        break;
                    }
                }
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
