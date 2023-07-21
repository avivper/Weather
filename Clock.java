import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.Calendar;

public class Clock {

    public static int hour;

    public static void LiveClock() {
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(1), event -> updateLiveClock()
                )
        );
        timeline.play();
    }

    public static void updateLiveClock() {
        long currentTimeMillis = System.currentTimeMillis();  // Time millisecond data that the memory store
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);

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

        Layouts.clock.setText(DateTime);
    }
}
