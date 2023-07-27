import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
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
import java.util.function.Consumer;

public class Main  extends Application {

    private static boolean SearchisOpen = false;  // Prevent from the user open multiple search Windows
    public static boolean HomeisOpen = true; // Prevent from the user to load Home and make the software slow
    private boolean addHome = false;

    public static Label clock;
    public static Label Status;
    public static Button removeButton;
    public static TextField citySearchField;
    public static String HomeCity = Data.getCity();
    private String HomeAdd = null;

    public static final String UIPath = "ui.css";
    public static BorderPane MainRoot;
    public static BorderPane CenterContent;
    public static VBox sidebarCities;  // Container for new Cities that was added by the user
    public static HBox titleBar;


    @Override
    public void start(Stage primaryStage) throws Exception {  // Main central Window
        init();  // Implement the css

        // Root layout
        MainRoot = new BorderPane();

        HBox closeBar = createCloseBar(primaryStage);
        HBox titleBar = titleBar();
        sidebarCities = createSidebar();

        VBox topContainer = new VBox(closeBar, titleBar);
        CenterContent = createHome();

        MainRoot.setTop(topContainer);  // Setting the CloseBar and titlebar on top
        MainRoot.setCenter(CenterContent);  // Set the Home Stage on the center
        MainRoot.setLeft(sidebarCities);  // Set the Sidebar on the left

        InputStream stream = new FileInputStream("data\\status\\weather-app.png");

        Scene scene = new Scene(MainRoot);

        primaryStage.initStyle(StageStyle.UNDECORATED);  // Hide the OS closeBar
        primaryStage.getIcons().add(new Image(stream));  // Set the weather Icon
        primaryStage.setResizable(false); // Prevent from the user to resize the window
        primaryStage.setTitle("Weather App");  // Title set
        primaryStage.setWidth(800);  // Determined the width and height
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
                        try {
                            addCity.addTab(data); // Add the city to the GUI
                            SearchisOpen = false;  // Switch to off that the user can open again the window
                            searchStage.close();  // Closing the window
                        } catch (Exception e) {
                            Error.raiseError(100);
                        }
                    } else {
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
        titleBar = new HBox();  // Container
        Label title = new Label("Weather App");
        Button plusButton = new Button();
        removeButton = new Button();

        // Implement the CSS
        plusButton.setId("plus-button");
        title.setId("title");
        removeButton.setId("remove-button");

        plusButton.setOnAction(  // Open the search window to add cities to the app
                event -> {
                    if (!SearchisOpen) {
                        search();
                    }
                }
        );

        removeButton.setOnAction(
                event -> {
                    for (Node node : sidebarCities.getChildren()) {  // Scanning the buttons in the sidebar
                        if (node instanceof Button) {  // Checking if the node is a button
                            if (((Button) node).getText()
                                    .equals(addCity.currentLayout)) {  // Checking if it's on the current layer
                                showConfirmation(  // Asking the user permission
                                        result -> {
                                            boolean decision = result;
                                            if (decision) {
                                                sidebarCities.getChildren().remove(node);  // Bye, bye!

                                                switchHome(); // Get back to home
                                                addCity.currentLayout = null;  // Set the currentLayout to null
                                                HomeisOpen = true;  // On home
                                                titleBar.getChildren().get(2).setVisible(false); // Hide the remove button
                                            } else {
                                                System.out.println(false);  // Will display false at the console
                                            }
                                        }
                                );
                            }
                        }
                    }
                }
        );

        Region region = new Region();  // Making space between the buttons and the title
        HBox.setHgrow(region, Priority.ALWAYS);
        titleBar.setPadding(new Insets(5));
        titleBar.setId("titleBar");
        titleBar.getChildren().addAll(title, region, removeButton, plusButton);

        titleBar.getChildren().get(2).setVisible(false);

        return titleBar;
    }

    private VBox createSidebar() {  // Sidebar, mostly contain new cities that was added by the user
        VBox sidebar = new VBox();

        addCity.Tabs[0] = new Button("Home");  // Saving the first Button for Home Stage that will display location's weather
        Button homeButton = addCity.Tabs[0];  // Creating variable name for homeButton

        homeButton.getStyleClass().add("sidebar-button"); // Implement CSS

        homeButton.setOnAction(
                event -> {
                    if (!HomeisOpen) {
                        titleBar.getChildren().get(2).setVisible(false);
                        addCity.currentLayout = null;
                        HomeisOpen = true;
                        switchHome();
                    }
                }
        );


        VBox.setVgrow(sidebar, Priority.ALWAYS);
        sidebar.setPrefWidth(150);
        sidebar.setPadding(new Insets(10));
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setSpacing(0);  // Set a space between buttons
        sidebar.getStyleClass().add("sidebar");
        sidebar.getChildren().add(0, homeButton);

        return sidebar;
    }

