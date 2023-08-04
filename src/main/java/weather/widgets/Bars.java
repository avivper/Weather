package weather.widgets;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import weather.data.Contents;
import weather.data.Data;
import weather.data.Search;
import weather.main.Alerts;
import weather.main.App;
import weather.main.City;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Bars extends App {

    public static String currentLayout;
    public static VBox root = new VBox();

    public static List<String> Cities = new ArrayList<>();
    private static  int index = 1;

    public VBox createSideBar() {

        Button homeButton = new Button("Home");
        Button saveButton = new Button("Save");

        homeButton.getStyleClass().add("sidebar-button");
        saveButton.getStyleClass().add("sidebar-button");

        homeButton.setOnAction(
                event -> {
                    if (!City.isHomeOpen) {
                        TitleBar.getChildren().get(2).setVisible(false);
                        currentLayout = null;
                        City.isHomeOpen = true;
                        try {
                            switchHome();
                        } catch (FileNotFoundException e) {
                            new Alerts("Weather App", "Error code 50: An error has been occurred",
                                    Alert.AlertType.ERROR, null);
                        }
                    }
                }
        );

        saveButton.setOnAction(
                event -> save(Cities)
        );

        Region region = new Region();
        VBox.setVgrow(region, Priority.ALWAYS);
        root.setPrefWidth(150);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.TOP_CENTER);
        root.setSpacing(0);  // Set a space between buttons
        root.getStyleClass().add("sidebar");
        root.getChildren().addAll(homeButton, region, saveButton);

        return root;
    }

    public void addTab(String city, String code, String province) {
        if (city != null && code != null) {
            String name = city  + ", " + code;

            if (index < 9) {

                for (int i = Cities.size() - 1; i >=0; i--) {
                    if (name.trim().equals(Cities.get(i).trim())) {
                        new Alerts("Weather App", "City already exists"
                                , Alert.AlertType.WARNING, null);
                        return;
                    }
                }

                Cities.add(name);

                Button newButton = new Button(name);
                newButton.getStyleClass().add("sidebar-button");

                newButton.setOnAction(
                        event -> {
                            if (currentLayout == null || !currentLayout.equals(name)) {
                                switchLayout(city, code, province);
                                TitleBar.getChildren().get(2).setVisible(true);
                                currentLayout = name;
                                City.isHomeOpen = false;
                            }
                        }
                );

                root.getChildren().add(index, newButton);
                index++;

            } else if (index == 9) {
                new Alerts("Weather App", "You reached the maximum amount of cities",
                        Alert.AlertType.INFORMATION, null);
            }
        }
    }

    public static void switchHome() throws FileNotFoundException {
        City City = new City();
        App.MainRoot.setCenter(City.createHome());
    }

    private void switchLayout(String city, String code, String province) {
        City City = new City();
        App.MainRoot.setCenter(City.createCity(city, code, province));
    }

    private void save(List<String> Cities) {
        String file = "data\\save";

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file))) {

            for (int i = Cities.size() - 1;  i >= 0 ; i--) {
                writer.write(Cities.get(i));
                writer.newLine();
            }

            new Alerts("Weather App", "Saved successfully",
                    Alert.AlertType.INFORMATION, null);

        } catch (IOException e) {
            new Alerts("Weather App", "Error: " + e.getMessage(),
                    Alert.AlertType.ERROR, null);
        }
    }

    public void load() {
        String file = "data\\save";

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file)
        )) {

            Data data = new Data();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] save = line.split(", ");
                String name = save[0] + ", " + save[1];
                if (!name.equals(data.getCity())) {
                    addTab(save[0], save[1], data.getProvince(save[0]));
                } else {
                    new Alerts("Weather App", "Error code 154:  Error has been occurred",
                            Alert.AlertType.ERROR, null);
                }
            }
        } catch (IOException e) {
            new Alerts("Weather App", "Error: " + e.getMessage(),
                    Alert.AlertType.ERROR, null);
        }
    }

    public HBox titleBar(Stage stage)  {
        HBox titleBar = new HBox();

        Label title = new Label("Weather App");
        Button plusButton = new Button();  // Creating a button that will implement the add of cities by user choice
        Button removeButton = new Button();
        Button contentsButton = new Button();

        plusButton.setId("plus-button");
        title.setId("title");
        removeButton.setId("remove-button");
        contentsButton.setId("contents-button");

        plusButton.setOnAction(
                event -> {
                    if (!Search.isSearchStageOpen) {
                        try {
                            Search searchStage = new Search(stage);
                            searchStage.show();
                            Search.isSearchStageOpen = true;
                            searchStage.setOnHidden(
                                    e -> Search.isSearchStageOpen = false
                            );
                        } catch (FileNotFoundException e) {
                            new Alerts("Weather App", "Error code 191: Invalid data",
                                    Alert.AlertType.ERROR, null);
                        }
                    }
                }
        );

        contentsButton.setOnAction(
                event -> {
                    if (!Contents.isContentsOpen) {
                        try {
                            Contents contents = new Contents(stage);
                            contents.show();
                            Contents.isContentsOpen = false;
                            contents.setOnHidden(
                                    e-> Contents.isContentsOpen = false
                            );
                        } catch (FileNotFoundException e) {
                            new Alerts("Weather App", "Error code 206: Invalid data",
                                    Alert.AlertType.ERROR, null);
                        }
                    }
                }
        );

        removeButton.setOnAction(
                event -> {
                    for (Node node : sideBar.getChildren()) {
                        if (node instanceof  Button) {
                            if (((Button) node).getText().equals(currentLayout)) {
                                showConfirmation(
                                        result -> {
                                            boolean decision = result;
                                            if (decision) {
                                                sideBar.getChildren().remove(node);
                                                Cities.remove(currentLayout);
                                                index--;

                                                try {
                                                    switchHome();
                                                } catch (FileNotFoundException e) {
                                                    new Alerts("Weather App", "Error code 212: An error has been occurred",
                                                            Alert.AlertType.ERROR, null);
                                                }

                                                currentLayout = null;
                                                City.isHomeOpen = true;
                                                titleBar.getChildren().get(2).setVisible(false);
                                            } else {
                                                System.out.println(false);
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
        HBox.setMargin(removeButton, new Insets(0, 10, 0, 10));
        HBox.setMargin(contentsButton, new Insets(0, 10, 0, 10));
        titleBar.setPadding(new Insets(5));
        titleBar.setId("titleBar");
        titleBar.getChildren().addAll(title, contentsButton, region, removeButton, plusButton);

        titleBar.getChildren().get(2).setVisible(false);

        return titleBar;
    }

    private void showConfirmation(Consumer<Boolean> callback) { // Method that will ask user true or false (Yes or no message)
        new Alerts("Confirmation", "Are you sure you want to proceed?",
                Alert.AlertType.CONFIRMATION, callback);
    }

}
