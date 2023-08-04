package weather.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import weather.widgets.CloseBar;
import weather.widgets.Bars;

import java.io.*;

public class App extends Application {

    private final String title = "Weather App";
    public static BorderPane MainRoot = new BorderPane();
    public static VBox sideBar;
    public static HBox TitleBar;

    @Override
    public void init() {
        String UIPath = "data\\css\\ui.css";
        Application.setUserAgentStylesheet(new File(UIPath)
                .toURI().toString());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        init();

        String path = "data\\save";
        File file = new File(path);
        Bars bars = new Bars();

        sideBar =  bars.createSideBar();
        TitleBar = bars.titleBar(primaryStage);

        if (file.exists()) {
            bars.load();
        }

        VBox topContainer = new VBox(
                createCloseBar(primaryStage),
                TitleBar
        );

        MainRoot.setTop(topContainer);
        MainRoot.setCenter(createHome());
        MainRoot.setLeft(sideBar);

        InputStream stream = new FileInputStream("data\\icon.png");

        Scene scene = new Scene(MainRoot);

        primaryStage.initStyle(StageStyle.UNDECORATED);  // Hide the OS closeBar
        primaryStage.getIcons().add(new Image(stream));  // Set the weather Icon
        primaryStage.setResizable(false); // Prevent from the user to resize the window
        primaryStage.setTitle(title);  // Title set
        primaryStage.setWidth(800);  // Determined the width and height
        primaryStage.setHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private CloseBar createCloseBar(Stage stage) {  // Creating the CloseBar that will contain to minimize and close buttons
        try {
            return new CloseBar(stage);
        } catch (IOException e) {
            new Alerts(title, "Error code 408: Close Bar isn't loading", Alert.AlertType.ERROR, null);
            stage.close();
        }
        return null;
    }


    private BorderPane createHome() throws FileNotFoundException {
        City city = new City();
        return city.createHome();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
