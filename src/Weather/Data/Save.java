package Weather.Data;

import Weather.main.Alerts;
import Weather.main.app;
import javafx.scene.control.Alert;

import java.io.*;
import java.util.List;

public class Save {

    public static void load() {
        String file = "data\\save";

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file)
        )) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(", ");

                String name = data[0] + ", " + data[1];
                if (!name.equals(app.HomeCity)) {
                    Load.addTab(data[0], data[1]);
                } else {
                    new Alerts(app.title, "Error code 27:  Error has been occurred",
                            Alert.AlertType.ERROR, null);
                }
            }

        } catch (IOException e) {
            new Alerts(app.title, "Error: " + e.getMessage(),
                    Alert.AlertType.ERROR, null);
        }
    }

    public static void save(List<String> data) {
        String file = "data\\save";

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file))) {

            for (int i = 1; i < Load.Cities.size(); i++) {
                writer.write(Load.Cities.get(i));
                writer.newLine();
            }

            new Alerts(app.title, "Saved successfully",
                    Alert.AlertType.INFORMATION, null);

        } catch (IOException e) {
            new Alerts(app.title, "Error: " + e.getMessage(),
                    Alert.AlertType.ERROR, null);
        }
    }
}
