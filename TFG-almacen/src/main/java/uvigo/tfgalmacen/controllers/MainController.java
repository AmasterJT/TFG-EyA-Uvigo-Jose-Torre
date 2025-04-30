package uvigo.tfgalmacen.controllers;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import javafx.scene.shape.Box;
import javafx.scene.control.Alert.AlertType;
import uvigo.tfgalmacen.almacenManagement.Almacen;
import uvigo.tfgalmacen.almacenManagement.MisElementoGraficos;
import uvigo.tfgalmacen.almacenManagement.Palet;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {


    @FXML
    AnchorPane topBar;

    @FXML
    HBox windowBar;

    @FXML
    private Button ExitButton;

    @FXML
    private Label MenuBackButton;

    @FXML
    private BorderPane BorderPane;

    @FXML
    private Label MenuButton;


    @FXML
    private AnchorPane root;

    @FXML
    AnchorPane Slider;

    @FXML
    private Label welcomeText;

    @FXML
    private AnchorPane almacenContainer;


    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {



        ExitButton.setOnMouseClicked(event -> {
            System.exit(0);
        });
        Slider.setTranslateX(-176);

        MenuButton.setOnMouseClicked(_ -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(Slider);

            slide.setToX(0);
            slide.play();

            Slider.setTranslateX(-176);

            slide.setOnFinished((ActionEvent e)-> {
                MenuButton.setVisible(false);
                MenuBackButton.setVisible(true);
            });
        });

        MenuBackButton.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(Slider);

            slide.setToX(-176);
            slide.play();

            Slider.setTranslateX(0);

            slide.setOnFinished((ActionEvent e)-> {
                MenuButton.setVisible(true);
                MenuBackButton.setVisible(false);
            });
        });



        setupAlmacen3D();

    }


    private final Group grupo3D = new Group();
    private final Group grupoEjes = new Group();

    private final Group grupoPalets1 = new Group();
    private final Group grupoBaldas1 = new Group();
    private final Group grupoPalets2 = new Group();
    private final Group grupoBaldas2 = new Group();
    private final Group grupoPalets3 = new Group();
    private final Group grupoBaldas3 = new Group();
    private final Group grupoPalets4 = new Group();
    private final Group grupoBaldas4 = new Group();

    private PerspectiveCamera camara; // Cámara utilizada en la escena 3D
    private double ratonX, ratonY; // Posición del ratón en la ventana
    private double ratonXVentanaAntes, ratonYVentanaAntes;

    public void seleccionarGrupoBalda(Box Balda) {
        Group[] gruposBaldas = {grupoBaldas4, grupoBaldas3, grupoBaldas2, grupoBaldas1};

        for (Group gruposBalda : gruposBaldas) {
            if (gruposBalda.getChildren().size() <= 7) gruposBalda.getChildren().add(Balda);
        }
    }

    public void seleccionarGrupoPaletProducto(Palet palet) {
        Box paletEstanteria = palet.CreaPalet();
        Box produtoPalet = palet.CreaProducto();
        int estanteria = palet.getEstanteria() - 1;

        palet.setPaletBox(paletEstanteria);
        palet.setProductBox(produtoPalet);

        Group[] gruposPalets = {grupoPalets4, grupoPalets3, grupoPalets2, grupoPalets1};

        gruposPalets[estanteria].getChildren().add(paletEstanteria);
        gruposPalets[estanteria].getChildren().add(produtoPalet);
    }

    public void dibujaAlmacen(Almacen almacen) {
        for (int k = 0; k < 4; k++) {
            int desplazamientoEstanterias = (k >= 2) ? 3700 : 0;
            for (int i = 0; i < 8; i++) {
                seleccionarGrupoBalda(MisElementoGraficos.CreaParalelepipedo(3600, 100, 36300, 0, -6300 * k + desplazamientoEstanterias, (2000) * i, Color.GRAY));
                for (int j = 0; j < 24; j++) {
                    for (boolean esDelante : new boolean[]{true, false}) {

                        Palet palet = almacen.getPalet(k + 1, i + 1, j + 1, esDelante);
                        if (palet != null) {
                            seleccionarGrupoPaletProducto(palet);
                        }
                    }
                }
            }
        }
    }

    public void setAxis(Boolean show) {
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

    private void setupAlmacen3D() {
        Almacen almacen = new Almacen("almacen.xml");
        almacen.GenerarAlmacen(); // Extraemos todos los datos del XML

        // Configurar los ejes
        setAxis(false);

        // Dibujar el almacén
        dibujaAlmacen(almacen);

        // Agregar los grupos de baldas y palets al grupo principal
        grupo3D.getChildren().add(grupoEjes);
        grupo3D.getChildren().add(grupoBaldas1);
        grupo3D.getChildren().add(grupoBaldas2);
        grupo3D.getChildren().add(grupoBaldas3);
        grupo3D.getChildren().add(grupoBaldas4);
        grupo3D.getChildren().add(grupoPalets1);
        grupo3D.getChildren().add(grupoPalets2);
        grupo3D.getChildren().add(grupoPalets3);
        grupo3D.getChildren().add(grupoPalets4);

        // Configurar la SubScene 3D
        SubScene subEscena3D = new SubScene(grupo3D, 0, 0, true, SceneAntialiasing.BALANCED);
        Paint BACKGROUND_COLOR = Color.web("#EEE");
        subEscena3D.setFill(BACKGROUND_COLOR);

        camara = new PerspectiveCamera(false);
        camara.setNearClip(0.1);
        camara.setFarClip(300000.0);
        Rotate rotacionXCamara = new Rotate(160, Rotate.X_AXIS);
        Rotate rotacionYCamara = new Rotate(-30, Rotate.Y_AXIS);
        Rotate rotacionZCamara = new Rotate(-10, Rotate.Z_AXIS);
        Translate traslacionCamara = new Translate(-1500, -9500, -40000);
        camara.getTransforms().addAll(rotacionXCamara, rotacionYCamara, rotacionZCamara, traslacionCamara);
        subEscena3D.setCamera(camara);

        // Configurar las luces
        PointLight luzPuntual = new PointLight(new Color(0.6, 0.6, 0.6, 1));
        luzPuntual.setTranslateX(10000);
        luzPuntual.setTranslateY(20000);
        luzPuntual.setTranslateZ(30000);
        grupo3D.getChildren().add(luzPuntual);
        grupo3D.getChildren().add(new AmbientLight(new Color(0.3, 0.3, 0.3, 1)));


        // Crear el contenido de la ventana
        BorderPane contenidoVentana = new BorderPane();
        contenidoVentana.setCenter(subEscena3D);

        // Hacer que la SubScene se ajuste al tamaño del BorderPane
        subEscena3D.heightProperty().bind(contenidoVentana.heightProperty());
        subEscena3D.widthProperty().bind(contenidoVentana.widthProperty());

        // Agregar el contenido al almacenContainer
        almacenContainer.getChildren().add(contenidoVentana);

        // Ajustar el contenido al tamaño del AnchorPane
        AnchorPane.setTopAnchor(contenidoVentana, 0.0);
        AnchorPane.setRightAnchor(contenidoVentana, 0.0);
        AnchorPane.setBottomAnchor(contenidoVentana, 0.0);
        AnchorPane.setLeftAnchor(contenidoVentana, 0.0);

        subEscena3D.setOnMousePressed((MouseEvent evento) -> {
            // Define mediante una expresión Lambda el código que se ejecuta cuando se produce
            // el evento de ratón consistente en la pulsación de un botón del ratón.
            // Guarda en las variables ratonXVentanaAntes y ratonYVentanaAntes las coordenadas
            // X e Y donde se pulsó el ratón.
            ratonXVentanaAntes = evento.getSceneX();
            ratonYVentanaAntes = evento.getSceneY();
        });

        subEscena3D.setOnScroll((ScrollEvent evento) -> {
            // Define mediante una expresión Lambda el código que se ejecuta cuando el
            // usuario gira la rueda del ratón.
            double factor = 0.06;
            grupo3D.setRotationAxis(Rotate.Y_AXIS);

            double roll = grupo3D.getRotate() + evento.getDeltaY() * factor;

            double centerX = -3600 * 2 - 2000;
            double centerY = (double) 36300 / 2;
            double centerZ = -2200 * 4;

            // Rotar alrededor del centro del grupo
            Rotate rotacion = new Rotate(roll, centerX, centerY, centerZ, Rotate.Y_AXIS);
            grupo3D.getTransforms().add(rotacion); // Modifica la rotación de la cámara en su eje Y
        });

        subEscena3D.setOnKeyPressed(event -> {
            // Define la expresión Lambda que se ejecuta cuando se detecta la pulsación
            // (única o repetida) de una tecla
            double desplazamiento = 200;
            if (event.isShiftDown()) {
                desplazamiento = 60;
            }
            // Desplazamiento de la cámara. Con mayúsculas se desplaza el doble
            KeyCode tecla = event.getCode(); // Tecla pulsada
            if (tecla == KeyCode.S) {
                // Si tecla S, desplazamiento hacia atrás, en eje Z de la cámara, que es su
                // eje de visión
                Translate traslacion = new Translate(0, 0, -desplazamiento);
                camara.getTransforms().addAll(traslacion);
            }
            if (tecla == KeyCode.W) { // Desplazamiento hacia delante
                Translate traslacion = new Translate(0, 0, desplazamiento);
                camara.getTransforms().addAll(traslacion);
            }
            if (tecla == KeyCode.A) { // Desplazamiento hacia la izquierda
                Translate traslacion = new Translate(-desplazamiento, 0, 0);
                camara.getTransforms().addAll(traslacion);
            }
            if (tecla == KeyCode.D) { // Desplazamiento hacia la derecha
                Translate traslacion = new Translate(desplazamiento, 0, 0);
                camara.getTransforms().addAll(traslacion);
            }
            if (tecla == KeyCode.E) { // Desplazamiento hacia arriba
                Translate traslacion = new Translate(0, -desplazamiento, 0);
                camara.getTransforms().addAll(traslacion);
            }
            if (tecla == KeyCode.C) { // Desplazamiento hacia abajo
                Translate traslacion = new Translate(0, desplazamiento, 0);
                camara.getTransforms().addAll(traslacion);
            }
        });

        subEscena3D.setOnMouseDragged(evento -> {
            // Expresión Lambda que se ejecuta mientras se arrastra el ratón con algún
            // botón pulsado
            double limitePitch = 90; // Ángulo máximo de cabeceo, positivo o negativo

            // Obtiene la nueva posición del ratón
            ratonY = evento.getSceneY();
            ratonX = evento.getSceneX();

            double movimientoRatonY = ratonY - ratonYVentanaAntes;
            double movimientoRatonX = ratonX - ratonXVentanaAntes;

            double factor = 0.05;
            if (evento.isShiftDown()) factor = 0.3;

            if (evento.isPrimaryButtonDown()) {
                // Se está pulsando el botón izquierdo del ratón ...
                if (ratonY != ratonYVentanaAntes) {
                    // Si hubo movimiento vertical del ratón en la ventana ...
                    camara.setRotationAxis(Rotate.X_AXIS);
                    double pitch = -movimientoRatonY * factor;
                    // Ángulo de cabeceo ó pitch en función del movimiento del ratón
                    if (pitch > limitePitch) pitch = limitePitch;
                    if (pitch < -limitePitch) pitch = -limitePitch;
                    // Limita el pitch
                    camara.getTransforms().addAll(new Rotate(pitch, Rotate.X_AXIS));
                    camara.getTransforms().addAll(new Rotate(0, Rotate.Z_AXIS));
                    // Rota la cámara con ese ángulo con respecto a su eje X
                }
                if (ratonX != ratonXVentanaAntes) { // Hubo movimiento horizontal del ratón
                    camara.setRotationAxis(Rotate.Y_AXIS);
                    double yaw = movimientoRatonX * factor; // Calcula el ángulo yaw
                    camara.getTransforms().addAll(new Rotate(yaw, Rotate.Y_AXIS));
                    camara.getTransforms().addAll(new Rotate(0, Rotate.Z_AXIS));
                    // Rota la cámara con ese ángulo con respecto al eje Y de la cámara
                }
            }
            ratonXVentanaAntes = ratonX;
            ratonYVentanaAntes = ratonY;
        });

        subEscena3D.setOnMouseClicked((MouseEvent evento) -> {
            // Trata con una expresión Lambda el evento producido cuando se hace click
            // con el ratón
            Node captura = evento.getPickResult().getIntersectedNode();

            if (captura instanceof Box) {
                for (Palet palet : almacen.TodosPalets) {
                    if (palet.getProductBox() == captura) {
                        Alert alert = new Alert(AlertType.INFORMATION); // Cambia el tipo si es necesario
                        alert.setTitle("titulo");
                        alert.setHeaderText("encabezado");
                        alert.setContentText("Palet: " + palet.getIdProducto() + " = " + palet.getCantidadProducto() + "L (" + palet.getEstanteria() + ", " + palet.getBalda() + ", " + palet.getPosicion() + ", " + palet.isDelante() + ")");

                        alert.showAndWait();

                    }
                }
            }
        });
    }

    @FXML
    private Button openExcelButton;

    @FXML
    private void openXmlInExcel() {
        String excelPath = "\"C:\\Program Files (x86)\\Microsoft Office\\root\\Office16\\EXCEL.EXE\"";
        File folder = new File("output_files/");

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Directorio no encontrado.");
            return;
        }

        File[] xmlFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));

        if (xmlFiles == null || xmlFiles.length == 0) {
            System.out.println("No se encontraron archivos XML.");
            return;
        }

        try {
            for (File xmlFile : xmlFiles) {
                // Usa el comando por defecto del sistema para abrir el archivo con la aplicación asociada (Excel si está bien asociado).
                //Desktop.getDesktop().open(xmlFile);

                // Construir el comando para abrir cada archivo XML con Excel
                String command = excelPath + " \"" + xmlFile.getAbsolutePath() + "\"";
                Runtime.getRuntime().exec(command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}