import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Main  extends Application {

    private static boolean SearchisOpen = false;  // Prevent from the user open multiple search Windows
    public static boolean HomeisOpen = true; // Prevent from the user to load Home and make the software slow

    public static Label clock;
    public static Label Status;
    public static TextField citySearchField;
    public static String HomeCity = Data.getCity();

    public static final String UIPath = "ui.css";
    public static BorderPane MainRoot;
    public static BorderPane CenterContent;
    public static VBox sidebarCities;  // Container for new Cities that was added by the user

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

        VBox topContainer = new VBox(closeBar, titleBar);
        CenterContent = createHome();

        sidebarCities.setSpacing(40);
        sidebarCities.setFillWidth(true);

        MainRoot.setTop(topContainer);
        MainRoot.setCenter(CenterContent);  // Set the Home Stage on the center
        MainRoot.setLeft(sidebarCities);

        InputStream stream = new FileInputStream("data\\status\\weather-app.png");

        Scene scene = new Scene(MainRoot);

        primaryStage.initStyle(StageStyle.UNDECORATED);  // Hide the OS closeBar
        primaryStage.getIcons().add(new Image(stream));
        primaryStage.setResizable(false); // Prevent from the user to resize the window
        primaryStage.setTitle("Weather App");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void Error() {  // todo: continue to work on this
        Stage errorStage = new Stage();
        BorderPane root = new BorderPane();

        HBox closeContainer = new HBox();
        VBox ErrorContainer = new VBox();
        HBox ButtonContainer = new HBox();

        Label Error = new Label("Error Occurred");
        Button CancelButton = new Button("Cancel");

        CancelButton.setOnAction(
                event-> {
                    errorStage.close();
                }
        );

        closeContainer.getChildren().add(0, createCloseBar(errorStage));
        ErrorContainer.getChildren().add(0, Error);
        ErrorContainer.setAlignment(Pos.CENTER);
        ButtonContainer.getChildren().add(0, CancelButton);
        ButtonContainer.setAlignment(Pos.BOTTOM_CENTER);

        Scene scene = new Scene(root);

        errorStage.setScene(scene);
        errorStage.setWidth(200);
        errorStage.setHeight(200);
        errorStage.show();
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
                        try {
                            addCity.addTab(data); // Add the city to the GUI
                            SearchisOpen = false;  // Switch to off that the user can open again the window
                            searchStage.close();  // Closing the window
                        } catch (Exception e) {  // todo: Make an error message
                            e.printStackTrace();
                        }
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
                        Error();
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
        HBox ButtonContainer = new HBox();
        addCity.Tabs = new Button[12];

        addCity.Tabs[0] = new Button("Home");  // Saving the first Button for Home Stage that will display location's weather
        Button homeButton = addCity.Tabs[0];  // Creating variable name for homeButton
        homeButton.setId("newButton");  // Implement CSS

        homeButton.setOnAction(
                event -> {
                    if (!HomeisOpen) {
                        HomeisOpen = true;
                        switchHome();
                        addCity.currentLayout = null;
                    }
                }
        );

        HBox.setHgrow(ButtonContainer, Priority.ALWAYS);
        ButtonContainer.setAlignment(Pos.TOP_CENTER);
        ButtonContainer.setFillHeight(true);
        ButtonContainer.setSpacing(10);
        ButtonContainer.getChildren().add(homeButton);

        VBox.setVgrow(sidebar, Priority.ALWAYS);
        // sidebar.setFillWidth(true);
        sidebar.setPrefWidth(150);
        sidebar.setPadding(new Insets(2));
        sidebar.setSpacing(10);  // Set a space between buttons
        sidebar.getStyleClass().add("sidebar");
        sidebar.getChildren().add(0, ButtonContainer);

        return sidebar;
    }

    private BorderPane createHome() {
        Data.createClock();
        clock = new Label();
        Status = new Label();

        Group[] ForecastTable = new Group[5];
        Rectangle[] forecastData = new Rectangle[5];
        Label[] ForecastLabel = new Label[5];
        Label[] Days = new Label[5];
        ImageView[] forecastStatus = new ImageView[5];

        double[] temperatureForecast = Data.getForecast(HomeCity);

        BorderPane root = new BorderPane();
        HBox clockContainer = new HBox();
        HBox forecastContainer = new HBox();
        VBox dataContainer = new VBox();

        Label City = new Label(HomeCity); // Contains the user's city location based on Public IP Address
        Label HomeTemperature = new Label(Data.basedTemperatureLocation() + " °C");

        // Implement CSS
        clock.setId("Clock");
        HomeTemperature.setId("basedTemperature");
        City.setId("city");

        // Top Container
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        clockContainer.setPadding(new Insets(10));
        clockContainer.getChildren().addAll(Status, region, clock);

        // Central data
        dataContainer.setPadding(new Insets(0, 10, 0, 0));
        dataContainer.setSpacing(5);
        dataContainer.getChildren().add(0, City);
        dataContainer.getChildren().add(1, HomeTemperature);
        dataContainer.setAlignment(Pos.TOP_CENTER);

        // Forecast data
        try {
            if (temperatureForecast.length == forecastData.length) {

                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE");
                double labelX = 10;
                double labelY = 10;

                for (int i = forecastData.length - 1; i > -1; i--) {
                    int temperature = (int) temperatureForecast[i];
                    LocalDate date = today.plusDays(i + 1);
                    Image status = Data.Status("sunny");

                    forecastStatus[i] = new ImageView();
                    forecastStatus[i].setImage(status);
                    forecastStatus[i].setX(labelX);
                    forecastStatus[i].setY(labelY + 70);

                    Days[i] = new Label(date.format(formatter));
                    Days[i].setId("Day");
                    Days[i].setLayoutX(labelX);
                    Days[i].setLayoutY(labelY);

                    ForecastLabel[i] = new Label(temperature + " °C");
                    ForecastLabel[i].setId("Forecast");
                    ForecastLabel[i].setLayoutX(labelX);
                    ForecastLabel[i].setLayoutY(labelY + 40);

                    forecastData[i] = createForecastSquare();
                    forecastData[i].setOpacity(0.4);
                    forecastData[i].setFill(Color.BLACK);

                    ForecastTable[i] = new Group(forecastData[i], ForecastLabel[i],
                            Days[i], forecastStatus[i]);
                    forecastContainer.getChildren().add(0, ForecastTable[i]);
                }
            }
        } catch (FileNotFoundException e) {  // todo: Make an error
            e.printStackTrace();
        }

        forecastContainer.setSpacing(10);
        forecastContainer.setPadding(new Insets(5));
        forecastContainer.setAlignment(Pos.BOTTOM_CENTER);

        root.setTop(clockContainer);
        root.setCenter(dataContainer);
        root.setBottom(forecastContainer);
        Clock.setStatus(root);

        for (String search : addCity.Cities) {
            if (search.equals(getHome())) {
                return root;
            }
        }

        getHome();
        return root;
    }

    public static Rectangle createForecastSquare() {
        return new Rectangle(113, 113);
    }

    public static String getHome() {
        String[][] Home = Data.searchResult(HomeCity);
        String addToCities = Home[0][0] + ", " + Home[3][0] + " " + Home[2][0];
        addCity.Cities.add(addToCities);

        return addToCities;
    }

    private void switchHome() {
        BorderPane home = createHome();
        MainRoot.setCenter(home);
    }

    private static CloseBar createCloseBar(Stage stage) {
        try {
            return new CloseBar(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}