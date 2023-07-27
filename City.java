import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class City extends BorderPane {

    public City(String city) {
        Data.createClock();
        Main.clock = new Label();
        Main.Status = new Label();

        Group[] ForecastTable = new Group[5];
        Rectangle[] forecastData = new Rectangle[5];
        Label[] ForecastLabel = new Label[5];
        Label[] Days = new Label[5];

        double[] temperatureForecast = Data.getForecast(city);

        HBox clockContainer = new HBox();
        HBox forecastContainer = new HBox();
        VBox dataContainer = new VBox();

        Label cityLabel = new Label(city);
        Label Temperature = new Label((
                Data.getTemperature(city)
        ) + " °C");

        // Implement CSS
        Main.clock.setId("Clock");
        Temperature.setId("basedTemperature");
        cityLabel.setId("city");

        // Top Container
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        clockContainer.setPadding(new Insets(10));
        clockContainer.getChildren().addAll(Main.Status, region, Main.clock);

        // Central Container
        dataContainer.setPadding(new Insets(0, 10, 0, 0));
        dataContainer.setSpacing(5);
        dataContainer.getChildren().add(0, cityLabel);
        dataContainer.getChildren().add(1, Temperature);
        dataContainer.setAlignment(Pos.TOP_CENTER);

        // Forecast Container
        if (temperatureForecast.length == forecastData.length) {

            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE");
            double labelX = 10;
            double labelY = 10;

            for (int i = forecastData.length - 1; i > -1; i--) {
                int temperature = (int) temperatureForecast[i];
                LocalDate date = today.plusDays(i + 1);

                Days[i] = new Label(date.format(formatter));
                Days[i].setId("Day");
                Days[i].setLayoutX(labelX);
                Days[i].setLayoutY(labelY);

                ForecastLabel[i] = new Label(temperature + " °C");
                ForecastLabel[i].setId("Forecast");
                ForecastLabel[i].setLayoutX(labelX);
                ForecastLabel[i].setLayoutY(labelY + 40);

                forecastData[i] = Main.createForecastSquare();
                forecastData[i].setOpacity(0.4);
                forecastData[i].setFill(Color.BLACK);

                ForecastTable[i] = new Group(forecastData[i], ForecastLabel[i], Days[i]);
                forecastContainer.getChildren().add(0, ForecastTable[i]);
            }
        }

        forecastContainer.setSpacing(10);
        forecastContainer.setPadding(new Insets(5));
        forecastContainer.setAlignment(Pos.BOTTOM_CENTER);
        Clock.setStatus(this); // Apply the appropriate background based on the hour

        this.setTop(clockContainer);
        this.setCenter(dataContainer);
        this.setBottom(forecastContainer);
    }
}

class addCity {
    public static List<String> Cities = new ArrayList<>();

    public static Button[] Tabs = new Button[8];
    private static int buttonCount = 0;
    public static String currentLayout;

    public static void addTab(String[] data) {
        if (data != null && data.length >= 3) {

            String city = data[0]; // City
            String province = data[2]; // Province
            String code = data[3]; // Country Code

            String name = city + ", " + code;
            String scan = name + " " + province;

            for (String search : Cities) {  // Checking if the user is on the current layout ->
                if (search.equals(scan)) { // if yes, it will prevent from the user to load the layout all over again.
                    System.out.println(Cities);
                    Error.raiseError(121);
                    return;
                }
            }

            if (buttonCount < 7) {

                Cities.add(name + " " + province);  // Add a new city to the list
                buttonCount++;  // Increase the button count
                Tabs[buttonCount] = new Button(name);  // create a new button in the array
                Button newButton = Tabs[buttonCount];  // Assign a new variable

                //newButton.setId("newButton");
                newButton.getStyleClass().add("sidebar-button"); // Implement CSS

                newButton.setOnAction(  // Layout switch
                        event -> {
                            if (currentLayout == null || !currentLayout.equals(name)) {

                                if (!Cities.isEmpty()) {  // Let the user add again the city to the list after removal of the city
                                    Iterator<String> iterator = Cities.iterator();
                                    while (iterator.hasNext()) {
                                        String search = iterator.next();
                                        if (search.equals(scan)) {
                                            iterator.remove();
                                            break;
                                        }
                                    }
                                }

                                switchLayout(city);
                                currentLayout = name;
                                Main.HomeisOpen = false;
                                Main.titleBar.getChildren().get(2).setVisible(true);
                            }
                        }
                );

                Main.sidebarCities.getChildren().add(newButton);

            } else if (buttonCount > 7) {  // Max: 8
                Error.raiseError(146);
            }
        }
    }

    private static void switchLayout(String newCity) {
        City city = new City(newCity);
        BorderPane root = Main.getMainRoot();
        root.setCenter(city);
    }

    public static String getCity(String city) {
        String[][] City = Data.searchResult(city);
        String addToCities = City[0][0] + ", " + City[3][0] + " " + City[2][0];
        addCity.Cities.add(addToCities);

        return addToCities;
    }


}