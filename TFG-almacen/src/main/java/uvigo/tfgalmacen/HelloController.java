package uvigo.tfgalmacen;

import io.github.palexdev.materialfx.controls.MFXCheckbox;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;


import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private Button ExitButton;

    @FXML
    private Label MenuBackButton;

    @FXML
    private Label MenuButton;

    @FXML
    private AnchorPane Slider;

    @FXML
    private Label welcomeText;


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
        MenuButton.setOnMouseClicked(event -> {
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
    }
}