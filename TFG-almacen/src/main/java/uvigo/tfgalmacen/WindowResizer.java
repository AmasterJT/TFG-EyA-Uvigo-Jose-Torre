package uvigo.tfgalmacen.utils;

import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.scene.Cursor.*;

public final class WindowResizer {

    private WindowResizer() {
    }

    /**
     * Grosor del borde interactivo
     */
    private static final double BORDER = 8.0;

    /**
     * Mínimos recomendados (ajusta a lo que necesites)
     */
    private static final double MIN_W = 320.0;
    private static final double MIN_H = 200.0;

    private enum Region {NONE, N, S, E, W, NE, NW, SE, SW}

    public static void attach(Parent root, Stage stage, Scene scene) {
        final DragState state = new DragState();

        // Cursor dinámico según zona
        root.setOnMouseMoved(e -> {
            double x = e.getSceneX();
            double y = e.getSceneY();
            Region r = computeRegion(x, y, stage.getWidth(), stage.getHeight());
            root.setCursor(cursorFor(r));
            state.region = r; // pre-selección (rápida, sin click)
        });

        // Fijamos región al presionar (evita “saltos” si el cursor cambia durante el drag)
        root.setOnMousePressed(e -> {
            state.region = computeRegion(e.getSceneX(), e.getSceneY(), stage.getWidth(), stage.getHeight());
            state.startScreenX = e.getScreenX();
            state.startScreenY = e.getScreenY();
            state.startX = stage.getX();
            state.startY = stage.getY();
            state.startW = stage.getWidth();
            state.startH = stage.getHeight();
        });

        // Arrastre para redimensionar
        root.setOnMouseDragged(e -> {
            if (state.region == Region.NONE) return;

            double dx = e.getScreenX() - state.startScreenX;
            double dy = e.getScreenY() - state.startScreenY;

            // Clona valores de partida
            double newX = state.startX;
            double newY = state.startY;
            double newW = state.startW;
            double newH = state.startH;

            switch (state.region) {
                case NW -> {
                    // izquierda + arriba
                    double[] west = resizeWest(newX, newW, dx);
                    newX = west[0];
                    newW = west[1];
                    double[] north = resizeNorth(newY, newH, dy);
                    newY = north[0];
                    newH = north[1];
                }
                case NE -> {
                    // derecha + arriba
                    newW = resizeEast(newW, dx);
                    double[] north = resizeNorth(newY, newH, dy);
                    newY = north[0];
                    newH = north[1];
                }
                case SW -> {
                    // izquierda + abajo
                    double[] west = resizeWest(newX, newW, dx);
                    newX = west[0];
                    newW = west[1];
                    newH = resizeSouth(newH, dy);
                }
                case SE -> {
                    // derecha + abajo
                    newW = resizeEast(newW, dx);
                    newH = resizeSouth(newH, dy);
                }
                case W -> {
                    double[] west = resizeWest(newX, newW, dx);
                    newX = west[0];
                    newW = west[1];
                }
                case E -> newW = resizeEast(newW, dx);
                case N -> {
                    double[] north = resizeNorth(newY, newH, dy);
                    newY = north[0];
                    newH = north[1];
                }
                case S -> newH = resizeSouth(newH, dy);
                default -> {
                }
            }

            // Aplicar límites mínimos
            if (newW < MIN_W) {
                // Corrige X si contrajimos por la izquierda
                if (state.region == Region.W || state.region == Region.NW || state.region == Region.SW) {
                    newX += (newW - MIN_W);
                }
                newW = MIN_W;
            }
            if (newH < MIN_H) {
                // Corrige Y si contrajimos por arriba
                if (state.region == Region.N || state.region == Region.NW || state.region == Region.NE) {
                    newY += (newH - MIN_H);
                }
                newH = MIN_H;
            }

            stage.setX(newX);
            stage.setY(newY);
            stage.setWidth(newW);
            stage.setHeight(newH);
            e.consume();
        });

        // Al soltar, restaurar cursor si no está en borde
        root.setOnMouseReleased(e -> {
            if (computeRegion(e.getSceneX(), e.getSceneY(), stage.getWidth(), stage.getHeight()) == Region.NONE) {
                root.setCursor(Cursor.DEFAULT);
            }
        });
    }

    // ---------- Helpers de cálculo ----------

    private static Region computeRegion(double x, double y, double w, double h) {
        boolean left = x <= BORDER;
        boolean right = x >= w - BORDER;
        boolean top = y <= BORDER;
        boolean bottom = y >= h - BORDER;

        if (top && left) return Region.NW;
        if (top && right) return Region.NE;
        if (bottom && left) return Region.SW;
        if (bottom && right) return Region.SE;
        if (left) return Region.W;
        if (right) return Region.E;
        if (top) return Region.N;
        if (bottom) return Region.S;
        return Region.NONE;
    }

    private static Cursor cursorFor(Region r) {
        return switch (r) {
            case NW -> NW_RESIZE;
            case NE -> NE_RESIZE;
            case SW -> SW_RESIZE;
            case SE -> SE_RESIZE;
            case N -> N_RESIZE;
            case S -> S_RESIZE;
            case E -> E_RESIZE;
            case W -> W_RESIZE;
            default -> DEFAULT;
        };
    }

    // ---------- Helpers de resize (reutilizables) ----------

    /**
     * Redimensiona por el borde izquierdo, devolviendo [nuevoX, nuevoW].
     */
    private static double[] resizeWest(double startX, double startW, double dx) {
        double newX = startX + dx;
        double newW = startW - dx;
        if (newW < MIN_W) {
            // Compensa para no saltar por debajo del mínimo
            newX -= (MIN_W - newW);
            newW = MIN_W;
        }
        return new double[]{newX, newW};
    }

    /**
     * Redimensiona por el borde derecho, devolviendo nuevo ancho.
     */
    private static double resizeEast(double startW, double dx) {
        double w = startW + dx;
        return Math.max(w, MIN_W);
    }

    /**
     * Redimensiona por el borde superior, devolviendo [nuevoY, nuevoH].
     */
    private static double[] resizeNorth(double startY, double startH, double dy) {
        double newY = startY + dy;
        double newH = startH - dy;
        if (newH < MIN_H) {
            newY -= (MIN_H - newH);
            newH = MIN_H;
        }
        return new double[]{newY, newH};
    }

    /**
     * Redimensiona por el borde inferior, devolviendo nueva altura.
     */
    private static double resizeSouth(double startH, double dy) {
        double h = startH + dy;
        return Math.max(h, MIN_H);
    }

    // ---------- Estado del drag ----------
    private static final class DragState {
        double startScreenX, startScreenY;
        double startX, startY, startW, startH;
        Region region = Region.NONE;
    }
}
