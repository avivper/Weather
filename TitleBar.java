import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class TitleBar extends HBox {
    private double xOffset = 0;
    private double yOffset = 0;
    public static Button closeButton;

    public TitleBar(Stage stage) {
        this.setPadding(new Insets(10));
        this.setStyle(" -fx-background-color: #333333;");
        this.setAlignment(Pos.CENTER_RIGHT);

        Button minimizeButton = new Button();
        closeButton = new Button();

        minimizeButton.setId("minimize-button");
        minimizeButton.setOnAction(
                event -> {
                    stage.setIconified(true);
                }
        );

        closeButton.setId("close-button");
        closeButton.setOnAction(
                event -> {
                    stage.close();
                }
        );

        HBox.setMargin(minimizeButton, new Insets(0, 10, 0, 0));
        this.getChildren().addAll(minimizeButton, closeButton);

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
    }
}
