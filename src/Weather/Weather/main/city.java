package Weather.main;

import Weather.Data.data;
import Weather.widgets.Clock;
import Weather.widgets.Conditions;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class city extends BorderPane {

    public city(String city) {

        String name = city;

        // Forecast Container
        try {

            if (city.equals("New York")) {
                city = city.replace(" ", "%20");

            }

            Clock Clock = new Clock();
            Conditions Conditions = new Conditions();

            Clock.createClock();  // Creating the clock for the user based on location's time
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
            HBox todayContainer = new HBox();
            VBox dataContainer = new VBox();  // Current weather data values and forecast values

            Label cityLabel = new Label(name);  // City label based on the user's choices
            Label Temperature = new Label((  // Getting the temperature of the city that was added by the user
                    data.getTemperature(city)
            ) + " °C");

            Image Condition = Conditions.getStatusImage(data.getWeatherStatus(city));
            ImageView todayCondition = new ImageView();
            todayCondition.setImage(Condition);

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
            todayContainer.setAlignment(Pos.CENTER);
            todayContainer.setPadding(new Insets(0 , 10, 0, 10));
            todayContainer.setSpacing(10);
            todayContainer.getChildren().addAll(todayCondition, Temperature);

            dataContainer.setPadding(new Insets(0, 10, 0, 0));
            dataContainer.setSpacing(5);
            dataContainer.getChildren().add(0, cityLabel);
            dataContainer.getChildren().add(1, todayContainer);
            dataContainer.setAlignment(Pos.TOP_CENTER);

            if (temperatureForecast.length == forecastData.length) { // If the forecast data is equal to the array length, should be 5

                LocalDate today = LocalDate.now(); // Getting today's
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE"); // Changing this to day string
                double labelX = 10; // Graph positioning
                double labelY = 10;
                String[] conditions = data.getWeatherForecast(city);

                for (int i = forecastData.length - 1; i > -1; i--) { // Reverse loop to add all the data in certain order
                    int temperature = (int) temperatureForecast[i];  // Converting the temperature to an integer in purpose to display an integer
                    LocalDate date = today.plusDays(i + 1);
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

                    forecastData[i] = app.createForecastSquare();
                    forecastData[i].setOpacity(0.4); // Weaken the color that will have a clear background effect
                    forecastData[i].setFill(Color.BLACK); // Set to black

                    ForecastTable[i] = new Group(forecastData[i], ForecastLabel[i],
                            Days[i], forecastStatus[i]); // Assemble all the elements into one
                    forecastContainer.getChildren().add(0, ForecastTable[i]);  // Add the group to the layout
                }

                forecastContainer.setSpacing(10);
                forecastContainer.setPadding(new Insets(5));
                forecastContainer.setAlignment(Pos.BOTTOM_CENTER);
                Clock.setStatus(this); // Apply the appropriate background based on the hour

                this.setTop(clockContainer);
                this.setCenter(dataContainer);
                this.setBottom(forecastContainer);

            }
        } catch (FileNotFoundException e) {
            new Alerts(app.title, "Error code 106: File not found",
                    Alert.AlertType.ERROR, null);
        }
    }
}

