import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.Objects;
import java.util.function.Consumer;

public class Error extends Stage {

    private Label error;
    private final String ErrorPath = "data\\css\\error.css";
    public static Consumer<Boolean> user_confirmed = decision -> {};

    public static void raiseError(int error) {
        new Error(error);
    }


    public Error(int error_num) {  // todo: add a warning img
        BorderPane root = new BorderPane();
        VBox ErrorContainer = new VBox();

        switch (error_num) {
            case 0 ->
                error = new Label("An error has been occurred");
            case 1 ->
                    error = new Label("Are you sure you want to remove?");
            case 144 ->
                    error = new Label("Error code: 144\n" +
                    "Unable to connect, try again later");
            case 121 ->
                error = new Label("City already exists");
            case 666 ->
                    error = new Label(
                    "Unable to connect, try again later");
            case 676 ->
                    error = new Label(
                    "You are offline, connect and try again");
            case 348 ->
                    error = new Label("File not found");
            case 999 ->
                    error = new Label("Test");
            case 100 ->
                    error = new Label("Invalid Data");
            case 146 ->
                    error = new Label("No more cities can be added");
        }

        error.setId("Error");
        ErrorContainer.setAlignment(Pos.CENTER);
        ErrorContainer.setSpacing(20);
        ErrorContainer.setPadding(new Insets(15, 0, 15, 0));
        ErrorContainer.getChildren().add(0, error);


        if (error_num >= 100) {
            Button cancelButton = new Button("Cancel");

            cancelButton.setId("button");

            cancelButton.setOnAction(
                    event -> this.close()
            );

            ErrorContainer.getChildren().add(1, cancelButton);
        } else if (error_num == 1) {
            Button yesButton = new Button("Yes");
            Button noButton = new Button("No");
            HBox ButtonContainer = new HBox();

            yesButton.setId("button");
            noButton.setId("button");

            yesButton.setOnAction(
                    event -> {
                        user_confirmed.accept(true);
                        this.close();
                    }
            );

            noButton.setOnAction(
                    event -> {
                        user_confirmed.accept(false);
                        this.close();
                    }
            );

            ButtonContainer.setSpacing(10);
            ButtonContainer.getChildren().add(0, yesButton);
            ButtonContainer.getChildren().add(1, noButton);
            ButtonContainer.setAlignment(Pos.CENTER);

            ErrorContainer.getChildren().add(1, ButtonContainer);
        }

        root.setTop(Main.createCloseBar(this));
        root.setCenter(ErrorContainer);

        Scene scene = new Scene(root);

        /*
                String cssPath = Objects.requireNonNull(
                getClass().getResource("error.css")
        ).toExternalForm();
         */
        
        scene.getStylesheets().add(new File(ErrorPath).toURI().toString());

        this.initStyle(StageStyle.UNDECORATED);
        this.setResizable(false);
        this.setWidth(200);
        this.setHeight(200);
        this.setScene(scene);
        this.show();

    }
}
