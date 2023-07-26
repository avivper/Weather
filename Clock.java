import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import java.util.Calendar;

public class Clock {

    static int hour;

    public static Calendar createCalendar() {
        long currentTimeMillis = System.currentTimeMillis(); // Time millisecond data that the memory store
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        return calendar;
    }

    public static void setStatus(BorderPane root) {
        if (root != null) {
            Calendar calendar = createCalendar();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            Main.Status.setId("city");

            if (hour >= 22 || hour < 5) {
                Main.Status.setText("Good Night");
            } else if (hour >= 19) {
                Main.Status.setText("Good Evening");
            } else if (hour >= 12) {
                Main.Status.setText("Have a nice noon");
            } else {
                Main.Status.setText("Good Morning");
            }

            if (hour >= 20 || hour < 5) {
                root.getStyleClass().remove("day");
                root.getStyleClass().add("night");
            } else {
                root.getStyleClass().remove("night");
                root.getStyleClass().add("day");
            }

        } else {
            Error.raiseError(0);
        }
    }

    public Clock() {
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

                    Main.clock.setText(DateTime);
                }
                ));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

}
