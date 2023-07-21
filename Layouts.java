import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class Layouts {

    private static final String[] location = Data.getDataLocation();  // todo: fix offline problem
    public static Label clock;
    public static VBox sidebarCities;  // Container for new Cities that was added by the user

    public  static HBox titleBar() {
        HBox titleBar = new HBox();
        Label title = new Label("Weather App");
        Button plusButton = new Button();

        // Implement the CSS
        plusButton.setId("plus-button");
        title.setId("title");

        plusButton.setOnAction(  // Open the search window to add cities to the app
                event -> {
                    if (!Main.SearchisOpen) {
                        Main.search();
                    }
                }
        );

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        titleBar.setPadding(new Insets(5));
        titleBar.setSpacing(10);
        titleBar.setId("addBar");
        titleBar.getChildren().addAll(title, region, plusButton);

        return titleBar;
    }

    public static VBox createCenterContent() {
        VBox centerContent = new VBox();
        GridPane dataContainer = new GridPane();

        Label HomeCity = new Label(location[0]); // Contains the user's city location based on Public IP Address
        Label HomeTemperature = new Label(Data.basedTemperatureLocation() + " °C");
        clock = new Label();

        // Implement the UI
        clock.setId("Clock");
        HomeTemperature.setId("basedTemperature");
        HomeCity.setId("city");

        dataContainer.setPadding(new Insets(10));
        dataContainer.setVgap(10);
        dataContainer.setHgap(10);
        dataContainer.add(HomeCity, 0, 5, 2, 1);
        dataContainer.add(HomeTemperature, 0, 6, 2 , 2);
        dataContainer.add(clock, 1, 0, 1, 2);

        // Set constraints to place the live clock on the top right
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS); // Make the first column grow to fill any remaining space
        dataContainer.getColumnConstraints().addAll(column1);

        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(Priority.ALWAYS); // Make the second row grow to fill any remaining space
        dataContainer.getRowConstraints().addAll(row1, row2);

        // Align the city and temperature labels to the center
        GridPane.setHalignment(HomeTemperature, HPos.CENTER);
        GridPane.setHalignment(HomeCity, HPos.CENTER);

        centerContent.getChildren().add(dataContainer);
        centerContent.setPadding(new Insets(20));

        Clock.LiveClock();

        return centerContent;
    }

    public static VBox createSidebar() {  // Side bar, mostly contain new cities that was added by the user
        VBox sidebar = new VBox();
        Cities.Tabs = new Button[12];

        Button Home = Cities.Tabs[0];
        Home.setText("Home");  // Saving the first Button for Home Stage that will display location's weather
        Home.setId("newButton");

        VBox.setVgrow(sidebar, Priority.ALWAYS);
        sidebar.setFillWidth(true);
        sidebar.setPrefWidth(150);
        sidebar.setPadding(new Insets(2));
        sidebar.setSpacing(10);  // Set a space between buttons
        sidebar.getStyleClass().add("sidebar");
        sidebar.getChildren().addAll(Home);

        return sidebar;
    }
}
