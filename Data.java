import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Objects;

import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.DatabaseReader;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;

public class Data {

    private static final String Public_IP = PublicIPFinder();
    private static final String apiKey = "70f28ecc4f3d6c65e0897b61513c262f"; // todo: hide it somewhere and hook it from the web

    public static String PublicIPFinder() {
        try {
            URL url = new URL("https://api.ipify.org");  // Contain user IP address
            HttpURLConnection http_connection = (HttpURLConnection) url.openConnection();
            http_connection.setRequestMethod("GET");  // Making a request
            BufferedReader reader = new BufferedReader(new InputStreamReader(http_connection.getInputStream())); // Perform a request
            String Public_IP = reader.readLine();  // Get the data from the website
            reader.close();
            return Public_IP; // returns the IP of the user
        } catch (IOException e) {
            return e.toString();
        }
    }

    public static String[] getDataLocation() {   // Gets location by the IP of the user
        String[] data_location = new String[2];  // Array that will get 2 elements of string, one is city and the second is country
        File database = new File("data\\GeoLite2-City.mmdb"); // Read the db file
        try (DatabaseReader reader = new DatabaseReader.Builder(database).build()) {  // Build the file
            CityResponse response = reader.city(InetAddress.getByName(Public_IP)); // Gets the data based on Public IP
            City city = response.getCity();
            Country country = response.getCountry();
            data_location[0] = city.getName();  // Set the first element of the array as the string of the city
            data_location[1] = country.getName(); // Set the second element of the array as the string of the country
            return data_location;
        } catch (GeoIp2Exception | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getTemperature(String city) { // Except a city to output a weather data according to the city name
        String weatherData = getWeatherData(city, apiKey);
        return (int) parseTemperatureCelsius(weatherData);
    }

    public static int basedTemperatureLocation() {  // Will output the wheater based on the location of the user
        String[] data_location = getDataLocation();
        String city = data_location[0];
        String weatherData = getWeatherData(city, apiKey);
        return (int) parseTemperatureCelsius(weatherData);
    }

    private static String getWeatherData(String city, String apiKey) {
        try {
            String apiURL = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
            URL url = new URL(apiURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine())!= null) {
                response.append(line);
            }

            reader.close();
            return response.toString();
        } catch (IOException e) {
            return e.toString();
        }
    }

    private static double parseTemperatureCelsius(String weatherData) {
        JSONObject jsonObject = new JSONObject(weatherData);
        JSONObject mainObject = jsonObject.getJSONObject("main");
        double temperatureKelvin = mainObject.getDouble("temp");
        return temperatureKelvin - 273.15;
    }

    // todo: add them to the array and return all the array to the GUI
    // todo: represent the search result to the user
    // todo: Maybe overhaul this method to make it even better and faster
    public static String[][] searchResult(String searchString) {

        // todo: Make an array to include all the problematic cities like "Tel Aviv"
        // todo: and scan the array with for loop and equal it to a 2d array
        if (Objects.equals(searchString, "Tel Aviv") || (Objects.equals(searchString, "Yafo"))) {
            searchString = "Tel Aviv-Yafo";
        }

        int[] elements = new int[5];
        elements[0] = 0; // Specify the number of array that will contain the array of cities + Specify the index of the "A" section column, contains cities
        elements[1] = 1; // Specify the number of array that will contain the array of countries
        elements[2] = 2; // Specify the number of array that will contain the array of provinces
        elements[3] = 4; // Specify the index of the "E" section column, contains countries
        elements[4] = 7; // Specify the index of the "H" section column, will contain Province or states in countries like USA

        int ElementValue = -1;

        String[][] data = new String[3][50];  // Mostly contain null but will get the search results

        try (Reader reader = new FileReader("data\\worldcities.csv");  // Performing csv read
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

            for (CSVRecord csvRecord : csvParser) {    // For each loop
                String city = csvRecord.get(elements[0]);  // City string value
                String country = csvRecord.get(elements[3]);  // Country string value
                String province = csvRecord.get(elements[4]);  // Province string value

                if (city.equalsIgnoreCase(searchString)) {   // Comparing searchString to city
                    ElementValue++;
                    data[elements[0]][ElementValue] = city;   // data[0][ElementValue] = city;
                    data[elements[1]][ElementValue] = country; // data[1][ElementValue] = country;
                    data[elements[2]][ElementValue] = province; // data[2][ElementValue] = province;
                }
            }

            if (ElementValue > -1 && ElementValue != 0) {
                ElementValue++;
                String[][] results = new String[3][ElementValue];  // New array to assign the results from the search

                for (int i = 0; i < ElementValue; i++) {  // Assign the results value
                    results[elements[0]][i] = data[elements[0]][i]; // city
                    results[elements[1]][i] = data[elements[1]][i]; // country
                    results[elements[2]][i] = data[elements[2]][i]; // province
                }
                return results;

            } else if (ElementValue == 0) {
                String[][] results = new String[3][1];
                results[elements[0]][0] = data[elements[0]][0]; // city
                results[elements[1]][0] = data[elements[1]][0]; // country
                results[elements[2]][0] = data[elements[2]][0]; // province
                return results;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return data; // Will return null
    }
}

