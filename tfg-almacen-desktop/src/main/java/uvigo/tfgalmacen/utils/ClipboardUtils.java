package uvigo.tfgalmacen.utils;

import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.logging.*;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Duration;

import java.util.logging.Logger;

public class ClipboardUtils {

    /**
     * Utilidad para copiar el texto de un Label al portapapeles
     * con animación y logging detallado.
     */
    private static final Logger LOGGER = Logger.getLogger("ClipboardLogger");


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

    /**
     * Copia el contenido de un Label al portapapeles cuando se pulsa un botón.
     * Si el Label no tiene texto válido, muestra una advertencia visual.
     *
     * @param boton Botón que activa la acción (para mostrar animación).
     * @param label Label del cual se copiará el texto.
     */
    public static void copyLabelText(Button boton, Label label) {
        if (label == null) {
            LOGGER.severe("Label nulo: no se puede copiar texto.");
            mostrarTooltip(boton, "Error: Label no encontrado");
            return;
        }

        String texto = label.getText();

        // Validar contenido
        if (texto == null || texto.trim().isEmpty()) {
            LOGGER.warning("El Label no contiene texto para copiar.");
            mostrarTooltip(boton, "Nada que copiar");
            animacionError(boton);
            return;
        }

        try {
            // Copiado al portapapeles del sistema
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(texto.trim());
            clipboard.setContent(content);

            LOGGER.info(() -> "Texto copiado al portapapeles: \"" + texto.trim() + "\"");

            mostrarTooltip(boton, "✅ Copiado");
            animacionConfirmacion(boton);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error copiando texto al portapapeles", ex);
            mostrarTooltip(boton, "❌ Error al copiar");
            animacionError(boton);
        }
    }


    public static void copyLabelText(Button boton, TextField textField) {
        if (textField == null) {
            LOGGER.severe("Label nulo: no se puede copiar texto.");
            mostrarTooltip(boton, "Error: Label no encontrado");
            return;
        }

        String texto = textField.getText();

        // Validar contenido
        if (texto == null || texto.trim().isEmpty()) {
            LOGGER.warning("El Label no contiene texto para copiar.");
            mostrarTooltip(boton, "Nada que copiar");
            animacionError(boton);
            return;
        }

        try {
            // Copiado al portapapeles del sistema
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(texto.trim());
            clipboard.setContent(content);

            LOGGER.info(() -> "Texto copiado al portapapeles: \"" + texto.trim() + "\"");

            mostrarTooltip(boton, "✅ Copiado");
            animacionConfirmacion(boton);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error copiando texto al portapapeles", ex);
            mostrarTooltip(boton, "❌ Error al copiar");
            animacionError(boton);
        }
    }


    /**
     * Muestra un Tooltip temporal sobre el botón.
     * Desaparece automáticamente tras 1.5 segundos.
     */
    private static void mostrarTooltip(Button boton, String mensaje) {
        if (boton == null || boton.getScene() == null) {
            LOGGER.warning("No se puede mostrar tooltip: el botón o la escena son nulos.");
            return;
        }

        Tooltip tooltip = new Tooltip(mensaje);
        tooltip.setAutoHide(true);

        // Posicionar justo encima del botón
        double x = boton.localToScreen(boton.getBoundsInLocal()).getMinX();
        double y = boton.localToScreen(boton.getBoundsInLocal()).getMinY() - 30;

        tooltip.show(boton.getScene().getWindow(), x, y);

        // Cerrar automáticamente tras 1.5 segundos (1500 ms)
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                javafx.application.Platform.runLater(tooltip::hide);
            } catch (InterruptedException ignored) {
            }
        }).start();
    }


    /**
     * Animación sutil de confirmación (efecto fade verde).
     */
    private static void animacionConfirmacion(Button boton) {
        boton.setStyle("-fx-background-color: #4CAF50;"); // verde éxito
        FadeTransition ft = new FadeTransition(Duration.millis(500), boton);
        ft.setFromValue(1.0);
        ft.setToValue(0.3);
        ft.setCycleCount(1);
        ft.setAutoReverse(true);
        ft.setOnFinished(_ -> boton.setStyle("-fx-opacity: 1"));
        ft.play();
    }

    /**
     * Animación de error (efecto parpadeo rojo).
     */
    private static void animacionError(Button boton) {
        boton.setStyle("-fx-background-color: #d32f2f;"); // rojo error
        FadeTransition ft = new FadeTransition(Duration.millis(400), boton);
        ft.setFromValue(1.0);
        ft.setToValue(0.3);
        ft.setCycleCount(1);
        ft.setAutoReverse(true);
        ft.setOnFinished(_ -> boton.setStyle("-fx-opacity: 1"));
        ft.play();
    }
}
