package Weather.Data;

import Weather.main.Alerts;
import Weather.main.app;
import Weather.main.city;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.List;

public class Load {

    public static List<String> Cities = new ArrayList<>();
    public static String currentLayout;
    private static int index = 1;

    public static void addTab(String city, String code) {
        if (city != null && code != null) {
            String name = city + ", " + code;

            if (Cities.size() < 9) {

                for (int i = Cities.size() - 1; i >= 0; i--) {
                    if (name.trim().equals(Cities.get(i).trim())) {
                        new Alerts(app.title, "City already exists", Alert.AlertType.WARNING, null);
                        return;
                    }
                }

                Cities.add(name);  // Add a new city to the list
                Button newButton = new Button(name);  // Assign a new variable

                newButton.getStyleClass().add("sidebar-button"); // Implement CSS

                newButton.setOnAction(  // Layout switch
                        event -> {
                            if (currentLayout == null || !currentLayout.equals(name)) {
                                switchLayout(city);  // Switch layout on click
                                currentLayout = name;  // Set the layout equal to current city
                                app.HomeisOpen = false;  // Well, you are not at home, right?
                                app.titleBar.getChildren().get(2).setVisible(true);  // Making remove button visible
                            }
                        }
                );

                app.sidebarCities.getChildren().add(index, newButton);
                index++;

            } else if (Cities.size() == 9) {  // Max: 8
                new Alerts(app.title, "You reached the maximum amount of cities",
                        Alert.AlertType.INFORMATION, null);
            }
        }
    }

    private static void switchLayout(String newCity) {
        city city = new city(newCity);
        BorderPane root = app.getMainRoot();
        root.setCenter(city);
    }

    public static String getCity(String city) {
        String[][] City = Weather.Data.data.searchResult(city);
        String addToCities = City[0][0] + ", " + City[3][0];
        Cities.add(addToCities);

        return addToCities;
    }


}
