import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Conditions {
    private static final Map<String, String> statusToImagePath = new HashMap<>();


    /*
    todo: get images for:
      Clear - V
      Clouds -
      Rain -
      Drizzle -
      Thunderstorm -
      Snow -
      Mist -
      Smoke -
      Haze -
      Dust -
      Fog -
      Squall -
      Tornado -
     */

    static {
        // set weather conditions and their corresponding image file paths to the map
        statusToImagePath.put("Clear", "data/status/Clear.png");
        statusToImagePath.put("Clouds", "data/status/Clouds.png");
        statusToImagePath.put("Rain", "data/status/Rain.png");
        statusToImagePath.put("Drizzle", "data/status/Drizzle.png");
        statusToImagePath.put("Thunderstorm", "data/status/Thunderstorm.png");
        statusToImagePath.put("Snow", "data/status/Snow.png");
        statusToImagePath.put("Mist", "data/status/Mist.png");
        statusToImagePath.put("Smoke", "data/status/Smoke.png");
        statusToImagePath.put("Haze", "data/status/Haze.png");
        statusToImagePath.put("Dust", "data/status/Dust.png");
        statusToImagePath.put("Fog", "data/status/Fog.png");
        statusToImagePath.put("Squall", "data/status/Squall.png");
        statusToImagePath.put("Tornado", "data/status/Tornado.png");
    }

    public static Image getStatusImage(String status) throws FileNotFoundException {
        String imagePath = statusToImagePath.get(status);

        if (imagePath != null) {
            InputStream stream = new FileInputStream(imagePath);
            return new Image(stream);
        } else {
            Error.raiseError(348);
            return null;
        }
    }
}
