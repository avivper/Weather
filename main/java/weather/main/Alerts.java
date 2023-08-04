package weather.main;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

public class Alerts {

    private final Alert.AlertType confirmation = Alert.AlertType.CONFIRMATION;

    public Alerts(String title, String contentText,
                  Alert.AlertType alertType,
                  Consumer<Boolean> decision) {

        Platform.runLater(() -> { // Run the alert on the JavaFX Application Thread
            Alert alertHandle = new Alert(alertType);
            DialogPane dialogPane = alertHandle.getDialogPane();

            alertHandle.setTitle(title);
            alertHandle.setHeaderText("");
            alertHandle.setContentText(contentText);

            VBox vbox = new VBox();
            vbox.setAlignment(Pos.CENTER);

            vbox.getChildren().add(new Label(contentText));
            dialogPane.setContent(vbox);

            dialogPane.setPrefWidth(400);
            dialogPane.setPrefHeight(120);

            String modenaCSS = "data\\css\\caspian.css";
            alertHandle.getDialogPane().getStylesheets().add(
                    new File(modenaCSS).toURI().toString());


            if (alertType == confirmation) {

                ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
                alertHandle.getButtonTypes().setAll(yesButton, noButton);

                Optional<ButtonType> result = alertHandle.showAndWait();

                if (result.isPresent() && result.get() == yesButton) {
                    decision.accept(true);
                } else {
                    decision.accept(false);
                }

            } else {
                alertHandle.showAndWait();
            }
        });
    }
}
