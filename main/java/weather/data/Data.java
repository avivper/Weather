package weather.data;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import javafx.scene.control.Alert;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import weather.main.Alerts;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public class Data {

    public String Public_IP = PublicIPFinder();
    private final String DataPath = "data\\GeoLite2-City.mmdb";
    private final String apiKey;


    // todo: make it more steady with various url services
    public String PublicIPFinder() {
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
                new Alerts("Weather App", "Error code 67: Unable to connect, try again later",
                        Alert.AlertType.ERROR, null);
                return "Error: Unable to fetch public IP. HTTP Response Code: " + http_connection.getResponseCode();
            }
        } catch (IOException | URISyntaxException e ) {
            new Alerts("Weather App", "Error code 71: Unable to connect, try again later",
                    Alert.AlertType.ERROR, null);
            return "Error " + e.getMessage();
        }
    }

    public String checkCity(String city) {
        return URLEncoder.encode(city, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }

    public String getCity() {   // Gets location by the IP of the user
        if (!Objects.equals(Public_IP, "Error Connect timed out")) {
            File database = new File(DataPath);  // Read the database
            try (DatabaseReader reader = new DatabaseReader.Builder(database).build()) {  // Build
                CityResponse response = reader.city(InetAddress.getByName(Public_IP));
                City city = response.getCity();
                return city.getName();
            } catch (GeoIp2Exception | IOException e) {
                new Alerts("Weather App", "Error code 86: Unable to fetch data, try again later",
                        Alert.AlertType.ERROR, null);
                return null;
            }
        } else {
            new Alerts("Weather App", "Error code 72: An error has been occurred",
                    Alert.AlertType.ERROR, null);
            return null;
        }
    }

    public String getCode() {
        if (!Objects.equals(Public_IP, "Error Connect timed out")) {

            File database = new File(DataPath);
            try (DatabaseReader reader = new DatabaseReader.Builder(database).build()) {
                CountryResponse response = reader.country(InetAddress.getByName(Public_IP));
                Country country = response.getCountry();
                String country_name = country.getName();

                if (country_name != null) {
                    Locale[] locales = Locale.getAvailableLocales();
                    for (Locale locale : locales) {
                        if (country_name.equalsIgnoreCase(locale.getDisplayCountry())) {
                            return locale.getCountry();
                        }
                    }
                }

            } catch (IOException | GeoIp2Exception e) {
                new Alerts("Weather App", "Error code 72: An error has been occurred",
                        Alert.AlertType.ERROR, null);
                return  null;
            }
        }
        return null;
    }

    public String getProvince(String city) throws FileNotFoundException {
        Search SearchStage = new Search(null);
        SearchStage.close();
        String[][] Get = SearchStage.searchResult(city);
        return Get[2][0];
    }


    public  int getTemperature(String city, String code, String province) { // Except a city to output a weather data according to the city name
        String weatherData = getWeatherData(city, code, province);
        return (int) parseTemperatureCelsius(weatherData);
    }

    private String getWeatherData(String city, String code, String province) {
        try {
            
            String apiURL = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "," + province + "," + code + "&appid=" + apiKey;
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
            new Alerts("Weather App", "Error code 120: Unable to fetch data",
                    Alert.AlertType.ERROR, null);
            return e.toString();
        }
    }

    private static double parseTemperatureCelsius(String weatherData) {
        try {
            JSONObject jsonObject = new JSONObject(weatherData);
            JSONObject mainObject = jsonObject.getJSONObject("main");
            double temperatureKelvin = mainObject.getDouble("temp");
            return temperatureKelvin - 273.15;
        } catch (JSONException e) {
            System.out.println("Weather Data: " + weatherData);
            e.printStackTrace();
            return 0.0;
        }
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

    public double[] getForecast(String city, String code, String province) {
        String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "," + province + "," + code + "&appid=" + apiKey;
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
                new Alerts("Weather App", "Error code 176: An error has been occurred",
                        Alert.AlertType.ERROR, null);
            }
        } catch (IOException | URISyntaxException e) {
            new Alerts("Weather App", "Error code 91: An error has been occurred",
                    Alert.AlertType.ERROR, null);
        }
        return temperatureForecast;
    }

    private String parseWeatherStatus(String weatherData) {
        JSONObject jsonObject = new JSONObject(weatherData);
        JSONArray weatherArray = jsonObject.getJSONArray("weather");

        if (!weatherArray.isEmpty()) {
            JSONObject weatherObject = weatherArray.getJSONObject(0);
            return weatherObject.getString("main");

        } else {
            new Alerts("Weather App", "Error code 195: Unable to fetch data",
                    Alert.AlertType.ERROR, null);
            return "Unknown";
        }
    }

    private String getStatusOfToday(String city, String code, String province) {
        try {
            String apiURL = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "," + province + "," + code + "&appid=" + apiKey;
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
            new Alerts("Weather App", "Error code 219: Unable to fetch data",
                    Alert.AlertType.ERROR, null);
            return null;
        }
    }

    public String[] getWeatherForecast(String city, String code, String province) {
        String[] weatherForecast = new String[5]; // Assuming we want data for the next 5 days

        try {
            String apiURL = "http://api.openweathermap.org/data/2.5/forecast?q=" + city + "," + province + "," + code + "&appid=" + apiKey;
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
            new Alerts("Weather App", "Error code 254: Unable to get the data",
                    Alert.AlertType.ERROR, null);
        }

        return weatherForecast;
    }

    public String getWeatherStatus(String city, String code, String province) {
        String weatherData = getStatusOfToday(city, code, province);
        return parseWeatherStatus(weatherData);
    }



}
