import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class Cities {

    public static Button[] Tabs;
    private static int buttonCount = 0;
    private static boolean alreadyExist = false;

    public static void addCity(String[] data) {
        if (data != null && data.length >= 3) {
            String city = data[0];
            String code = data[3];

            // Check if the button with the same city already exists in the sidebar
            for (Node node : Layouts.sidebarCities.getChildren()) {  // node represents each child node during each iteration.
                if (node instanceof VBox sidebar) {  // Checks if node is instance of VBox, if it is the for each loop will execute
                    for (Node button : sidebar.getChildren()) {  // For each loop for each child node in sidebar
                        if (button instanceof Button &&
                                ((Button) button).getText().equals(city)) {  // Check if the button is instance of the button, if it is have equal string it will stopped
                            alreadyExist = true;
                            break;
                        } // To summarize: Checks the VBox container if it's true -> Checks all the buttons in the sidebar ->
                    } // If the text's button is equal, it will break
                } else if (alreadyExist) { // If alreadyExist is true
                    break;
                }
            }

            // Creating a new button for the sidebar
            if (!alreadyExist && buttonCount <= 11) {
                buttonCount++;  // Increasing the count for the buttons
                Button newCity = Tabs[buttonCount];
                newCity.setText(city + ", " + code);
                newCity.setOnAction(
                        event -> {
                            System.out.println(city + " " + code);  // todo: will switch stages
                        }
                );
                newCity.setId("newButton");  // Implement CSS

                // Creating new VBox to contain the new button
                VBox sidebar = new VBox();
                sidebar.getChildren().add(0,  newCity); // Add the button to the new VBox
                sidebar.setSpacing(10); // Add spacing between buttons

                // Consider to use this print in the final code
                System.out.println("Number of buttons that is exist: "+ buttonCount);
                Layouts.sidebarCities.getChildren().add(sidebar);
            } else if (buttonCount >= 11) {  // todo: Make an error message
                // Checks if the buttonCount is at the limit
                System.out.println("You have passed the amount of buttons allowed: " + buttonCount);
            }
        } else {  // todo: Make an error message
            System.out.println(buttonCount + " Invalid data. Cannot add tab to the sidebar.");
        }
    }
}
