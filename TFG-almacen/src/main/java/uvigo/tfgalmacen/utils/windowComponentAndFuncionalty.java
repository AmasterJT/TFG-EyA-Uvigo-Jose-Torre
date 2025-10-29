package uvigo.tfgalmacen.utils;

import javafx.animation.TranslateTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import uvigo.tfgalmacen.User;

import java.sql.Connection;
import java.util.List;


public class windowComponentAndFuncionalty {

    public static Connection connection = null;
    public static User currentUser = null;
    public static List<User> allUsers = null;

    private static double xOffset = 0; // Desplazamiento horizontal del ratón respecto a la ventana.
    private static double yOffset = 0; // Desplazamiento vertical del ratón respecto a la ventana.

    private static final double borderWidth = 8; // Define el grosor del borde que será interactivo para redimensionar.

    private enum RESIZE {NONE, W_border, E_border, N_border, S_border, NW_cornner, NE_cornner, SW_cornner, SE_cornner}


    public static @NotNull Stage crearStageBasico(String titulo, AnchorPane root, boolean habilitarMovimientoVentana) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle(titulo);
        stage.setScene(new Scene(root));
        if (habilitarMovimientoVentana) {
            WindowMovement(root, stage);
        }
        return stage;
    }


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
            System.out.println("Error al buscar el nodo windowBar en el FXML");
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
}
