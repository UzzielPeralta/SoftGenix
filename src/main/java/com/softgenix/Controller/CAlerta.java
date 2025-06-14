package com.softgenix.Controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CAlerta {

    @FXML
    private Label lblMensaje;

    @FXML
    private VBox alertBox;

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;

        dialogStage.setOnShown(e -> {
            dialogStage.centerOnScreen();
            reproducirAnimacionEntrada();
        });
    }

    public void setMessage(String mensaje) {
        lblMensaje.setText(mensaje);
    }

    @FXML
    private void cerrarAlerta() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), alertBox);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), alertBox);
        scaleOut.setFromX(1.0);
        scaleOut.setFromY(1.0);
        scaleOut.setToX(0.9);
        scaleOut.setToY(0.9);

        fadeOut.setOnFinished(e -> dialogStage.close());

        fadeOut.play();
        scaleOut.play();
    }

    private void reproducirAnimacionEntrada() {
        alertBox.setOpacity(0.0);
        alertBox.setScaleX(0.9);
        alertBox.setScaleY(0.9);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), alertBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(250), alertBox);
        scaleIn.setFromX(0.9);
        scaleIn.setFromY(0.9);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        fadeIn.play();
        scaleIn.play();
    }
}
