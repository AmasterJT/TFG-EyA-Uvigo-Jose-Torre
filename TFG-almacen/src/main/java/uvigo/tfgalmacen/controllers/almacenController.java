package uvigo.tfgalmacen.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.MisElementoGraficos;
import uvigo.tfgalmacen.almacenManagement.Palet;

import java.net.URL;
import java.util.ResourceBundle;

public class almacenController  implements Initializable {
    private final Group grupo3D = new Group();
    private final Group grupoEjes = new Group();
    private final Group[] gruposPalets = {new Group(), new Group(), new Group(), new Group()};
    private final Group[] gruposBaldas = {new Group(), new Group(), new Group(), new Group()};

    private PerspectiveCamera camara;
    private double ratonX, ratonY;
    private double ratonXVentanaAntes, ratonYVentanaAntes;


    @FXML
    private AnchorPane almacenContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarVistaAlmacen();
    }

    private void cargarVistaAlmacen() {
        // Re-crear almacen cada vez que se entra a esta vista
        Almacen almacen = new Almacen("almacen.xml");
        almacen.GenerarAlmacen();

        almacenController almacen3DController = new almacenController();
        SubScene subEscena = almacen3DController.crearVistaAlmacen(almacenContainer, almacen);
        subEscena.setOnMouseEntered(e -> subEscena.requestFocus());

        // Usa un contenedor intermedio para redimensionar correctamente
        BorderPane content = new BorderPane();
        content.setCenter(subEscena);
        subEscena.widthProperty().bind(content.widthProperty());
        subEscena.heightProperty().bind(content.heightProperty());

        almacenContainer.getChildren().clear(); // Limpia la vista anterior si la hubiera
        almacenContainer.getChildren().add(content);
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setLeftAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);
    }

    public SubScene crearVistaAlmacen(AnchorPane container, Almacen almacen) {
        // Ejes opcionales
        grupo3D.getChildren().add(grupoEjes);
        showAxis(true);

        // Dibujar baldas y palets
        for (int k = 0; k < 4; k++) {
            int offsetEstanteria = (k >= 2) ? 3700 : 0;
            for (int i = 0; i < 8; i++) {
                Box balda = MisElementoGraficos.CreaParalelepipedo(3600, 100, 36300, 0, -6300 * k + offsetEstanteria, 2000 * i, Color.GRAY);
                if (gruposBaldas[k].getChildren().size() <= 7) gruposBaldas[k].getChildren().add(balda);

                for (int j = 0; j < 24; j++) {
                    for (boolean esDelante : new boolean[]{true, false}) {
                        Palet palet = almacen.getPalet(k + 1, i + 1, j + 1, esDelante);
                        if (palet != null) {
                            Box paletBox = palet.CreaPalet();
                            Box productoBox = palet.CreaProducto();
                            palet.setPaletBox(paletBox);
                            palet.setProductBox(productoBox);
                            gruposPalets[k].getChildren().addAll(paletBox, productoBox);
                        }
                    }
                }
            }
        }

        // Agregar todos los grupos
        for (int i = 0; i < 4; i++) {
            grupo3D.getChildren().addAll(gruposBaldas[i], gruposPalets[i]);
        }

        // Cámara y luz
        camara = new PerspectiveCamera(false);
        camara.setNearClip(0.1);
        camara.setFarClip(300000.0);
        camara.getTransforms().addAll(
                new Rotate(160, Rotate.X_AXIS),
                new Rotate(-30, Rotate.Y_AXIS),
                new Rotate(-10, Rotate.Z_AXIS),
                new Translate(-1500, -9500, -40000)
        );

        SubScene subEscena3D = new SubScene(grupo3D, 0, 0, true, SceneAntialiasing.BALANCED);
        subEscena3D.setCamera(camara);
        subEscena3D.setFill(Color.web("#EEE"));

        // Luces
        grupo3D.getChildren().addAll(
                new PointLight(new Color(0.6, 0.6, 0.6, 1)) {{
                    setTranslateX(10000);
                    setTranslateY(20000);
                    setTranslateZ(30000);
                }},
                new AmbientLight(new Color(0.3, 0.3, 0.3, 1))
        );

        // Eventos
        configurarEventos(subEscena3D, almacen);

        return subEscena3D;
    }

    private void reiniciarCamara() {
        camara.getTransforms().clear();
        camara.getTransforms().addAll(
                new Rotate(160, Rotate.X_AXIS),
                new Rotate(-30, Rotate.Y_AXIS),
                new Rotate(-10, Rotate.Z_AXIS),
                new Translate(-1500, -9500, -40000)
        );
    }

    private void configurarEventos(SubScene escena, Almacen almacen) {

        escena.setOnKeyPressed(event -> {
            double desplazamiento = event.isShiftDown() ? 60 : 200;
            double rotacion = 5;
            double factor = 0.06;
            double roll = grupo3D.getRotate() + rotacion * factor;

            Translate tr = null;
            Rotate rot = null;

            switch (event.getCode()) {
                case S, DOWN    -> tr = new Translate(0, -desplazamiento, 0);
                case W, UP      -> tr = new Translate(0, desplazamiento,0);
                case A, LEFT    -> tr = new Translate(-desplazamiento, 0, 0);
                case D, RIGHT   -> tr = new Translate(desplazamiento, 0, 0);
                case C          -> tr = new Translate(0, desplazamiento, 0);
                case R          -> reiniciarCamara();
                case PLUS, EQUALS -> tr = new Translate(0, 0, 500); // Zoom in
                case MINUS        -> tr = new Translate(0, 0, -500); // Zoom out
                default         -> {}
            }

            if (tr != null) camara.getTransforms().add(tr);
        });

        escena.setOnScroll(event -> {
            if (event.isControlDown()) {
                // Zoom con CTRL + scroll
                double zoomAmount = event.getDeltaY() > 0 ? 500 : -500;
                Translate zoom = new Translate(0, 0, zoomAmount);
                camara.getTransforms().add(zoom);
            } else {
                // Rotación normal si no está presionado CTRL
                double factor = 0.06;
                double roll = grupo3D.getRotate() + event.getDeltaY() * factor;
                Rotate rot = new Rotate(
                        roll,
                        (double) -(3600 * 4 + 3700 * 2) / 2,  // centro X
                        18150,                               // centro Y
                        (double) -36300 / 2,                 // centro Z
                        Rotate.Y_AXIS
                );
                grupo3D.getTransforms().add(rot);
            }
        });

        escena.setOnMousePressed(event -> {
            ratonXVentanaAntes = event.getSceneX();
            ratonYVentanaAntes = event.getSceneY();
        });

        escena.setOnMouseDragged(event -> {
            double dx = event.getSceneX() - ratonXVentanaAntes;
            double dy = event.getSceneY() - ratonYVentanaAntes;

            if (event.isPrimaryButtonDown()) {
                camara.getTransforms().addAll(new Rotate(-dy * 0.05, Rotate.X_AXIS));
                camara.getTransforms().addAll(new Rotate(dx * 0.05, Rotate.Y_AXIS));
            }
            ratonXVentanaAntes = event.getSceneX();
            ratonYVentanaAntes = event.getSceneY();


        });

        escena.setOnMouseClicked(event -> {
            Node node = event.getPickResult().getIntersectedNode();
            if (node instanceof Box) {
                for (Palet palet : almacen.TodosPalets) {
                    if (palet.getProductBox() == node) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Información del Palet");
                        alert.setHeaderText("Detalle");
                        alert.setContentText("Palet: " + palet.getIdProducto() + " = " +
                                palet.getCantidadProducto() + "L (" + palet.getEstanteria() + ", " +
                                palet.getBalda() + ", " + palet.getPosicion() + ", " + palet.isDelante() + ")");
                        alert.showAndWait();
                        break;
                    }
                }
            }
        });

    }

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


            grupoEjes.getChildren().add(ejeX);
            grupoEjes.getChildren().add(ejeY);
            grupoEjes.getChildren().add(ejeZ);
        }
    }

}
