package Weather.main;

import Weather.Data.data;
import Weather.widgets.clock;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class city extends BorderPane {

    public city(String city) {

        clock.createClock();  // Creating the clock for the user based on location's time
        app.clock = new Label();  // Clock label
        app.Status = new Label();  // Will show a greeting message based on the time

        Group[] ForecastTable = new Group[5]; // Will group all the data to the square, contains 5 because 5 different data
        Rectangle[] forecastData = new Rectangle[5]; // The data will present in this square
        Label[] ForecastLabel = new Label[5]; // Array of temperature forecast based on the location
        Label[] Days = new Label[5]; // Array of days, it will contain only 5 days
        ImageView[] forecastStatus = new ImageView[5];  // Array of images that will display the condition of the weather

        double[] temperatureForecast = data.getForecast(city); // Getting the forecast for the next 5 days

        HBox clockContainer = new HBox();  // Will contain the clock in this container, will be declared at the top of the GUI
        HBox forecastContainer = new HBox();  // Will contain the data of the forecast
        VBox dataContainer = new VBox();  // Current weather data values and forecast values

        Label cityLabel = new Label(city);  // City label based on the user's choices
        Label Temperature = new Label((  // Getting the temperature of the city that was added by the user
                data.getTemperature(city)
        ) + " °C");

        // Implement CSS
        app.clock.setId("Clock");
        Temperature.setId("basedTemperature");
        cityLabel.setId("city");

        // Top Container
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        clockContainer.setPadding(new Insets(10));
        clockContainer.getChildren().addAll(app.Status, region, app.clock);

        // Central Container
        dataContainer.setPadding(new Insets(0, 10, 0, 0));
        dataContainer.setSpacing(5);
        dataContainer.getChildren().add(0, cityLabel);
        dataContainer.getChildren().add(1, Temperature);
        dataContainer.setAlignment(Pos.TOP_CENTER);

        // Forecast Container
        try {
            if (temperatureForecast.length == forecastData.length) { // If the forecast data is equal to the array length, should be 5

                LocalDate today = LocalDate.now(); // Getting today's
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE"); // Changing this to day string
                double labelX = 10; // Graph positioning
                double labelY = 10;
                String[] conditions = data.getWeatherForecast(city);

                for (int i = forecastData.length - 1; i > -1; i--) { // Reverse loop to add all the data in certain order
                    int temperature = (int) temperatureForecast[i];  // Converting the temperature to an integer in purpose to display an integer
                    LocalDate date = today.plusDays(i + 1);
                    Image status = Weather.widgets.conditions.getStatusImage(conditions[i]);

                    forecastStatus[i] = new ImageView();  // Image
                    forecastStatus[i].setImage(status);  // Get the image by status of the weather
                    forecastStatus[i].setX(labelX);
                    forecastStatus[i].setY(labelY + 70);

                    Days[i] = new Label(date.format(formatter)); // Will present the string of the day (Sunday, Monday and etc)
                    Days[i].setId("Day");
                    Days[i].setLayoutX(labelX);
                    Days[i].setLayoutY(labelY);

                    ForecastLabel[i] = new Label(temperature + " °C"); // Will display the temperature number
                    ForecastLabel[i].setId("Forecast");
                    ForecastLabel[i].setLayoutX(labelX);
                    ForecastLabel[i].setLayoutY(labelY + 40);

                    forecastData[i] = app.createForecastSquare();
                    forecastData[i].setOpacity(0.4); // Weaken the color that will have a clear background effect
                    forecastData[i].setFill(Color.BLACK); // Set to black

                    ForecastTable[i] = new Group(forecastData[i], ForecastLabel[i],
                            Days[i], forecastStatus[i]); // Assemble all the elements into one
                    forecastContainer.getChildren().add(0, ForecastTable[i]);  // Add the group to the layout
                }
            }
        } catch (FileNotFoundException e) {
            app.alert(app.title, "Error code 106: File not found",
                    app.error, null);
        }

        forecastContainer.setSpacing(10);
        forecastContainer.setPadding(new Insets(5));
        forecastContainer.setAlignment(Pos.BOTTOM_CENTER);
        clock.setStatus(this); // Apply the appropriate background based on the hour

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

    public static void addTab(String city, String province, String code) {
        if (city != null && province != null && code != null) {
            String name = city + ", " + code;

            if (buttonCount < 8) {

                for (int i = Cities.size() - 1; i >= 0; i--) {
                    System.out.println(Cities);
                    if (name.trim().equals(Cities.get(i).trim())) {
                        app.alert(app.title, "City already exists", app.warning, null);
                        return;
                    }
                }

                Cities.add(name);  // Add a new city to the list

                buttonCount++;  // Increase the button count
                Tabs[buttonCount] = new Button(name);  // create a new button in the array
                Button newButton = Tabs[buttonCount];  // Assign a new variable

                //newButton.setId("newButton");
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

                app.sidebarCities.getChildren().add(newButton);

            } else if (buttonCount == 8) {  // Max: 8
                app.alert(app.title, "You reached the maximum amount of cities",
                        app.information, null);
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