    private BorderPane createHome() {

        Data.createClock();
        clock = new Label();
        Status = new Label();

        Group[] ForecastTable = new Group[5];  // Will group all the data to the square, contains 5 because 5 different data
        Rectangle[] forecastData = new Rectangle[5];  // The data will present in this square
        Label[] ForecastLabel = new Label[5];  // Array of temperature forecast based on the location
        Label[] Days = new Label[5];  // Array of days, it will contain only 5 days
        ImageView[] forecastStatus = new ImageView[5];

        double[] temperatureForecast = Data.getForecast(HomeCity);  // Getting the forecast for the next 5 days

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
            if (temperatureForecast.length == forecastData.length) {  // If the forecast data is equal to the array length

                LocalDate today = LocalDate.now();  // Getting today's
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE");  // Changing this to day string
                double labelX = 10;  // Graph positioning
                double labelY = 10;

                for (int i = forecastData.length - 1; i > -1; i--) {  // Reverse loop to add all the data in certain order
                    int temperature = (int) temperatureForecast[i];
                    LocalDate date = today.plusDays(i + 1);  // Increasing by one to display tomorrow data
                    Image status = Data.Status("sunny");  // todo: Getting the data status

                    forecastStatus[i] = new ImageView();  // Image
                    forecastStatus[i].setImage(status);  // Get the image by status of the weather
                    forecastStatus[i].setX(labelX);
                    forecastStatus[i].setY(labelY + 70);

                    Days[i] = new Label(date.format(formatter));  // Will present the string of the day (Sunday, Monday and etc)
                    Days[i].setId("Day");
                    Days[i].setLayoutX(labelX);
                    Days[i].setLayoutY(labelY);

                    ForecastLabel[i] = new Label(temperature + " °C");  // Will display the temperature number
                    ForecastLabel[i].setId("Forecast");
                    ForecastLabel[i].setLayoutX(labelX);
                    ForecastLabel[i].setLayoutY(labelY + 40);

                    forecastData[i] = createForecastSquare();  // Creating the square =]
                    forecastData[i].setOpacity(0.4);  // Weaken the color that will have a clear background effect
                    forecastData[i].setFill(Color.BLACK);  // Set to black

                    ForecastTable[i] = new Group(forecastData[i], ForecastLabel[i],  // Assemble all the elements into one
                            Days[i], forecastStatus[i]);
                    forecastContainer.getChildren().add(0, ForecastTable[i]);  // Add the group to the layout
                }
            }
        } catch (FileNotFoundException e) {
            Error.raiseError(348);  // Let's try to prevent this error :D
        }

        forecastContainer.setSpacing(10);
        forecastContainer.setPadding(new Insets(5));
        forecastContainer.setAlignment(Pos.BOTTOM_CENTER);

        root.setTop(clockContainer);
        root.setCenter(dataContainer);
        root.setBottom(forecastContainer);
        Clock.setStatus(root);

        if (!addHome) {  // Trigger only once the list addition
            HomeAdd = addCity.getCity(HomeCity);  // Add the home city based on the IP
            addHome = true; // Set the boolean to true and will signal the software that the homeCity added to the list
        } else {
            for (String search : addCity.Cities) {
                if (search.equals(HomeAdd)) {
                    return root;
                }
            }
        }

        return root;
    }

    public static Rectangle createForecastSquare() {  // Creating square
        return new Rectangle(113, 113);
    }

    private void switchHome() {  // Switch home layout
        BorderPane home = createHome();
        MainRoot.setCenter(home);
    }

    public void showConfirmation(Consumer<Boolean> callback) {  // Method that will ask user true or false (Yes or no message)
        Error.raiseError(1);
        Error.user_confirmed = callback;
    }

    public static CloseBar createCloseBar(Stage stage) {  // Creating the CloseBar that will contain the minimize and close buttons
        try {
            return new CloseBar(stage);
        } catch (IOException e) {
            Error.raiseError(0);
            e.printStackTrace();
        }
        return null;
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

    public static void main(String[] args) {
        launch(args);
    }

}