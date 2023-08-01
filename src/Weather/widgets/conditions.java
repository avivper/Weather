package Weather.widgets;

import Weather.main.Alerts;
import Weather.main.app;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class conditions {
    private static final Map<String, String> statusToImagePath = new HashMap<>();


    /*
    todo: get images for:
      Squall -
     */

    static {
        // set weather conditions and their corresponding image file paths to the map
        statusToImagePath.put("Clear", "data\\status\\Clear.png"); // Tested
        statusToImagePath.put("Clouds", "data\\status\\Clouds.png"); // Tested
        statusToImagePath.put("Rain", "data\\status\\Rain.png");  // Tested
        statusToImagePath.put("Drizzle", "data\\status\\Drizzle.png");  // Light rain
        statusToImagePath.put("Thunderstorm", "data\\status\\Thunderstorm.png");
        statusToImagePath.put("Snow", "data\\status\\Snow.png");
        statusToImagePath.put("Mist", "data\\status\\Mist.png");
        statusToImagePath.put("Smoke", "data\\status\\Smoke.png");
        statusToImagePath.put("Haze", "data\\status\\Haze.png");
        statusToImagePath.put("Dust", "data\\status\\Dust.png");
        statusToImagePath.put("Fog", "data\\status\\Fog.png");
        statusToImagePath.put("Squall", "data\\status\\Squall.png");
        statusToImagePath.put("Tornado", "data\\status\\Tornado.png");
    }

    public static Image getStatusImage(String status) throws FileNotFoundException {
        String imagePath = statusToImagePath.get(status);

        if (imagePath != null) {
            InputStream stream = new FileInputStream(imagePath);
            return new Image(stream);
        } else {
            new Alerts(app.title, "Error code 46: File not found",
                    Alert.AlertType.ERROR, null);
            return null;
        }
    }

}

