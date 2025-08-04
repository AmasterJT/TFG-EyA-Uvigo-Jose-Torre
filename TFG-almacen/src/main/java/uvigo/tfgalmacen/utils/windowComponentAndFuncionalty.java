package uvigo.tfgalmacen.utils;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import uvigo.tfgalmacen.Main;
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
    private enum RESIZE {NONE, W_border, E_border, N_border, S_border, NW_cornner, NE_cornner, SW_cornner, SE_cornner}
    static Main.RESIZE resize;


    static double mousex21;

    /**
     * Configura el movimiento de la ventana para permitir que el usuario la arrastre desde un área específica.
     *
     * @param root  El nodo raíz del diseño de la ventana, que contiene todos los elementos de la interfaz.
     * @param stage El escenario principal de la aplicación, que representa la ventana.
     */
    public static void WindowMovement(Parent root, Stage stage) {
        // Busca en el diseño un nodo con el ID "windowBar", que se usará para arrastrar la ventana.
        try{
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
        }
        catch(Exception e){
            System.out.println("Error al buscar el nodo windowBar en el FXML");
        }
    }



    /**
     * Configura el redimensionamiento de la ventana mediante el movimiento y el arrastre del ratón.
     *
     * @param root  El nodo raíz del diseño de la ventana.
     * @param stage El escenario principal que representa la ventana.
     * @param scene La escena que contiene el diseño de la ventana.
     */
    public static void WindowResize(Parent root, Stage stage, Scene scene) {
        final double borderWidth = 8; // Define el grosor del borde que será interactivo para redimensionar.
        // Evento que detecta el movimiento del ratón para cambiar el cursor al acercarse al borde.

        root.setOnMouseMoved(event -> {
            Main.mousex21 = event.getScreenX();
            double mouseX = event.getSceneX(); // Obtiene la posición X del ratón en la escena.
            double mouseY = event.getSceneY(); // Obtiene la posición Y del ratón en la escena.
            double width = stage.getWidth();   // Obtiene el ancho actual de la ventana.
            double height = stage.getHeight(); // Obtiene el alto actual de la ventana.


            // Cambia el cursor según la posición del ratón cerca de los bordes o esquinas y establece el modo de redimensionamiento.
            if (mouseX < borderWidth && mouseY < borderWidth) {
                root.setCursor(NW_RESIZE); // Esquina superior izquierda.
                resize = Main.RESIZE.NW_cornner;
            } else if (mouseX < borderWidth && mouseY > height - borderWidth) {
                root.setCursor(SW_RESIZE); // Esquina inferior izquierda.
                resize = Main.RESIZE.SW_cornner;
            } else if (mouseX > width - borderWidth && mouseY < borderWidth) {
                root.setCursor(NE_RESIZE); // Esquina superior derecha.
            } else if (mouseX > width - borderWidth && mouseY > height - borderWidth) {
                root.setCursor(SE_RESIZE); // Esquina inferior derecha.
                resize = Main.RESIZE.SE_cornner;
            } else if (mouseX < borderWidth) {
                root.setCursor(W_RESIZE);  // Borde izquierdo.
                resize = Main.RESIZE.W_border;
            } else if (mouseX > width - borderWidth) {
                root.setCursor(E_RESIZE);  // Borde derecho.
                resize = Main.RESIZE.E_border;
            } else if (mouseY < borderWidth) {
                root.setCursor(N_RESIZE);  // Borde superior.
                resize = Main.RESIZE.N_border;
            } else if (mouseY > height - borderWidth) {
                root.setCursor(S_RESIZE);  // Borde inferior.
                resize = Main.RESIZE.S_border;
            } else {
                root.setCursor(DEFAULT);  // Cursor por defecto si no está en un borde.
            }


        });


        // Evento que redimensiona la ventana mientras el usuario arrastra el borde.
        root.setOnMouseDragged(event -> {
            double mouseX = event.getSceneX(); // Posición X del ratón dentro de la escena.
            double mouseY = event.getSceneY(); // Posición Y del ratón dentro de la escena.
            double width = stage.getWidth();   // Ancho actual de la ventana.
            double height = stage.getHeight(); // Alto actual de la ventana.
            double mousex22 = 0;

            double newWidth;
            double newHeight;

            // Verifica el estado del resize y realiza la acción correspondiente.
            switch (resize) {
                case NW_cornner:
                    // Redimensionar desde la esquina superior izquierda.
                    newWidth = width - mouseX;
                    newHeight = height - mouseY;
                    if (newWidth > 0) {
                        stage.setWidth(newWidth);
                        stage.setX(event.getScreenX());
                    }
                    if (newHeight > 0) {
                        stage.setHeight(newHeight);
                        stage.setY(event.getScreenY());
                    }
                    break;
                case SW_cornner:
                    // Redimensionar desde la esquina inferior izquierda.
                    stage.setWidth(mouseX);
                    newHeight = height - mouseY;
                    if (newHeight > 0) {
                        stage.setHeight(newHeight);
                        stage.setY(event.getScreenY());
                    }
                    break;
                case NE_cornner:
                    // Redimensionar desde la esquina superior derecha.
                    stage.setWidth(mouseX);
                    newHeight = height - mouseY;
                    if (newHeight > 0) {
                        stage.setHeight(newHeight);
                        stage.setY(event.getScreenY());
                    }
                    break;
                case SE_cornner:
                    // Redimensionar desde la esquina inferior derecha.
                    stage.setWidth(mouseX);
                    stage.setHeight(mouseY);
                    break;
                case W_border:
                    // Redimensionar desde el borde izquierdo.
                    mousex22 = event.getScreenX();

                    double newX = Main.mousex21 - mousex22;
                    newWidth = width + newX;
                    stage.setX(mousex22);
                    if (newWidth > 0) {
                        stage.setWidth(newWidth);

                    }
                    break;
                case E_border:
                    // Redimensionar desde el borde derecho.
                    stage.setWidth(mouseX);
                    break;
                case N_border:
                    // Redimensionar desde el borde superior.
                    newHeight = height - mouseY;
                    if (newHeight > 0) {
                        stage.setHeight(newHeight);
                        stage.setY(event.getScreenY());
                    }
                    break;
                case S_border:
                    // Redimensionar desde el borde inferior.
                    stage.setHeight(mouseY);
                    break;

                default:
                    break;
            }

            Main.mousex21 = mousex22;
        });

    }


    public static void limpiarGridPane(GridPane gridPane) {
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
    }

}
