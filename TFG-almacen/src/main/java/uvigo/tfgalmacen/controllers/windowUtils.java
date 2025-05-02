package uvigo.tfgalmacen.controllers;

import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import static javafx.scene.Cursor.*;

public class wWindowUtils {

    private static final double borderWidth = 8; // Define el grosor del borde que será interactivo para redimensionar.
    private static double xOffset = 0; // Desplazamiento horizontal del ratón respecto a la ventana.
    private static double yOffset = 0; // Desplazamiento vertical del ratón respecto a la ventana.
    private enum RESIZE {NONE, W_border, E_border, N_border, S_border, NW_cornner, NE_cornner, SW_cornner, SE_cornner}
    private static RESIZE resize;
    private static double mousex21;
    private static double mousex22 = 0;

    private boolean isMoving = false;
    private boolean isResizing = false;

    /**
     * Configura el movimiento de la ventana para permitir que el usuario la arrastre.
     *
     * @param root  El nodo raíz del diseño de la ventana.
     * @param stage El escenario principal de la ventana.
     */
    public static void enableWindowMovement(Parent root, Stage stage) {
        try {
            HBox windowBar = (HBox) root.lookup("#windowBar"); // Busca el nodo con el ID "windowBar".

            if (windowBar == null) {
                throw new IllegalStateException("No se encontró el componente windowBar en el FXML.");
            }

            // Capturar la posición inicial del ratón al presionar.
            windowBar.setOnMousePressed(event -> {
                double mouseX = event.getX(); // Posición del ratón relativa a la windowBar.
                double mouseY = event.getY(); // Posición vertical del ratón relativa a la windowBar.

                // Comprobar si el ratón está dentro de los límites definidos por borderWidth.
                if (mouseX >= borderWidth && mouseX <= windowBar.getWidth() - borderWidth &&
                        mouseY >= borderWidth && mouseY <= windowBar.getHeight() - borderWidth) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                } else {
                    // Si el ratón no está dentro de los bordes válidos, ignora el evento.
                    xOffset = Double.NaN;
                    yOffset = Double.NaN;
                }
            });

            // Manejar el arrastre del ratón para mover la ventana.
            windowBar.setOnMouseDragged(event -> {
                // Solo permitir el movimiento si las coordenadas iniciales son válidas.
                if (!Double.isNaN(xOffset) || !Double.isNaN(yOffset)) {
                    double newX = event.getScreenX() - xOffset;
                    double newY = event.getScreenY() - yOffset;

                    stage.setX(newX);
                    stage.setY(newY);
                }
            });
        } catch (Exception e) {
            System.out.println("Error al buscar el nodo windowBar en el FXML: " + e.getMessage());
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
}
