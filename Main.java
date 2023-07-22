import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class Main  extends Application {

    private static boolean SearchisOpen = false;  // Prevent from the user open multiple search Windows
    public static boolean HomeisOpen = true; // Prevent from the user to load Home and make the software slow

    public static final String UIPath = "ui.css";
    public static BorderPane MainRoot;
    public static BorderPane CenterContent;
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
        CenterContent = createHome();

        MainRoot.setTop(topContainer);
        MainRoot.setCenter(CenterContent);  // Set the Home Stage on the center
        MainRoot.setLeft(sidebar);

        Scene scene = new Scene(MainRoot);

        primaryStage.initStyle(StageStyle.UNDECORATED);  // Hide the OS closeBar
        primaryStage.setResizable(false); // Prevent from the user to resize the window
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void search() {
        SearchisOpen = true;
        Stage searchStage = new Stage();

        BorderPane root = new BorderPane();
        HBox BottomContainer = new HBox();
        HBox SearchContainer = new HBox();
        VBox topContainer = new VBox(); // Containing CloseBar + Search Field
        VBox centerContainer = new VBox();

        citySearchField = new TextField();  // Search field for the city search engine

        // Buttons
        Button searchButton = new Button();
        Button addButton = new Button("Add");

        // Handling top Container
        HBox.setMargin(SearchContainer, new Insets(0, 5, 0, 0));
        SearchContainer.setAlignment(Pos.TOP_CENTER);
        SearchContainer.setSpacing(10);
        SearchContainer.getChildren().add(0, citySearchField);
        SearchContainer.getChildren().add(1, searchButton);
        topContainer.getChildren().add(0, createCloseBar(searchStage)); // Get the CloseBar first on top
        topContainer.getChildren().add(1, SearchContainer); // Get the Search Container second that will be below CloseBar

        // Handling Center
        centerContainer.setAlignment(Pos.TOP_CENTER);
        centerContainer.setSpacing(10);
        centerContainer.getChildren().add(Data.searchResultsContainer());

        // Handling Bottom
        HBox.setMargin(BottomContainer, new Insets(10));
        BottomContainer.setAlignment(Pos.CENTER);
        BottomContainer.getChildren().add(addButton);

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
        searchButton.setId("magnifying-glass");
        citySearchField.setId("text-field");
        addButton.setId("addButton");
        root.getStyleClass().add("root");

        root.setTop(topContainer);  // Set topContainer as the top of the root
        root.setCenter(centerContainer);  // Set DataContainer as the center of the root
        root.setBottom(BottomContainer);

        Scene scene = new Scene(root);

        // Implement CSS
        String cssPath = Objects.requireNonNull(
                Main.class.getResource("search.css")).toExternalForm();
        scene.getStylesheets().add(cssPath);

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

    private VBox createSidebar() {  // Sidebar, mostly contain new cities that was added by the user
        VBox sidebar = new VBox();
        addCity.Tabs = new Button[12];

        addCity.Tabs[0] = new Button("Home");  // Saving the first Button for Home Stage that will display location's weather
        Button homeButton = addCity.Tabs[0];  // Creating variable name for homeButton
        homeButton.setId("newButton");  // Implement CSS

        homeButton.setOnAction(
                event -> {
                    if (!HomeisOpen) {
                        HomeisOpen = true;
                        switchHome();
                    }
                }
        );

        VBox.setVgrow(sidebar, Priority.ALWAYS);
        sidebar.setFillWidth(true);
        sidebar.setPrefWidth(150);
        sidebar.setPadding(new Insets(2));
        sidebar.setSpacing(10);  // Set a space between buttons
        sidebar.getStyleClass().add("sidebar");
        sidebar.getChildren().addAll(addCity.Tabs[0]);

        return sidebar;
    }

    private BorderPane createHome() {
        Data.createClock();
        clock = new Label();

        BorderPane root = new BorderPane();
        HBox clockContainer = new HBox();
        VBox dataContainer = new VBox();
        // todo: make a forecast container

        Label HomeCity = new Label(Data.getCity()); // Contains the user's city location based on Public IP Address
        Label HomeTemperature = new Label(Data.basedTemperatureLocation() + " °C");

        // Implement CSS
        clock.setId("Clock");
        HomeTemperature.setId("basedTemperature");
        HomeCity.setId("city");

        // Top Container
        clockContainer.setPadding(new Insets(10));
        clockContainer.setAlignment(Pos.TOP_RIGHT);
        clockContainer.getChildren().add(clock);


        // Central data
        dataContainer.setPadding(new Insets(0, 10, 0, 0));
        dataContainer.setSpacing(5);
        dataContainer.getChildren().add(0, HomeCity);
        dataContainer.getChildren().add(1, HomeTemperature);
        dataContainer.setAlignment(Pos.TOP_CENTER);

        root.setTop(clockContainer);
        root.setCenter(dataContainer);
        Clock.setCityBackground(root);

        return root;


    }

    private void switchHome() {
        BorderPane home = createHome();
        MainRoot.setCenter(home);
    }

    private static CloseBar createCloseBar(Stage stage) {
        return new CloseBar(stage);
    }

}