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
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class City extends BorderPane {

    public City(String city) {

        Clock.createClock();  // Creating the clock for the user based on location's time
        Main.clock = new Label();  // Clock label
        Main.Status = new Label();  // Will show a greeting message based on the time

        Group[] ForecastTable = new Group[5]; // Will group all the data to the square, contains 5 because 5 different data
        Rectangle[] forecastData = new Rectangle[5]; // The data will present in this square
        Label[] ForecastLabel = new Label[5]; // Array of temperature forecast based on the location
        Label[] Days = new Label[5]; // Array of days, it will contain only 5 days
        ImageView[] forecastStatus = new ImageView[5];  // Array of images that will display the condition of the weather

        double[] temperatureForecast = Data.getForecast(city); // Getting the forecast for the next 5 days

        HBox clockContainer = new HBox();  // Will contain the clock in this container, will be declared at the top of the GUI
        HBox forecastContainer = new HBox();  // Will contain the data of the forecast
        VBox dataContainer = new VBox();  // Current weather data values and forecast values

        Label cityLabel = new Label(city);  // City label based on the user's choices
        Label Temperature = new Label((  // Getting the temperature of the city that was added by the user
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
        try {
            if (temperatureForecast.length == forecastData.length) { // If the forecast data is equal to the array length, should be 5

                LocalDate today = LocalDate.now(); // Getting today's
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE"); // Changing this to day string
                double labelX = 10; // Graph positioning
                double labelY = 10;
                String[] conditions = Data.getWeatherForecast(city);

                for (int i = forecastData.length - 1; i > -1; i--) { // Reverse loop to add all the data in certain order
                    int temperature = (int) temperatureForecast[i];  // Converting the temperature to an integer in purpose to display an integer
                    LocalDate date = today.plusDays(i + 1); // Increasing the day variable, I want to display the forecast, am I? =]
                    Image status = Conditions.getStatusImage(conditions[i]);

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

                    forecastData[i] = Main.createForecastSquare(); // Creating the square =], Calling the method from Main
                    forecastData[i].setOpacity(0.4); // Weaken the color that will have a clear background effect
                    forecastData[i].setFill(Color.BLACK); // Set to black

                    ForecastTable[i] = new Group(forecastData[i], ForecastLabel[i],
                            Days[i], forecastStatus[i]); // Assemble all the elements into one
                    forecastContainer.getChildren().add(0, ForecastTable[i]);  // Add the group to the layout
                }
            }
        } catch (FileNotFoundException e) {
            Error.raiseError(348); // Let's try to prevent this error :D
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

                                switchLayout(city);  // Switch layout on click
                                currentLayout = name;  // Set the layout equal to current city
                                Main.HomeisOpen = false;  // Well, you are not at home, right?
                                Main.titleBar.getChildren().get(2).setVisible(true);  // Making remove button visible
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