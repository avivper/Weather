package weather.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import weather.main.Alerts;
import weather.widgets.CloseBar;
import weather.widgets.Bars;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

public class Search extends Stage {

    private final TextField citySearchField = new TextField();
    private final TableView<Results> Table = new TableView<>();
    public static boolean isSearchStageOpen = false;

    public Search(Stage stage) throws FileNotFoundException {
        initOwner(stage);
        setupSearch();
    }

    private void setupSearch() throws FileNotFoundException {
        CloseBar CloseBar = new CloseBar(this);

        BorderPane root = new BorderPane();
        HBox ButtonContainer = new HBox();
        HBox SearchContainer = new HBox();
        VBox topContainer = new VBox();
        VBox centerContainer = new VBox();

        Button searchButton = new Button();
        Button addButton = new Button("Add");

        // Handling top Container
        HBox.setMargin(SearchContainer, new Insets(0, 5, 0, 0));
        SearchContainer.setAlignment(Pos.TOP_CENTER);
        SearchContainer.setSpacing(10);
        SearchContainer.getChildren().add(0, citySearchField);
        SearchContainer.getChildren().add(1, searchButton);
        topContainer.getChildren().add(0, new CloseBar(this)); // Get the CloseBar first on top
        topContainer.getChildren().add(1, SearchContainer); // Get the Search Container second that will be below CloseBar

        searchButton.setOnAction(
                event -> performSearch()
        );

        addButton.setOnAction(
                event -> {
                    String[] data = getData(Table); // Receive the data from the user
                    if (data != null) { // Check if it's not null
                        try {
                            Bars Bars = new Bars();
                            Bars.addTab(data[0], data[3], data[2]);
                            isSearchStageOpen = false; // Switch to off that the user can open again the window
                            this.close();  // Closing the window
                        } catch (Exception e) {
                            new Alerts("Weather App", "Error code 71: Invalid data",
                                    Alert.AlertType.ERROR, null);
                            e.printStackTrace();
                        }
                    } else {
                        isSearchStageOpen = true; // Keeping the user on the window
                    }
                }
        );


        // Handling Center
        centerContainer.setAlignment(Pos.TOP_CENTER);
        centerContainer.setSpacing(10);
        centerContainer.getChildren().add(searchResultsContainer());

        // Handling Bottom
        HBox.setMargin(ButtonContainer, new Insets(10));
        ButtonContainer.setAlignment(Pos.CENTER);
        ButtonContainer.getChildren().add(addButton);

        searchButton.setId("magnifying-glass");
        citySearchField.setId("text-field");
        addButton.setId("addButton");
        root.getStyleClass().add("root");

        root.setTop(topContainer);  // Set topContainer as the top of the root
        root.setCenter(centerContainer);  // Set DataContainer as the center of the root
        root.setBottom(ButtonContainer);

        Scene scene = new Scene(root);

        scene.getStylesheets().add(new File("data\\css\\search.css").toURI().toString()); // Implement CSS

        this.initStyle(StageStyle.UNDECORATED);
        this.setResizable(false);  // Prevent from window to   resize the window
        this.setTitle("Weather App");
        this.setWidth(350);
        this.setHeight(300);
        this.setScene(scene);
        this.show();

        CloseBar.closeButton.setOnAction(
                event -> {
                    isSearchStageOpen = false;
                    this.close();
                }
        );
    }

    private boolean Check(String[][] arr) {  // Check if is there any null value in the arrays
        for (String[] value : arr) {  // For each value in the array
            if (value == null) {  // If it's find null, well, that's sucks bro
                System.out.println(Arrays.toString(arr));
                return false;  // The array is null
            }
        }
        return true;  // Yay, that's work :D
    }

    private HBox searchResultsContainer() {
        HBox searchContainer = new HBox();

        ObservableList<Results> Data = FXCollections.observableArrayList();

        TableColumn<Results, String> cityColumn = new TableColumn<>("City");
        TableColumn<Results, String> countryColumn = new TableColumn<>("Country");
        TableColumn<Results, String> provinceColumn = new TableColumn<>("Province");

        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        provinceColumn.setCellValueFactory(new PropertyValueFactory<>("province"));

        cityColumn.prefWidthProperty().set(100);
        countryColumn.prefWidthProperty().set(110);
        provinceColumn.prefWidthProperty().set(120);

        Data.add(new Results("", "", "", ""));
        Table.setItems(Data);

        Table.getColumns().addAll(  // todo: fix that
                cityColumn, countryColumn, provinceColumn
        );

        Table.getStyleClass().add("table-view");  // Implement CSS

        searchContainer.setPadding(new Insets(5));
        searchContainer.setAlignment(Pos.TOP_CENTER);
        searchContainer.getChildren().add(Table);

        return searchContainer;
    }

