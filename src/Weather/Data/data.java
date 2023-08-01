package Weather.Data;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import Weather.main.Alerts;
import Weather.main.app;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.record.City;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;


public class data {

    // https://api.openweathermap.org/data/2.5/forecast?q=Haifa&appid=70f28ecc4f3d6c65e0897b61513c262f

    private static String Public_IP = PublicIPFinder();
    private static final String DataPath = "data\\GeoLite2-City.mmdb";
    private static final String apiKey = "70f28ecc4f3d6c65e0897b61513c262f";
    public static TableView<search> Table;

    private static boolean Check(String[][] arr) {  // Check if is there any null value in the arrays
        for (String[] value : arr) {  // For each value in the array
            if (value == null) {  // If it's find null, well, that's sucks bro
                System.out.println(Arrays.toString(arr));
                return false;  // The array is null
            }
        }
        return true;  // Yay, that's work :D
    }

    // todo: make it more steady with various url services
    public static String PublicIPFinder() {
        try {
            URI url = new URI("http://checkip.amazonaws.com/");  // Contain user IP address
            HttpURLConnection http_connection = (HttpURLConnection) url.toURL().openConnection();
            http_connection.setRequestMethod("GET");  // Making a request
            http_connection.setConnectTimeout(5000);
            http_connection.connect();

            if (http_connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = http_connection.getInputStream();
                Scanner scanner = new Scanner(new InputStreamReader(inputStream))) {
                    Public_IP = scanner.next();
                    return Public_IP;
                }
            } else {
               new Alerts(app.title, "Error code 67: Unable to connect, try again later",
                        Alert.AlertType.ERROR, null);
                return "Error: Unable to fetch public IP. HTTP Response Code: " + http_connection.getResponseCode();
            }
        } catch (IOException  | URISyntaxException e ) {
            new Alerts(app.title, "Error code 71: Unable to connect, try again later",
                    Alert.AlertType.ERROR, null);
            return "Error " + e.getMessage();
        }
    }

    public static String getCity() {   // Gets location by the IP of the user
        if (!Objects.equals(Public_IP, "Error Connect timed out")) {
            File database = new File(DataPath);  // Read the database
            try (DatabaseReader reader = new DatabaseReader.Builder(database).build()) {  // Build
                CityResponse response = reader.city(InetAddress.getByName(Public_IP));
                City city = response.getCity();
                return city.getName();
            } catch (GeoIp2Exception | IOException e) {
                new Alerts(app.title, "Error code 86: Unable to fetch data, try again later",
                        Alert.AlertType.ERROR, null);
                return null;
            }
        } else {
            new Alerts(app.title, "Error code 91: An error has been occurred",
                    Alert.AlertType.ERROR, null);
            return null;
        }
    }

    public static int getTemperature(String city) { // Except a city to output a weather data according to the city name
        String weatherData = getWeatherData(city);
        return (int) parseTemperatureCelsius(weatherData);
    }

    private static String getWeatherData(String city) {
        try {
            String apiURL = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + data.apiKey;
            URI url = new URI(apiURL);
            HttpURLConnection connection = (HttpURLConnection) url.toURL().openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine())!= null) {
                response.append(line);
            }

            reader.close();
            return response.toString();
        } catch (IOException | URISyntaxException e) {
            new Alerts(app.title, "Error code 120: Unable to fetch data",
                    Alert.AlertType.ERROR, null);
            return e.toString();
        }
    }

    private static double parseTemperatureCelsius(String weatherData) {
        JSONObject jsonObject = new JSONObject(weatherData);
        JSONObject mainObject = jsonObject.getJSONObject("main");
        double temperatureKelvin = mainObject.getDouble("temp");
        return temperatureKelvin - 273.15;
    }

    private static double[] parseTemperatureForecast(String forecastData) {
        double[] temperatureForecast = new double[5];
        JSONObject jsonObject = new JSONObject(forecastData);
        JSONArray listArray = jsonObject.getJSONArray("list");

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        int forecastDayCount = 0;
        for (int i = 0; i < listArray.length(); i++) {
            JSONObject item = listArray.getJSONObject(i);
            String dtTxt = item.getString("dt_txt");
            LocalDateTime dateTime = LocalDateTime.parse(dtTxt, formatter);
            LocalDate date = dateTime.toLocalDate();

            if (date.isAfter(today) && forecastDayCount < 5) {
                JSONObject mainObject = item.getJSONObject("main");
                double temperatureKelvin = mainObject.getDouble("temp");
                temperatureForecast[forecastDayCount] = temperatureKelvin - 273.15;
                forecastDayCount++;
            }
        }
        return temperatureForecast;
    }

    public static double[] getForecast(String city) {
        String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + apiKey;
        double[] temperatureForecast = new double[5];

        try {
            URI url = new URI(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.toURL().openConnection();
            connection.setRequestMethod("GET");  // Making a request
            connection.setConnectTimeout(5000);
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = connection.getInputStream();
                Scanner scanner = new Scanner(new InputStreamReader(inputStream))) {
                    String forecastData = scanner.useDelimiter("\\A").next();
                    // Parse the forecast data to get the temperature for each day
                    temperatureForecast = parseTemperatureForecast(forecastData);
                    return temperatureForecast;
                }
            } else {
                new Alerts(app.title, "Error code 176: An error has been occurred",
                        Alert.AlertType.ERROR, null);
            }
        } catch (IOException | URISyntaxException e) {
            new Alerts(app.title, "Error code 91: An error has been occurred",
                    Alert.AlertType.ERROR, null);
        }
        return temperatureForecast;
    }

    private static String parseWeatherStatus(String weatherData) {
        JSONObject jsonObject = new JSONObject(weatherData);
        JSONArray weatherArray = jsonObject.getJSONArray("weather");

        if (!weatherArray.isEmpty()) {
            JSONObject weatherObject = weatherArray.getJSONObject(0);
            return weatherObject.getString("main");

        } else {
            new Alerts(app.title, "Error code 195: Unable to fetch data",
                   Alert.AlertType.ERROR, null);
            return "Unknown";
        }
    }

    private static String getStatusOfToday(String city) {
        try {
            String apiURL = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
            URI url = new URI(apiURL);
            HttpURLConnection connection = (HttpURLConnection) url.toURL().openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            return response.toString();
        } catch (IOException | URISyntaxException e) {
            new Alerts(app.title, "Error code 219: Unable to fetch data",
                    Alert.AlertType.ERROR, null);
            return null;
        }
    }

    public static String[] getWeatherForecast(String city) {
        String[] weatherForecast = new String[5]; // Assuming we want data for the next 5 days

        try {
            String apiURL = "http://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + apiKey;
            URI url = new URI(apiURL);
            HttpURLConnection connection = (HttpURLConnection) url.toURL().openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            JSONArray forecastList = jsonObject.getJSONArray("list");

            for (int i = 0; i < 5; i++) {
                JSONObject forecast = forecastList.getJSONObject(i);
                String weatherData = forecast.getJSONArray("weather").getJSONObject(0).getString("main");
                weatherForecast[i] = weatherData;
            }

        } catch (IOException | URISyntaxException e) {
            new Alerts(app.title, "Error code 254: Unable to get the data",
                    Alert.AlertType.ERROR, null);
        }

        return weatherForecast;
    }

    public static String getWeatherStatus(String city) {
        String weatherData = getStatusOfToday(city);
        return parseWeatherStatus(weatherData);
    }

    // todo: Maybe overhaul this method to make it even better and faster
    public static String[][] searchResult(String searchString) {  // todo: switch this later to private String[][] searchResult

        // todo: Make an array to include all the problematic cities like "Tel Aviv"
        // todo: and scan the array with for loop and equal it to a 2d array
        if (Objects.equals(searchString, "Tel Aviv") || (Objects.equals(searchString, "Yafo"))) {
            searchString = "Tel Aviv-Yafo";
        }

        int[] elements = new int[6];
        elements[0] = 0; // Specify the number of array that will contain the array of cities + Specify the index of the "A" section column, contains cities
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

                if (city.equalsIgnoreCase(searchString)) {   // Comparing searchString to city
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
            new Alerts(app.title, "Error code 329: An error has been occurred",
                    Alert.AlertType.ERROR, null);
        }
        return data; // Will return null
    }

    public static HBox searchResultsContainer() {  // Results Data Container
        HBox searchContainer = new HBox();

        Table = new TableView<>();

        ObservableList<search> Data = FXCollections.observableArrayList();

        TableColumn<search, String> cityColumn = new TableColumn<>("City");
        TableColumn<search, String> countryColumn = new TableColumn<>("Country");
        TableColumn<search, String> provinceColumn = new TableColumn<>("Province");

        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        provinceColumn.setCellValueFactory(new PropertyValueFactory<>("province"));

        cityColumn.prefWidthProperty().set(100);
        countryColumn.prefWidthProperty().set(110);
        provinceColumn.prefWidthProperty().set(120);

        Data.add(new search("", "", "", ""));
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

    public static void performSearch() {
        try {
            String searchString = app.citySearchField.getText();
            String[][] Results = searchResult(searchString);
            ObservableList<search> Data = FXCollections.observableArrayList();

            for (int i = 0;  i < Results[0].length;  i++) {
                if (Check(Results)) {
                    Data.add(new search(
                            Results[0][i], // City
                            Results[1][i], // Country
                            Results[2][i],  // Province
                            Results[3][i] // Country Code
                    ));
                } else if (Data.isEmpty()) {
                    Label notFound = new Label(" ");
                    Data.add(new search("", "", "", ""));
                    Table.setPlaceholder(notFound);
                } else {
                    new Alerts(app.title, "Error code 390: Unable to perform search",
                            Alert.AlertType.ERROR, null);
                    return; // Stop the loop
                }
            }

            Table.setItems(Data);

        } catch (ArrayIndexOutOfBoundsException e) {
            new Alerts(app.title, "Error code 397: Unable to perform search",
                    Alert.AlertType.ERROR, null);
        }
    }

    public static String[] getData(TableView<search> Table) {
        try {
            ObservableList<search> selectedItems = Table.getSelectionModel().getSelectedItems();

            if (selectedItems != null && !selectedItems.isEmpty()) {
                String[] selectedData = new String[4];

                for (Weather.Data.search search : selectedItems) {
                    selectedData[0] = search.getCity();
                    selectedData[1] = search.getCountry();
                    selectedData[2] = search.getProvince();
                    selectedData[3] = search.getCode();
                } // [city, country, province, code]
                return selectedData;
            } else {
                new Alerts(app.title, "Error code 417: Invalid data",
                        Alert.AlertType.ERROR, null);
                return null;
            }
        } catch (NullPointerException e) {
            new Alerts(app.title, "Error code 417: Invalid data",
                    Alert.AlertType.ERROR, null);
            return null;
        }
    }
}

