import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.Calendar;

public class City extends BorderPane {

    public City(String city) {
        Data.createClock();
        Main.clock = new Label();

        VBox topContainer = new VBox();

        Label City = new Label(city);
        Label Temperature = new Label((
                Data.getTemperature(city)
        ) + " °C");

        Main.clock.setId("Clock");
        Temperature.setId("basedTemperature");
        City.setId("city");

        topContainer.setSpacing(10);
        topContainer.setPadding(new Insets(10));
        topContainer.getChildren().addAll(City, Temperature);

        this.setTop(topContainer);
        this.setBottom(Main.clock);

        int hour = Clock.createCalendar().get(Calendar.HOUR_OF_DAY);
        setCityBackground(hour); // Apply the appropriate background based on the hour

    }

    // Set the background style based on the hour
    private void setCityBackground(int hour) {
        if (hour >= 19 || hour < 5) {
            this.getStyleClass().remove("day");
            this.getStyleClass().add("night");
        } else {
            this.getStyleClass().remove("night");
            this.getStyleClass().add("day");
        }
    }

}

class addCity {

    public static Button[] Tabs;
    private static int buttonCount = 0;

    public static void addTab(String[] data) {
        if (data != null && data.length >= 3) {
            String city = data[0];
            String code = data[3]; // Country Code

            // Check if the button with the same city already exists in the sidebar
            // Check if the button with the same city already exists in the sidebar
            for (Node node : Main.sidebarCities.getChildren()) {  // node represents each child node during each iteration.
                if (node instanceof VBox sidebar) {  // Checks if node is instance of VBox, if it is the for each loop will execute
                    for (Node button : sidebar.getChildren()) {  // For each loop for each child node in sidebar
                        if (button instanceof Button &&
                                ((Button) button).getText().equals(city + ", " + code)) {  // Check if the button is instance of the button, if it is have equal string it will stop
                            // todo: Make an error message
                            break;
                        } // To summarize: Checks the VBox container if it's true -> Checks all the buttons in the sidebar ->
                    } // If the text's button is equal, it will break
                }
            }

            // Creating a new button for the sidebar
            if (buttonCount < 11) {
                buttonCount++; // Increasing the count for the buttons
                Tabs[buttonCount] = new Button(city + ", " + code);  // Setting the city and the country code as the name of the button
                Button newButton = Tabs[buttonCount];  // a new variable new

                newButton.setOnAction(
                        event -> {
                            // Call a method to switch to a new layout
                            switchLayout(city);
                        }
                );
                newButton.setId("newButton");  // Implement CSS
                // Creating new VBox to contain the new button
                VBox sidebar = new VBox();
                sidebar.getChildren().add(0, Tabs[buttonCount]); // Add the button to the new VBox
                sidebar.setSpacing(10); // Add spacing between buttons

                // Consider to use this print in the final code
                System.out.println("Number of buttons that is exist: "+ buttonCount);
                Main.sidebarCities.getChildren().add(sidebar);
            } else if (buttonCount > 11) {  // todo: Make an error message
                // Check if the buttonCount is at the limit
                System.out.println("You have passed the amount of buttons allowed: " + buttonCount);
            }
        } else { // todo: Make an error message
            System.out.println(buttonCount + " Invalid data. Cannot add tab to the sidebar.");
        }
    }

    private static void switchLayout(String newCity) {
        City city = new City(newCity);
        BorderPane root = Main.getMainRoot();
        Clock.DayNightBackground();
        root.setCenter(city);
    }
}
