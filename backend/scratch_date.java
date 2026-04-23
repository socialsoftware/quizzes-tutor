import java.time.ZonedDateTime;
public class scratch_date {
    public static void main(String[] args) {
        try {
            System.out.println("Trying without Z:");
            ZonedDateTime.parse("2024-04-16T12:00:00");
        } catch(Exception e) {
            System.out.println("EXCEPTION ON NO Z: " + e.getMessage());
        }
        try {
            System.out.println("Trying yyyy-MM-dd HH:mm:ss:");
            ZonedDateTime.parse("2024-04-16 12:00:00");
        } catch(Exception e) {
            System.out.println("EXCEPTION ON SPACE: " + e.getMessage());
        }
        try {
            System.out.println("Trying format from ConvertDateService:");
            ZonedDateTime.parse("2024-04-16 12:00");
        } catch(Exception e) {
            System.out.println("EXCEPTION ON 16 chars: " + e.getMessage());
        }
    }
}
