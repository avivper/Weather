package weather.widgets;

import javafx.scene.control.Label;
import weather.main.Alerts;
import weather.main.City;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import java.util.Calendar;


public class Clock extends City {

    int hour;
    private static final Label clockLabel = new Label();
    private static final Label Status = new Label();

    public Label getClockLabel() {
        createClock();
        clockLabel.setId("Clock");
        return clockLabel;
    }

    public Calendar createCalendar() {
        long currentTimeMillis = System.currentTimeMillis(); // Time millisecond data that the memory store
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        return calendar;
    }

    public void createClock() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    Calendar calendar = createCalendar();

                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);

                    String DateTime = String.format(  // Arrange that the String will be in the format of the date
                            "%04d-%02d-%02d %02d:%02d:%02d",
                            year, month, day, hour, minute, second
                    );

                    clockLabel.setText(DateTime);
                }
                ));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public Label getStatus() {
        Calendar calendar = createCalendar();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        Status.setId("city");

        if (hour >= 22 || hour < 5) {
            Status.setText("Good Night");
        } else if (hour >= 19) {
            Status.setText("Good Evening");
        } else if (hour >= 12) {
            Status.setText("Have a nice noon");
        } else {
            Status.setText("Good Morning");
        }
        return Status;
    }

    public void setBackground(BorderPane root) {
        if (root != null) {
            Calendar calendar = createCalendar();
            hour = calendar.get(Calendar.HOUR_OF_DAY);

            if (hour >= 20 || hour < 5) {
                root.getStyleClass().remove("day");
                root.getStyleClass().add("night");
            } else {
                root.getStyleClass().remove("night");
                root.getStyleClass().add("day");
            }
        } else {
            new Alerts("Weather App", "Error code 73: Unable to load time data",
                    Alert.AlertType.ERROR, null);
        }
    }

}
