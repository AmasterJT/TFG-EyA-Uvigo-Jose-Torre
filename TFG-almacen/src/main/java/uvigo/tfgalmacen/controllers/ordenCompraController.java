package uvigo.tfgalmacen.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class ordenCompraController {

    @FXML
    private HBox windowBar;


    /**
     * Hace parpadear la barra superior (windowBar) para llamar la atención
     */
    private void parpadearWindowBar() {
        if (windowBar == null) return;

        // Animación simple cambiando opacidad
        Timeline blink = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(windowBar.opacityProperty(), 1.0)),
                new KeyFrame(Duration.millis(100), new KeyValue(windowBar.opacityProperty(), 0.3)),
                new KeyFrame(Duration.millis(200), new KeyValue(windowBar.opacityProperty(), 1.0))
        );

        blink.setCycleCount(3); // número de parpadeos
        blink.play();
    }


    /**
     * Exponerlo para que el padre pueda llamarlo
     */
    public void notifyClickOutside() {
        parpadearWindowBar();
    }
}
