import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

    public static void DayNightBackground() {  // todo: Make this method get VBox
        if (Main.CenterContent != null) {
            Calendar calendar = createCalendar();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            System.out.println(hour);

            // Avoiding duplicates
            Main.CenterContent.getStyleClass().remove("night");
            Main.CenterContent.getStyleClass().remove("day");

            if (hour >= 19 || hour < 5) {
                Main.CenterContent.getStyleClass().remove("day");
                Main.CenterContent.getStyleClass().add("night");
            } else {
                Main.CenterContent.getStyleClass().remove("night");
                Main.CenterContent.getStyleClass().add("day");
            }
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
