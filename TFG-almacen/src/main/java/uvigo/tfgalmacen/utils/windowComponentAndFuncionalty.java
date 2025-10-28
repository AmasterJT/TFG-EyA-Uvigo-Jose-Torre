package uvigo.tfgalmacen.utils;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.NotNull;
import uvigo.tfgalmacen.User;

import java.sql.Connection;
import java.util.List;

import static javafx.scene.Cursor.*;
import static javafx.scene.Cursor.DEFAULT;
import static javafx.scene.Cursor.E_RESIZE;
import static javafx.scene.Cursor.N_RESIZE;
import static javafx.scene.Cursor.SE_RESIZE;
import static javafx.scene.Cursor.S_RESIZE;
import static javafx.scene.Cursor.W_RESIZE;


public class windowComponentAndFuncionalty {

    public static Connection connection = null;
    public static User currentUser = null;
    public static List<User> allUsers = null;

    private static double xOffset = 0; // Desplazamiento horizontal del ratón respecto a la ventana.
    private static double yOffset = 0; // Desplazamiento vertical del ratón respecto a la ventana.

    private static final double borderWidth = 8; // Define el grosor del borde que será interactivo para redimensionar.

    private enum RESIZE {NONE, W_border, E_border, N_border, S_border, NW_cornner, NE_cornner, SW_cornner, SE_cornner}

    private static RESIZE resize;

    private static double mousex21;
    private static double mousex22 = 0;

    private boolean isMoving = false;
    private boolean isResizing = false;


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


    /**
     * Configura el redimensionamiento de la ventana mediante el movimiento y el arrastre del ratón.
     *
     * @param root  El nodo raíz del diseño de la ventana.
     * @param stage El escenario principal de la ventana.
     */
    public static void enableWindowResize(Parent root, Stage stage) {
        root.setOnMouseMoved(event -> {
            double mouseX = event.getSceneX(); // Posición X del ratón en la escena.
            double mouseY = event.getSceneY(); // Posición Y del ratón en la escena.
            double width = stage.getWidth();   // Ancho actual de la ventana.
            double height = stage.getHeight(); // Alto actual de la ventana.

            // Cambiar el cursor y configurar el estado de redimensionamiento.
            if (mouseX < borderWidth && mouseY < borderWidth) {
                root.setCursor(NW_RESIZE); // Esquina superior izquierda.
                resize = RESIZE.NW_cornner;
            } else if (mouseX < borderWidth && mouseY > height - borderWidth) {
                root.setCursor(SW_RESIZE); // Esquina inferior izquierda.
                resize = RESIZE.SW_cornner;
            } else if (mouseX > width - borderWidth && mouseY < borderWidth) {
                root.setCursor(NE_RESIZE); // Esquina superior derecha.
                resize = RESIZE.NE_cornner;
            } else if (mouseX > width - borderWidth && mouseY > height - borderWidth) {
                root.setCursor(SE_RESIZE); // Esquina inferior derecha.
                resize = RESIZE.SE_cornner;
            } else if (mouseX < borderWidth) {
                root.setCursor(W_RESIZE);  // Borde izquierdo.
                resize = RESIZE.W_border;
            } else if (mouseX > width - borderWidth) {
                root.setCursor(E_RESIZE);  // Borde derecho.
                resize = RESIZE.E_border;
            } else if (mouseY < borderWidth) {
                root.setCursor(N_RESIZE);  // Borde superior.
                resize = RESIZE.N_border;
            } else if (mouseY > height - borderWidth) {
                root.setCursor(S_RESIZE);  // Borde inferior.
                resize = RESIZE.S_border;
            } else {
                root.setCursor(DEFAULT);  // Cursor por defecto.
                resize = RESIZE.NONE;
            }
        });

        root.setOnMouseDragged(event -> {
            double mouseX = event.getSceneX(); // Posición X del ratón en la escena.
            double mouseY = event.getSceneY(); // Posición Y del ratón en la escena.
            double width = stage.getWidth();   // Ancho actual de la ventana.
            double height = stage.getHeight(); // Alto actual de la ventana.

            // Tamaño mínimo permitido para la ventana.
            double minWidth = 100;
            double minHeight = 100;

            switch (resize) {
                case NW_cornner: // Esquina superior izquierda.
                    double newWidth = width - mouseX;
                    double newHeight = height - mouseY;
                    if (newWidth > minWidth) {
                        stage.setWidth(newWidth);
                        stage.setX(event.getScreenX());
                    }
                    if (newHeight > minHeight) {
                        stage.setHeight(newHeight);
                        stage.setY(event.getScreenY());
                    }
                    break;

                case SW_cornner: // Esquina inferior izquierda.
                    newWidth = width - mouseX;
                    if (newWidth > minWidth) {
                        stage.setWidth(newWidth);
                        stage.setX(event.getScreenX());
                    }
                    newHeight = mouseY;
                    if (newHeight > minHeight) {
                        stage.setHeight(newHeight);
                    }
                    break;

                case NE_cornner: // Esquina superior derecha.
                    newWidth = mouseX;
                    if (newWidth > minWidth) {
                        stage.setWidth(newWidth);
                    }
                    newHeight = height - mouseY;
                    if (newHeight > minHeight) {
                        stage.setHeight(newHeight);
                        stage.setY(event.getScreenY());
                    }
                    break;

                case SE_cornner: // Esquina inferior derecha.
                    newWidth = mouseX;
                    newHeight = mouseY;
                    if (newWidth > minWidth) {
                        stage.setWidth(newWidth);
                    }
                    if (newHeight > minHeight) {
                        stage.setHeight(newHeight);
                    }
                    break;

                case W_border: // Borde izquierdo.
                    newWidth = width - mouseX;
                    if (newWidth > minWidth) {
                        stage.setWidth(newWidth);
                        stage.setX(event.getScreenX());
                    }
                    break;

                case E_border: // Borde derecho.
                    newWidth = mouseX;
                    if (newWidth > minWidth) {
                        stage.setWidth(newWidth);
                    }
                    break;

                case N_border: // Borde superior.
                    newHeight = height - mouseY;
                    if (newHeight > minHeight) {
                        stage.setHeight(newHeight);
                        stage.setY(event.getScreenY());
                    }
                    break;

                case S_border: // Borde inferior.
                    newHeight = mouseY;
                    if (newHeight > minHeight) {
                        stage.setHeight(newHeight);
                    }
                    break;

                default:
                    break;
            }
        });
    }

    public static void limpiarGridPane(GridPane gridPane) {
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
    }

}
