package weather.main;

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
import weather.data.Data;
import weather.widgets.Clock;
import weather.widgets.Conditions;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class City extends Data {

    public static boolean isHomeOpen = true;

    public BorderPane createCity(String city, String code, String province) {
        String name = checkCity(city);

        try {
            Clock clockHandler = new Clock();
            Conditions Conditions = new Conditions();

            Group[] ForecastTable = new Group[5];  // Will group all the data to the square, contains 5 because 5 different data
            Rectangle[] forecastData = new Rectangle[5];  // The data will present in this square
            Label[] ForecastLabel = new Label[5];  // Array of temperature forecast based on the location
            Label[] Days = new Label[5];  // Array of days, it will contain only 5 days
            ImageView[] forecastStatus = new ImageView[5]; // Array of images that will display the condition of the weather

            double[] temperatureForecast = getForecast(name, code, province);  // Getting the forecast for the next 5 days

            BorderPane root = new BorderPane();

            HBox clockContainer = new HBox();
            HBox forecastContainer = new HBox();
            HBox todayContainer = new HBox();

            VBox locationContainer = new VBox();
            VBox DetailsContainer = new VBox();
            VBox dataContainer = new VBox();

            Label City = new Label(city); // Contains the user's city location based on Public IP Address
            Label HomeTemperature = new Label(getTemperature(name, code, province) + " °C");

            // Details
            Label DetailCity = new Label("City: " + city);
            Label Country = new Label("Country: " + code);
            Label Province = new Label("Province: " + province);

            Image Condition = Conditions.getStatusImage(getWeatherStatus(name, code, province));
            ImageView todayCondition = new ImageView();
            todayCondition.setImage(Condition);

            // Implement CSS
            HomeTemperature.setId("basedTemperature");
            City.setId("city");

            Country.setId("details");
            Province.setId("details");
            DetailCity.setId("details");

            // Top Container
            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);
            clockContainer.setPadding(new Insets(10));
            clockContainer.getChildren().addAll(clockHandler.getStatus(), region, clockHandler.getClockLabel());

            // Central data
            todayContainer.setAlignment(Pos.CENTER);
            todayContainer.setPadding(new Insets(0 , 10, 0, 10));
            todayContainer.setSpacing(10);
            todayContainer.getChildren().addAll(todayCondition, HomeTemperature);

            locationContainer.setAlignment(Pos.TOP_LEFT);
            locationContainer.setPadding(new Insets(0 , 5, 0, 5));
            locationContainer.getChildren().add(0, DetailCity);
            locationContainer.getChildren().add(1, Country);
            locationContainer.getChildren().add(2, Province);

            dataContainer.setPadding(new Insets(0, 10, 0, 0));
            dataContainer.setSpacing(5);
            dataContainer.getChildren().add(0, City);
            dataContainer.getChildren().add(1, todayContainer);
            dataContainer.setAlignment(Pos.TOP_CENTER);

            if (temperatureForecast.length == forecastData.length) {  // If the forecast data is equal to the array length

                LocalDate today = LocalDate.now();  // Getting today's
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE");  // Changing this to day string
                double labelX = 10;  // Graph positioning
                double labelY = 10;
                String[] conditions = getWeatherForecast(name, code, province);

                for (int i = forecastData.length - 1; i > -1; i--) {  // Reverse loop to add all the data in certain order
                    int temperature = (int) temperatureForecast[i];
                    LocalDate date = today.plusDays(i + 1);  // Increasing by one to display tomorrow data
                    Image status = Conditions.getStatusImage(conditions[i]);

                    forecastStatus[i] = new ImageView();  // Image
                    forecastStatus[i].setImage(status);  // Get the image by status of the weather
                    forecastStatus[i].setX(labelX);
                    forecastStatus[i].setY(labelY + 70);

                    Days[i] = new Label(date.format(formatter));  // Will present the string of the day
                    Days[i].setId("Day");
                    Days[i].setLayoutX(labelX);
                    Days[i].setLayoutY(labelY);

                    ForecastLabel[i] = new Label(temperature + " °C");  // Will display the temperature number
                    ForecastLabel[i].setId("Forecast");
                    ForecastLabel[i].setLayoutX(labelX);
                    ForecastLabel[i].setLayoutY(labelY + 40);

                    forecastData[i] = createForecastSquare();
                    forecastData[i].setOpacity(0.4);  // Weaken the color that will have a clear background effect
                    forecastData[i].setFill(Color.BLACK);  // Set to black

                    ForecastTable[i] = new Group(forecastData[i], ForecastLabel[i],  // Assemble all the elements into one
                            Days[i], forecastStatus[i]);
                    forecastContainer.getChildren().add(0, ForecastTable[i]);  // Add the group to the layout
                }

                forecastContainer.setSpacing(10);
                forecastContainer.setPadding(new Insets(5));
                forecastContainer.setAlignment(Pos.BOTTOM_CENTER);

                DetailsContainer.getChildren().add(0, locationContainer);
                DetailsContainer.getChildren().add(1, forecastContainer);
                DetailsContainer.setSpacing(5);
                DetailsContainer.setPadding(new Insets(5));

                root.setTop(clockContainer);
                root.setCenter(dataContainer);
                root.setBottom(DetailsContainer);
                clockHandler.setBackground(root);

                return root;
            }

        } catch (FileNotFoundException e) {
            new Alerts("Weather App", "Error code 352: file not found", Alert.AlertType.ERROR, null);
        }
        return null;
    }

    public BorderPane createHome() throws FileNotFoundException {

        String name = checkCity(getCity());
        String province = getProvince(getCity());
        String HomeCity = getCity();
        String code = getCode();

        try {
             Clock clockHandler = new Clock();
            Conditions Conditions = new Conditions();

            Group[] ForecastTable = new Group[5];  // Will group all the data to the square, contains 5 because 5 different data
            Rectangle[] forecastData = new Rectangle[5];  // The data will present in this square
            Label[] ForecastLabel = new Label[5];  // Array of temperature forecast based on the location
            Label[] Days = new Label[5];  // Array of days, it will contain only 5 days
            ImageView[] forecastStatus = new ImageView[5]; // Array of images that will display the condition of the weather

            double[] temperatureForecast = getForecast(name, code, province);  // Getting the forecast for the next 5 days

            BorderPane root = new BorderPane();

            HBox clockContainer = new HBox();
            HBox forecastContainer = new HBox();
            HBox todayContainer = new HBox();

            VBox locationContainer = new VBox();
            VBox DetailsContainer = new VBox();
            VBox dataContainer = new VBox();

            Label City = new Label(HomeCity); // Contains the user's city location based on Public IP Address
            Label HomeTemperature = new Label(getTemperature(name, code, province) + " °C");


            // Details
            Label DetailCity = new Label("City: " + HomeCity);
            Label Country = new Label("Country: " + code);
            Label Province = new Label("Province: " + province);

            Image Condition = Conditions.getStatusImage(getWeatherStatus(name, code, province));
            ImageView todayCondition = new ImageView();
            todayCondition.setImage(Condition);

            // Implement CSS
            HomeTemperature.setId("basedTemperature");
            City.setId("city");

            Country.setId("details");
            Province.setId("details");
            DetailCity.setId("details");

            // Top Container
            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);
            clockContainer.setPadding(new Insets(10));
            clockContainer.getChildren().addAll(clockHandler.getStatus(), region, clockHandler.getClockLabel());

            // Central data
            todayContainer.setAlignment(Pos.CENTER);
            todayContainer.setPadding(new Insets(0 , 10, 0, 10));
            todayContainer.setSpacing(10);
            todayContainer.getChildren().addAll(todayCondition, HomeTemperature);

            locationContainer.setAlignment(Pos.TOP_LEFT);
            locationContainer.setPadding(new Insets(0 , 5, 0, 5));
            locationContainer.getChildren().add(0, DetailCity);
            locationContainer.getChildren().add(1, Country);
            locationContainer.getChildren().add(2, Province);

            dataContainer.setPadding(new Insets(0, 10, 0, 0));
            dataContainer.setSpacing(5);
            dataContainer.getChildren().add(0, City);
            dataContainer.getChildren().add(1, todayContainer);
            dataContainer.setAlignment(Pos.TOP_CENTER);

            if (temperatureForecast.length == forecastData.length) {  // If the forecast data is equal to the array length

                LocalDate today = LocalDate.now();  // Getting today's
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE");  // Changing this to day string
                double labelX = 10;  // Graph positioning
                double labelY = 10;
                String[] conditions = getWeatherForecast(HomeCity, code, province);

                for (int i = forecastData.length - 1; i > -1; i--) {  // Reverse loop to add all the data in certain order
                    int temperature = (int) temperatureForecast[i];
                    LocalDate date = today.plusDays(i + 1);  // Increasing by one to display tomorrow data
                    Image status = Conditions.getStatusImage(conditions[i]);

                    forecastStatus[i] = new ImageView();  // Image
                    forecastStatus[i].setImage(status);  // Get the image by status of the weather
                    forecastStatus[i].setX(labelX);
                    forecastStatus[i].setY(labelY + 70);

                    Days[i] = new Label(date.format(formatter));  // Will present the string of the day
                    Days[i].setId("Day");
                    Days[i].setLayoutX(labelX);
                    Days[i].setLayoutY(labelY);

                    ForecastLabel[i] = new Label(temperature + " °C");  // Will display the temperature number
                    ForecastLabel[i].setId("Forecast");
                    ForecastLabel[i].setLayoutX(labelX);
                    ForecastLabel[i].setLayoutY(labelY + 40);

                    forecastData[i] = createForecastSquare();
                    forecastData[i].setOpacity(0.4);  // Weaken the color that will have a clear background effect
                    forecastData[i].setFill(Color.BLACK);  // Set to black

                    ForecastTable[i] = new Group(forecastData[i], ForecastLabel[i],  // Assemble all the elements into one
                            Days[i], forecastStatus[i]);
                    forecastContainer.getChildren().add(0, ForecastTable[i]);  // Add the group to the layout
                }

                forecastContainer.setSpacing(10);
                forecastContainer.setPadding(new Insets(5));
                forecastContainer.setAlignment(Pos.BOTTOM_CENTER);

                DetailsContainer.getChildren().add(0, locationContainer);
                DetailsContainer.getChildren().add(1, forecastContainer);
                DetailsContainer.setSpacing(5);
                DetailsContainer.setPadding(new Insets(5));

                root.setTop(clockContainer);
                root.setCenter(dataContainer);
                root.setBottom(DetailsContainer);
                clockHandler.setBackground(root);

                return root;
            }

        } catch (FileNotFoundException e) {
            new Alerts("Weather App", "Error code 352: file not found", Alert.AlertType.ERROR, null);
        }

        return null;
    }

    private Rectangle createForecastSquare() {  // Creating square
        return new Rectangle(113, 113);
    }

}
