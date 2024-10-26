package App;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class Main {

    public static void main(String[] args) {
        System.out.println("hello app");

        // Method 1: Using LocalTime (Recommended)

// Create time 9:00:00
        LocalTime time = LocalTime.of(9, 0, 0); // hours, minutes, seconds
        System.out.println(time); // Prints: 09:00
// Format the time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = time.format(formatter);
        System.out.println(formattedTime); // Prints: 09:00:00

        String timeStr = "09:00:00";
        LocalTime timeeee = LocalTime.parse(timeStr, formatter);

        System.out.println(timeeee); // Prints: 09:00
    }
    
}
