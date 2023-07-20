import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

public class App extends Application {

    // public static Label TemperatureLabel;
    public String[] data_location = Data.getDataLocation();
    private TextField cityTextField;
    private TableView<Search> tableView;
    private Label liveClockLabel;
    private VBox centerContent;
    private VBox sidebarContent;
    private boolean isOpen = false;
    private Button Tabs;

    @Override
    public void start(Stage primaryStage) throws Exception {  // Main window
        // Root layout
        BorderPane root = new BorderPane();

        // Create sidebar
        sidebarContent = createSidebarContent();
        HBox sidebar = new HBox(sidebarContent);
        sidebar.setPrefWidth(150);

        // Create center content
        centerContent = createCenterContent();

        root.setTop(createTitleBar(primaryStage));
        root.setLeft(sidebar);
        root.setCenter(centerContent);

        // Set up scene
        Scene scene = new Scene(root);

        try {
            scene.getStylesheets().add(Objects.requireNonNull
                    (getClass().getResource("test.css")).toExternalForm());
            sidebar.getStyleClass().add("sidebar");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Test App");
        primaryStage.setResizable(false); // Prevent window resizing
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private VBox createCenterContent() {
        VBox centerContent = new VBox();
        GridPane centerPane = new GridPane();

        String city = data_location[0];
        Label basedCityLabel = new Label(city);
        Label basedTemperatureLabel = new Label(Data.basedTemperatureLocation() + " °C");
        liveClockLabel = new Label();

        // CSS
        liveClockLabel.setId("Clock");
        basedCityLabel.setId("city");
        basedTemperatureLabel.setId("basedTemperature");

        centerPane.setPadding(new Insets(10));
        centerPane.setVgap(10);
        centerPane.setHgap(10);

        centerPane.add(basedCityLabel, 0, 5, 2, 1);
        centerPane.add(basedTemperatureLabel, 0, 6, 2, 2);
        centerPane.add(liveClockLabel, 1, 0, 1, 2);

        // Set constraints to place the live clock on the top right
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS); // Make the first column grow to fill any remaining space
        centerPane.getColumnConstraints().addAll(column1);

        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(Priority.ALWAYS); // Make the second row grow to fill any remaining space
        centerPane.getRowConstraints().addAll(row1, row2);

        // Align the city and temperature labels to the center
        GridPane.setHalignment(basedCityLabel, HPos.CENTER);
        GridPane.setHalignment(basedTemperatureLabel, HPos.CENTER);

        centerContent.getChildren().add(centerPane);
        centerContent.setPadding(new Insets(20));

        startLiveClock(); // Live Clock

        return centerContent;
    }

    private VBox createSidebarContent() {
        VBox sidebarContent = new VBox();
        VBox buttonPane = new VBox(); // New VBox to hold the buttons
        // GridPane sidebarGrid = new GridPane();

        Button minusButton = new Button();
        Button plusButton = new Button();
        Label titleSidebar = new Label("Weather App");

        plusButton.setOnAction(
                event -> {
                    if (!isOpen) {
                        search();
                    }
                }
        );

        // minusButton.setOnAction();
        this.Tabs = new Button();

        plusButton.setId("plus-button");
        titleSidebar.setId("title");
        minusButton.setId("minus-button");

        // Set VBox properties to fill the available space
        VBox.setVgrow(buttonPane, Priority.ALWAYS);
        buttonPane.setFillWidth(true);

        // Add titleSidebar and buttonPane to the sidebarContent
        sidebarContent.setPadding(new Insets(2));
        sidebarContent.setSpacing(5);

        /*
        sidebarGrid.add(titleSidebar, 0, 0, 8, 1);
        sidebarGrid.add(plusButton, 1, 5, 1, 1);
        sidebarGrid.add(minusButton, 2, 5, 1, 1);
        sidebarGrid.setHgap(5); // Add some horizontal gap between buttons
        sidebarContent.getChildren().addAll(sidebarGrid, sidebarContent);
         */

        buttonPane.setSpacing(5); // Add spacing between buttons
        buttonPane.getChildren().addAll(plusButton, minusButton);
        sidebarContent.setPadding(new Insets(2));
        sidebarContent.setSpacing(5);
        sidebarContent.getChildren().addAll(titleSidebar, buttonPane); // Add sideBarContent to the VBox

        return sidebarContent;
    }


    private void startLiveClock() {
        Timeline timeline = new Timeline(new KeyFrame
                (Duration.seconds(1), event -> updateLoveClock()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateLoveClock() {
        long currentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(currentTimeMillis);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        if (hour > 19 || hour < 5) {
            centerContent.getStyleClass().add("night");
        } else {
            centerContent.getStyleClass().add("day");
        }

        String formattedDateTime = String.format("%04d-%02d-%02d %02d:%02d:%02d",
                year, month, day, hour, minute, second);

        liveClockLabel.setText(formattedDateTime);
    }

    private void search() {
        isOpen = true;
        BorderPane root = new BorderPane();
        HBox addButtonContainer = new HBox();
        Stage searchStage = new Stage();

        cityTextField = new TextField();
        Button performSearch = new Button();
        Button addButton = new Button("Add");

        performSearch.setOnAction(
                event -> performSearch()
        );

        addButton.setOnAction(
                event -> {
                    String[] data = addData(tableView);
                    if (data != null) {
                        addTabToSidebar(data);
                        isOpen = false;
                        searchStage.close();
                    } else {
                        isOpen = true;
                    }
                }
        );

        // Search Results Container + Table
        VBox searchResultsContainer = new VBox(10);
        tableView = new TableView<>();
        TableColumn<Search, String> cityColumn = new TableColumn<>("City");
        TableColumn<Search, String> countryColumn = new TableColumn<>("Country");
        TableColumn<Search, String> provinceColumn = new TableColumn<>("Province");

        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        provinceColumn.setCellValueFactory(new PropertyValueFactory<>("province"));

        // Set the placeholder to a custom Label to hide the default message
        Label customPlaceholder = new Label("");
        tableView.setPlaceholder(customPlaceholder);

        tableView.getColumns().addAll(cityColumn, countryColumn, provinceColumn);
        searchResultsContainer.getChildren().add(tableView);

        // Content Layout
        GridPane contentLayout = new GridPane();
        contentLayout.setPadding(new Insets(10));
        contentLayout.setVgap(10);
        contentLayout.setHgap(10);
        contentLayout.add(cityTextField, 4, 0, 1, 1);
        contentLayout.add(performSearch, 5, 0, 1, 1);
        contentLayout.add(searchResultsContainer, 3, 1, 2, 16);

        addButtonContainer.setAlignment(Pos.CENTER);
        addButtonContainer.getChildren().add(addButton);

        root.setTop(createTitleBar(searchStage));
        root.setCenter(contentLayout);
        root.setBottom(addButtonContainer);
        Scene scene = new Scene(root);

        // CSS
        try {
            scene.getStylesheets().add(Objects.requireNonNull
                    (getClass().getResource("test.css")).toExternalForm());
            performSearch.setId("magnifying-glass");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        searchStage.initStyle(StageStyle.UNDECORATED);
        searchStage.setTitle("Test App");
        searchStage.setResizable(false);
        searchStage.setWidth(350);
        searchStage.setHeight(300);
        searchStage.setScene(scene);
        searchStage.show();

        TitleBar.closeButton.setOnAction(
                event -> {
                    isOpen = false;
                    searchStage.close();
                }
        );
    }

    private void performSearch() {
        try {
            String searchString = cityTextField.getText();
            String[][] results = Data.searchResult(searchString);
            ObservableList<Search> data = FXCollections.observableArrayList();

            // results[0] = City; results[1] = Country; results[2] = Province
            for (int i = 0; i < results[0].length; i++) {
                if (results[0][i] != null || results[1][i] != null || results[2][i] != null) {
                    data.add(new Search(results[0][i], results[1][i], results[2][i]));
                }
                else {
                    i = results[0].length + 1;
                }
            }
            tableView.setItems(data);  // Update the TableView with the new data
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private String[] addData(TableView<Search> tableView) {
        try {
            ObservableList<Search> selectedItems = tableView.getSelectionModel().getSelectedItems();
            if (selectedItems != null && !selectedItems.isEmpty()) {
                String[] selectedData = new String[3];

                for (Search search : selectedItems) {
                    selectedData[0] = search.getCity();
                    selectedData[1] = search.getCountry();
                    selectedData[2] = search.getProvince();
                }
                return selectedData;
            } else {
                return null;
            }
        } catch (NullPointerException e) {
            return null;
        }
    }

    private TitleBar createTitleBar(Stage stage) {
        return new TitleBar(stage);
    }

    private void addTabToSidebar(String[] data) {
        if (data != null && data.length >= 3) {
            String city = data[0];
            // String country = data[1];
            // String province = data[2];
            boolean alreadyExists = false;

            // Check if the button with the same city already exists in the sidebar
            for (Node node : sidebarContent.getChildren()) {
                if (node instanceof VBox sidebarGrid) {
                    for (Node button : sidebarGrid.getChildren()) {
                        if (button instanceof Button && ((Button) button).getText().equals(city)) {
                            alreadyExists = true;
                            break;
                        }
                    }
                }
                if (alreadyExists) {
                    break;
                }
            }

            // If the button doesn't exist, add it to the sidebar
            if (!alreadyExists) {
                Tabs = new Button(city);
                Tabs.setOnAction(
                        event -> {
                            System.out.println(city);
                        }
                );
                Tabs.setId("newButton");

                VBox sidebarGrid = new VBox(); // Create a new VBox for the button
                sidebarGrid.getChildren().addAll(Tabs); // Add the button to the new VBox
                sidebarGrid.setSpacing(5); // Add spacing between buttons

                sidebarContent.getChildren().add(sidebarGrid); // Add the new VBox to the sidebar
            }
        } else {
            System.out.println("Invalid data. Cannot add tab to the sidebar.");
        }
    }

}