    public String[][] searchResult(String SearchString) {

        if (Objects.equals(SearchString, "Tel Aviv") || Objects.equals(SearchString, "Yafo")) {
            SearchString = "Tel Aviv-Yafo";
        }

        int[] elements = new int[6];
        elements[0] = 0; // Specify the number of array that will contain the array of cities + Specify the index of the "B" section column, contains cities
        elements[1] = 1; // Specify the number of array that will contain the array of countries
        elements[2] = 2; // Specify the number of array that will contain the array of provinces
        elements[3] = 4; // Specify the index of the "E" section column, contains countries
        elements[4] = 5;  // Specify the index of the "F" section column, contains country code
        elements[5] = 7; // Specify the index of the "H" section column, will contain Province or states in countries like USA

        int ElementValue = -1;

        String[][] data = new String[4][50];  // Mostly contain null but will get the search results

        try (Reader reader = new FileReader("data\\worldcities.csv");  // Performing csv read
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

            for (CSVRecord csvRecord : csvParser) {    // For each loop
                String city = csvRecord.get(elements[0]);  // City string value
                String country = csvRecord.get(elements[3]);  // Country string value
                String province = csvRecord.get(elements[5]);  // Province string value
                String countrycode = csvRecord.get(elements[4]); // Code String value

                if (city.equalsIgnoreCase(SearchString)) {   // Comparing searchString to city
                    ElementValue++;
                    data[elements[0]][ElementValue] = city;   // data[0][ElementValue] = city;
                    data[elements[1]][ElementValue] = country; // data[1][ElementValue] = country;
                    data[elements[2]][ElementValue] = province; // data[2][ElementValue] = province;
                    data[3][ElementValue] = countrycode; // data[3][ElementValue] = countrycode;
                }
            }

            if (ElementValue > -1 && ElementValue != 0) {
                ElementValue++;
                String[][] results = new String[4][ElementValue];  // New array to assign the results from the search

                for (int i = 0; i < ElementValue; i++) {  // Assign the results value
                    results[elements[0]][i] = data[elements[0]][i]; // city
                    results[elements[1]][i] = data[elements[1]][i]; // country
                    results[elements[2]][i] = data[elements[2]][i]; // province
                    results[3][i] = data[3][i]; // country code
                }

                return results;

            } else if (ElementValue == 0) {
                String[][] results = new String[4][1];
                results[elements[0]][0] = data[elements[0]][0]; // city
                results[elements[1]][0] = data[elements[1]][0]; // country
                results[elements[2]][0] = data[elements[2]][0]; // province
                results[3][0] = data[3][0]; // country code

                return results;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data; // Will return null

    }

    private void performSearch() {
        try {
            String searchString = citySearchField.getText();
            String[][] Results = searchResult(searchString);
            ObservableList<Results> Data = FXCollections.observableArrayList();

            for (int i = 0;  i < Results[0].length;  i++) {
                if (Check(Results)) {
                    Data.add(new Results(
                            Results[0][i], // City
                            Results[1][i], // Country
                            Results[2][i],  // Province
                            Results[3][i] // Country Code
                    ));
                } else if (Data.isEmpty()) {
                    Label notFound = new Label(" ");
                    Data.add(new Results("", "", "", ""));
                    Table.setPlaceholder(notFound);
                } else {
                    new Alerts("Weather App", "Error code 390: Unable to perform search",
                            Alert.AlertType.ERROR, null);
                    return; // Stop the loop
                }
            }

            Table.setItems(Data);

        } catch (ArrayIndexOutOfBoundsException e) {
            new Alerts("Weather App", "Error code 397: Unable to perform search",
                    Alert.AlertType.ERROR, null);
        }
    }

    public static String[] getData(TableView<Results> Table) {
        try {
            ObservableList<Results> selectedItems = Table.getSelectionModel().getSelectedItems();

            if (selectedItems != null && !selectedItems.isEmpty()) {
                String[] selectedData = new String[4];

                for (Results results : selectedItems) {
                    selectedData[0] = results.getCity();
                    selectedData[1] = results.getCountry();
                    selectedData[2] = results.getProvince();
                    selectedData[3] = results.getCode();
                } // [city, country, province, code]
                return selectedData;
            } else {
                new Alerts("Weather App", "Error code 417: Invalid data",
                        Alert.AlertType.ERROR, null);
                return null;
            }
        } catch (NullPointerException e) {
            new Alerts("Weather App", "Error code 417: Invalid data",
                    Alert.AlertType.ERROR, null);
            return null;
        }
    }

    public String getSearchField() {
        return citySearchField.getText();
    }
}