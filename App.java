import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class App extends Application {

    public String[] data_location = Data.getDataLocation();
    private TextField cityTextField;
    private TableView<Search> tableView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Root Layout
        BorderPane root = new BorderPane();

        // Create sidebar
        VBox sidebarContent = createSidebarContent();
        HBox sidebar = new HBox(sidebarContent);
        sidebar.setPrefWidth(150);

        // Create center content
        VBox centerContent = createCenterContent();

        // Placing content
        root.setLeft(sidebar);
        root.setCenter(centerContent);

        Scene scene = new Scene(root);

        try {
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("test.css")).toExternalForm());
        } catch (NullPointerException e) {  // todo: make an error pop up message
            e.printStackTrace();
        }

        primaryStage.setTitle("Weather App"); // Set the title for the application
        primaryStage.setResizable(false);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.show();
    }

    private VBox createCenterContent() {
        VBox centerContent = new VBox();
        GridPane centerPane = new GridPane();

        String city = data_location[0]; // Gets the city based on the location of the user
        Label basedCityLabel = new Label(city);  // Display the based city
        Label basedTemperatureLabel = new Label(Data.basedTemperatureLocation() + " °C");

        basedCityLabel.setId("city");
        basedTemperatureLabel.setId("basedTemperature");

        centerPane.setPadding(new Insets(10));
        centerPane.setVgap(10);
        centerPane.setHgap(10);
        centerPane.add(basedCityLabel, 30, 1, 1, 1);
        centerPane.add(basedTemperatureLabel, 30, 2, 1, 1); // Change the row index for the temperature label

        centerContent.getChildren().add(centerPane);
        centerContent.setPadding(new Insets(20));

        return centerContent;
    }

    private VBox createSidebarContent() {
        VBox sidebarContent = new VBox();
        GridPane sidebarGrid = new GridPane();  // todo: change this to Grid pane

        // Button minusButton = new Button(); todo: remove content from sidebar
        Button plusButton = new Button();
        Label titleSidebar = new Label("Weather App");

        plusButton.setOnAction(
                e -> search()
        );

        plusButton.setId("plus-button");  // todo: fix the + pic
        titleSidebar.setId("title");

        sidebarGrid.add(titleSidebar, 0, 0);
        sidebarGrid.add(plusButton, 0, 1);
        // sidebarContent.setSpacing(2);
        // sidebarContent.setPadding(new Insets(20));
        sidebarContent.getChildren().add(sidebarGrid);

        return sidebarContent;
    }

    private void search() {
        Stage searchStage = new Stage();
        GridPane contentLayout = new GridPane();

        cityTextField = new TextField();
        Button performSearch = new Button();
        Button addButton = new Button("Add");

        performSearch.setOnAction(
                event -> performSearch()
        );

        // Search Results  Container + Table
        VBox searchResultsContainer = new VBox(10);
        tableView = new TableView<>();
        TableColumn<Search, String> cityColumn = new TableColumn<>("City");
        TableColumn<Search, String> countryColumn = new TableColumn<>("Country");
        TableColumn<Search, String> provinceColumn = new TableColumn<>("Province");

        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        provinceColumn.setCellValueFactory(new PropertyValueFactory<>("province"));

        // Set the placeholder to a custom label to hide the default message
        Label noData = new Label("No data available");
        tableView.setPlaceholder(noData);
        tableView.getColumns().addAll(cityColumn, countryColumn, provinceColumn);
        searchResultsContainer.getChildren().add(tableView);

        // Content Layout
        contentLayout.setPadding(new Insets(10));
        contentLayout.setVgap(10);
        contentLayout.setHgap(10);
        contentLayout.add(cityTextField, 4, 0, 1, 1);
        contentLayout.add(performSearch, 5, 0, 1, 1);
        contentLayout.add(searchResultsContainer, 3, 1, 2, 16);
        contentLayout.add(addButton, 4, 18, 2, 1);

        Scene scene = new Scene(contentLayout);

        // UI style with CSS
        try { // todo: make main CSS file
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("test.css")).toExternalForm());
            performSearch.setId("magnifying-glass");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        searchStage.setTitle("Weather App");
        searchStage.setResizable(false);
        searchStage.setWidth(350);
        searchStage.setHeight(300);
        searchStage.setScene(scene);
        searchStage.show();
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
                } else {  // todo: make an error message
                    i = results[0].length + 1;
                }
            }
            tableView.setItems(data);
        } catch (ArrayIndexOutOfBoundsException e) { // todo: make an error message
            e.printStackTrace();
        }
    }
}
