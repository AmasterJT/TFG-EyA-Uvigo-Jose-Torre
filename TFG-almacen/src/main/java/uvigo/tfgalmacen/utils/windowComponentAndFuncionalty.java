package uvigo.tfgalmacen.utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import uvigo.tfgalmacen.User;

import java.awt.*;
import java.sql.Connection;
import java.util.*;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uvigo.tfgalmacen.RutasFicheros.APP_ICON;

import javafx.scene.image.Image;

import java.net.URL;

public class windowComponentAndFuncionalty {

    private static final Logger LOGGER = Logger.getLogger(windowComponentAndFuncionalty.class.getName());


    static {
        // Sube el nivel del logger
        LOGGER.setLevel(Level.ALL);

        // Evita que use los handlers del padre (que suelen estar en INFO con SimpleFormatter)
        LOGGER.setUseParentHandlers(false);

        // Crea un ConsoleHandler propio con tu ColorFormatter
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);                 // ¡importante!
        ch.setFormatter(new ColorFormatter());  // tu formatter con colores/emoji
        LOGGER.addHandler(ch);

        // (Opcional) Si quieres también afectar al root logger:
        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            h.setLevel(Level.ALL); // si decides mantenerlos
        }
    }

    public static Connection connection = null;
    public static User currentUser = null;
    public static List<User> allUsers = null;

    private static double xOffset = 0; // Desplazamiento horizontal del ratón respecto a la ventana.
    private static double yOffset = 0; // Desplazamiento vertical del ratón respecto a la ventana.

    private static final double borderWidth = 8; // Define el grosor del borde que será interactivo para redimensionar.

    private enum RESIZE {NONE, W_border, E_border, N_border, S_border, NW_cornner, NE_cornner, SW_cornner, SE_cornner}

    public static final Duration SHAKE_DURATION = Duration.millis(50);


    /**
     * Configura el movimiento de la ventana para permitir que el usuario la arrastre desde un área específica.
     *
     * @param root  El nodo raíz del diseño de la ventana, que contiene todos los elementos de la interfaz.
     * @param stage El escenario principal de la aplicación, que representa la ventana.
     */
    public static void WindowMovement(Parent root, Stage stage) {
        // Busca en el diseño un nodo con el ID "windowBar", que se usará para arrastrar la ventana.
        try {
            HBox windowBar = (HBox) root.lookup("#windowBar"); // Cambia "#windowBar" si el ID difiere.

            // Si no se encuentra el componente, lanza una excepción para evitar errores silenciosos.
            if (windowBar == null) {
                throw new IllegalStateException("No se encontró el componente windowBar en el FXML.");
            }

            // Evento para capturar la posición inicial del mouse cuando se presiona en la barra.
            windowBar.setOnMousePressed(event -> {
                // Guarda la posición X e Y del mouse dentro de la escena cuando el usuario presiona el botón.
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            // Evento para manejar el arrastre del mouse y mover la ventana en consecuencia.
            windowBar.setOnMouseDragged(event -> {
                // Calcula la nueva posición de la ventana basada en la posición actual del mouse en la pantalla,
                // menos el desplazamiento inicial registrado cuando se presionó el botón.
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });
        } catch (Exception e) {
            LOGGER.warning("Error al buscar el nodo windowBar en el FXML");
        }
    }


    public static void limpiarGridPane(GridPane gridPane) {
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
    }


    public static void shake(javafx.scene.Node node, Duration SHAKE_DURATION) {
        TranslateTransition tt = new TranslateTransition(SHAKE_DURATION, node);
        tt.setFromX(0);
        tt.setByX(4);
        tt.setAutoReverse(true);
        tt.setCycleCount(2);
        tt.play();
    }


    // ----------------------------
    // Utilidades de UI
    // ----------------------------
    private static boolean isValidSelection(String value, String placeholder) {
        return value != null && !value.isBlank() && !value.equals(placeholder);
    }

    public static boolean isInvalidSelection(String value, String placeholder) {
        return !isValidSelection(value, placeholder);
    }

    public static void parpadearErrorWindowBar(HBox windowBar) {
        if (windowBar == null) return;

        final String original = windowBar.getStyle();
        Timeline t = new Timeline(
                new KeyFrame(Duration.ZERO, _ -> windowBar.setStyle("-fx-background-color:rgba(62,36,17,0.47);")),
                new KeyFrame(Duration.millis(80), _ -> windowBar.setStyle("-fx-background-color:#3e2411;")),
                new KeyFrame(Duration.millis(160), _ -> windowBar.setStyle("-fx-background-color:rgba(62,36,17,0.47);")),
                new KeyFrame(Duration.millis(240), _ -> windowBar.setStyle("-fx-background-color:#3e2411;"))
        );
        t.setCycleCount(3);
        t.setOnFinished(_ -> windowBar.setStyle(original));
        t.play();
    }

    public static Optional<ButtonType> ventana_error(
            String title,
            String headerText,
            String contentText,
            String... buttonLabels // etiquetas opcionales
    ) {
        Alert confirmacion = new Alert(Alert.AlertType.ERROR);
        confirmacion.setTitle(title);
        confirmacion.setHeaderText(headerText);
        confirmacion.setContentText(contentText);

        // Determinar nombres de botones (con valores por defecto)
        String affirmativeLabel = (buttonLabels.length > 0 && buttonLabels[0] != null)
                ? buttonLabels[0]
                : "Sí";
        String negativeLabel = (buttonLabels.length > 1 && buttonLabels[1] != null)
                ? buttonLabels[1]
                : "Cancelar";

        // Crear botones personalizados
        ButtonType btnSi = new ButtonType(affirmativeLabel, ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNo = new ButtonType(negativeLabel, ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(btnSi, btnNo);

        // Aplicar estilo (opcional)
        DialogPane dialogPane = confirmacion.getDialogPane();
        try {
            dialogPane.getStylesheets().add(
                    Objects.requireNonNull(windowComponentAndFuncionalty.class
                                    .getResource("/uvigo/tfgalmacen/css/Styles.css"))
                            .toExternalForm()
            );
            dialogPane.getStyleClass().add("alert-dialog");
        } catch (Exception ex) {
            LOGGER.log(Level.FINE, "No se pudo cargar Styles.css para el diálogo de confirmación.", ex);
        }

        return confirmacion.showAndWait();
    }


    public static void ventana_warning(String tittle, String headerText, String contentText) {

        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(tittle);
        alerta.setHeaderText(headerText);
        alerta.setContentText(contentText);

        // Aplicar estilo (opcional)
        DialogPane dialogPane = alerta.getDialogPane();
        try {
            dialogPane.getStylesheets().add(
                    Objects.requireNonNull(windowComponentAndFuncionalty.class.getResource("/uvigo/tfgalmacen/css/Styles.css")).toExternalForm()
            );
            dialogPane.getStyleClass().add("alert-dialog");
        } catch (Exception ex) {
            LOGGER.log(Level.FINE, "No se pudo cargar Styles.css para el diálogo de confirmación.", ex);
        }

        alerta.showAndWait();
    }


    public static boolean ventana_confirmacion(String title, String content) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(title);
        alerta.setHeaderText(null);
        alerta.setContentText(content);
        // Aplicar estilo (opcional)
        DialogPane dialogPane = alerta.getDialogPane();
        try {
            dialogPane.getStylesheets().add(
                    Objects.requireNonNull(windowComponentAndFuncionalty.class.getResource("/uvigo/tfgalmacen/css/Styles.css")).toExternalForm()
            );
            dialogPane.getStyleClass().add("alert-dialog");
        } catch (Exception ex) {
            LOGGER.log(Level.FINE, "No se pudo cargar Styles.css para el diálogo de confirmación.", ex);
        }
        Optional<ButtonType> result = alerta.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    // Versión básica
    public static @NotNull Stage crearStageBasico(Parent root) {
        return crearStageBasico(root, true, "");
    }

    // Versión con movimiento
    public static @NotNull Stage crearStageBasico(Parent root, boolean movement) {
        return crearStageBasico(root, movement, "");
    }

    // Versión básica
    public static @NotNull Stage crearStageBasico(Parent root, String tittle) {
        return crearStageBasico(root, true, tittle);
    }

    // Versión completa
    public static @NotNull Stage crearStageBasico(Parent root, boolean movement, String title) {
        // Crear escena con fondo transparente
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        // Crear ventana transparente
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setTitle(title == null ? "" : title);

        // 🔹 Icono (APP_ICON = ruta en resources, ej. "/uvigo/tfgalmacen/icons/app_icon.png")
        try {
            URL iconUrl = windowComponentAndFuncionalty.class.getResource(APP_ICON);
            if (iconUrl != null) {
                stage.getIcons().add(new Image(iconUrl.toExternalForm()));
            } else {
                LOGGER.warning("No se encontró el icono en APP_ICON: " + APP_ICON);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "No se pudo cargar el icono de la app desde APP_ICON: " + APP_ICON, ex);
        }

        // Activar movimiento si aplica
        if (movement) WindowMovement(root, stage);

        // Recortar el contenido con esquinas redondeadas (20px)
        Rectangle clip = new Rectangle();
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        root.setClip(clip);

        // Ajustar clip dinámicamente al tamaño de la ventana
        root.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            clip.setWidth(newVal.getWidth());
            clip.setHeight(newVal.getHeight());
        });

        return stage;
    }


    public static TextFormatter<String> TEXTVIEW_NUMERICO = new TextFormatter<>(c -> {
        if (c.getControlNewText().matches("\\d*")) {
            return c; // aceptar si es un número
        }
        return null; // ignorar lo demás
    });


    public static TextFormatter<String> numericFormatter() {
        return new TextFormatter<>(c -> c.getControlNewText().matches("\\d*") ? c : null);
    }

}
