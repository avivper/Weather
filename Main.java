import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.Objects;

public class Main  extends Application{

    public static boolean SearchisOpen = false;  // Prevent from the user open multiple search Windows
    public static TextField citySearchField;
    private static final String UIPath = "data\\ui.css";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {  // Main central Window
        // Root layout
        System.out.println(26);
        BorderPane root = new BorderPane();

        HBox closeBar = createCloseBar(primaryStage);
        HBox titleBar = Layouts.titleBar();
        VBox topContainer = new VBox(titleBar, closeBar);

        root.setTop(topContainer);
        root.setLeft(Layouts.createSidebar());
        root.setCenter(Layouts.createCenterContent());  // Set the Home Stage on the center

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource(UIPath)).toExternalForm()
        );

        primaryStage.initStyle(StageStyle.UNDECORATED);  // Hide the OS closeBar
        primaryStage.setResizable(false); // Prevent from the user to resize the window
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void search() {
        SearchisOpen = true;
        BorderPane root = new BorderPane();
        HBox addButtonContainer = new HBox();
        VBox searchFieldContainer = new VBox();
        VBox topContainer = new VBox();
        Stage searchStage = new Stage();

        citySearchField = new TextField();

        Button performSearch = new Button();
        Button addButton = new Button("Add");

        searchFieldContainer.setAlignment(Pos.CENTER);
        searchFieldContainer.getChildren().add(0, citySearchField);
        searchFieldContainer.getChildren().add(1, performSearch);    // todo: make space between citySearchField to performSearch

        addButtonContainer.setAlignment(Pos.CENTER);
        addButtonContainer.getChildren().add(0, addButton);

        HBox closeBar = createCloseBar(searchStage);  // Creating CloseBar
        topContainer.getChildren().add(0, closeBar);  // Get the CloseBar on top
        topContainer.getChildren().add(1, searchFieldContainer);   // Making Java to place it below CloseBar

        performSearch.setOnAction(
                event -> Data.performSearch()
        );

        addButton.setOnAction(
                event -> {
                    String[] data = Data.getData(Data.Table);  // Receive the data from the user
                    if (data != null) {  // Check if it's not null
                        Cities.addCity(data);  // Add the city to the GUI
                        SearchisOpen = false;  // Switch to off that the user can open again the window
                        searchStage.close(); // Closing the window
                    } else { // todo: Make an error message
                        SearchisOpen = true;  // Keeping the user on the window
                    }
                }
        );

        // Setting the elements on the GUI
        root.setTop(topContainer);
        root.setCenter(Data.searchResultsContainer());
        root.setBottom(addButtonContainer);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(  // Implementing the CSS
                Main.class.getResource(UIPath)).toExternalForm()
        );
        performSearch.setId("magnifying-glass");  // todo: fix the png

        searchStage.initStyle(StageStyle.UNDECORATED);  // Hide the OS Close bar and the Title Bar
        searchStage.setResizable(false);  // Prevent from window to   resize the window
        searchStage.setWidth(350);
        searchStage.setHeight(300);
        searchStage.setScene(scene);
        searchStage.show();

        CloseBar.closeButton.setOnAction(  // After the search window is closed, the user can now open a new search window
                event -> {
                    SearchisOpen = false;  // Symbolizes the plusButton that he can open another search Window
                    searchStage.close();  // Closing the Window
                }
        );
    }

    private static CloseBar createCloseBar(Stage stage) {
        return new CloseBar(stage);
    }

}