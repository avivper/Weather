import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CloseBar extends HBox {

    // The Window will start and (0, 0), at the center of the screen
    private double xOffset = 0; // X Axis
    private double yOffset = 0; // Y Axis

    public static Button closeButton;

    public CloseBar(Stage stage) throws FileNotFoundException {
        this.setPadding(new Insets(10));
        this.setStyle(" -fx-background-color: #333333;");
        this.setAlignment(Pos.CENTER_RIGHT);

        InputStream stream = new FileInputStream("data\\status\\weather-app.png");
        Image image = new Image(stream);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setX(10);
        imageView.setY(10);


        Button minimizeButton = new Button();
        closeButton = new Button();

        // Implement UI
        minimizeButton.setId("minimize-button");
        closeButton.setId("close-button");

        minimizeButton.setOnAction(
                event -> {
                    stage.setIconified(true);
                }
        );

        closeButton.setOnAction(
                event -> {
                    stage.close();
                }
        );

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        HBox.setMargin(minimizeButton, new Insets(0, 10, 0, 10)); // Space between close and minimize
        this.getChildren().addAll(imageView, region, minimizeButton, closeButton);

        /*
        When I was a child I asked myself why I should learn the X and Y Graph?
        Well, after I started to code, Now I understand WHY
         */

        this.setOnMousePressed(
                event -> {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                    xOffset = stage.getX() - event.getScreenX();
                    yOffset = stage.getY() - event.getScreenY();
                }
        );

        this.setOnMouseDragged(
                event -> {
                    stage.setX(event.getScreenX() + xOffset);
                    stage.setY(event.getScreenY() + yOffset);
                }
        );
        // User now can drag the CloseBar smoothly based on X and Y
    }
}
