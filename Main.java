import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.util.Calendar;
import java.util.Objects;

public class Main  extends Application {

    private boolean SearchisOpen = false;  // Prevent from the user open multiple search Windows
    private static final String UIPath = "ui.css";

    public static BorderPane MainRoot;
    public static VBox CenterContent;
    public static Label clock;
    public static VBox sidebarCities;  // Container for new Cities that was added by the user
    public static TextField citySearchField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        Application.setUserAgentStylesheet(Objects.requireNonNull(
                getClass().getResource(UIPath)).toExternalForm()
        );
    }

    public static BorderPane getMainRoot() {
        return MainRoot;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {  // Main central Window
        init();

        // Root layout
        MainRoot = new BorderPane();

        HBox closeBar = createCloseBar(primaryStage);
        HBox titleBar = titleBar();
        sidebarCities = createSidebar();
        HBox sidebar = new HBox(sidebarCities);

        VBox topContainer = new VBox(closeBar, titleBar);
        CenterContent = createCenterContent();

        MainRoot.setTop(topContainer);
        MainRoot.setCenter(CenterContent);  // Set the Home Stage on the center
        MainRoot.setLeft(sidebar);

        Scene scene = new Scene(MainRoot);

        CenterContent.getStyleClass().add("day");
        Clock.DayNightBackground();

        primaryStage.initStyle(StageStyle.UNDECORATED);  // Hide the OS closeBar
        primaryStage.setResizable(false); // Prevent from the user to resize the window
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void search() {
        SearchisOpen = true;
        Stage searchStage = new Stage();

        BorderPane root = new BorderPane();
        HBox BottomContainer = new HBox();
        HBox SearchContainer = new HBox();
        HBox closeBar = createCloseBar(searchStage); // Creating CloseBar
        VBox table = Data.searchResultsContainer();  // Creating Table
        VBox DataContainer = new VBox();
        VBox topContainer = new VBox(); // Containing CloseBar + Search Field

        citySearchField = new TextField();  // Search field for the city search engine

        // Buttons
        Button searchButton = new Button("Search");
        Button addButton = new Button("Add");

        // Handling top Container
        SearchContainer.setAlignment(Pos.CENTER);
        SearchContainer.setPadding(new Insets(0, 5, 0, 0));
        SearchContainer.getChildren().add(0, citySearchField);
        SearchContainer.getChildren().add(1, searchButton);
        topContainer.getChildren().add(0, closeBar); // Get the CloseBar first on top
        topContainer.getChildren().add(1, SearchContainer); // Get the Search Container second that will be below CloseBar

        // Handling Center
        DataContainer.getChildren().add(table);

        // Handling Bottom
        BottomContainer.setAlignment(Pos.CENTER); // Align the button on the center
        BottomContainer.getChildren().addAll(addButton);

        searchButton.setOnAction(
                event -> Data.performSearch()
        );

        addButton.setOnAction(
                event -> {
                    String[] data = Data.getData(Data.Table); // Receive the data from the user
                    if (data != null) { // Check if it's not null
                        addCity.addTab(data); // Add the city to the GUI
                        SearchisOpen = false;  // Switch to off that the user can open again the window
                        searchStage.close();  // Closing the window
                    } else {  // todo: Make an error message
                        SearchisOpen = true; // Keeping the user on the window
                    }
                }
        );

        // Implement CSS
        // searchButton.setId("magnifying-glass ");
        citySearchField.setId("text-field");

        root.setTop(topContainer);  // Set topContainer as the top of the root
        root.setCenter(DataContainer);  // Set DataContainer as the center of the root
        root.setBottom(BottomContainer);

        Scene scene = new Scene(root);

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

    // Layouts
    private HBox titleBar() {
        HBox titleBar = new HBox();
        Label title = new Label("Weather App");
        Button plusButton = new Button();

        // Implement the CSS
        plusButton.setId("plus-button");
        title.setId("title");

        plusButton.setOnAction(  // Open the search window to add cities to the app
                event -> {
                    if (!SearchisOpen) {
                        search();
                    }
                }
        );

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        titleBar.setPadding(new Insets(5));
        titleBar.setSpacing(10);
        titleBar.setId("titleBar");
        titleBar.getChildren().addAll(title, region, plusButton);

        return titleBar;
    }

    private VBox createSidebar() {  // Side bar, mostly contain new cities that was added by the user
        VBox sidebar = new VBox();
        addCity.Tabs = new Button[12];

        addCity.Tabs[0] = new Button("Home");  // Saving the first Button for Home Stage that will display location's weather
        Button homeButton = addCity.Tabs[0];  // Creating variable name for homeButton
        homeButton.setId("newButton");  // Implement CSS

        VBox.setVgrow(sidebar, Priority.ALWAYS);
        sidebar.setFillWidth(true);
        sidebar.setPrefWidth(150);
        sidebar.setPadding(new Insets(2));
        sidebar.setSpacing(10);  // Set a space between buttons
        sidebar.getStyleClass().add("sidebar");
        sidebar.getChildren().addAll(addCity.Tabs[0]);

        return sidebar;
    }

    private VBox createCenterContent() {
        Data.createClock();
        clock = new Label();

        String city = Data.getCity();
        VBox centerContent = new VBox();
        GridPane dataContainer = new GridPane();

        Label HomeCity = new Label(city); // Contains the user's city location based on Public IP Address
        Label HomeTemperature = new Label(Data.basedTemperatureLocation() + " °C");

        // Implement the UI
        clock.setId("Clock");
        HomeTemperature.setId("basedTemperature");
        HomeCity.setId("city");

        dataContainer.setPadding(new Insets(10));
        dataContainer.setVgap(10);
        dataContainer.setHgap(10);
        dataContainer.add(HomeCity, 0, 5, 2, 1);
        dataContainer.add(HomeTemperature, 0, 6, 2 , 2);
        dataContainer.add(clock, 1, 0, 1, 2);

        // Set constraints to place the live clock on the top right
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS); // Make the first column grow to fill any remaining space
        dataContainer.getColumnConstraints().addAll(column1);

        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(Priority.ALWAYS); // Make the second row grow to fill any remaining space
        dataContainer.getRowConstraints().addAll(row1, row2);

        // Align the city and temperature labels to the center
        GridPane.setHalignment(HomeTemperature, HPos.CENTER);
        GridPane.setHalignment(HomeCity, HPos.CENTER);

        centerContent.getChildren().add(dataContainer);
        centerContent.setPadding(new Insets(20));


        return centerContent;
    }

    private static CloseBar createCloseBar(Stage stage) {
        return new CloseBar(stage);
    }




}