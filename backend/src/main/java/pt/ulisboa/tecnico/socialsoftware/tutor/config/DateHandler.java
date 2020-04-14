package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateHandler {

    /**
     *  Converts LocalDateTime to ISO8601 string format
     */
    public static String toString(LocalDateTime time) {
        return ZonedDateTime.of(time, ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     *  Frontend converts ISO8601 format to yyyy-MM-dd HH:mm in client timezone
     *
     *  If it returns that format, it means the date wasn't changed
     *  Because the component v-datetime-picker returns ISO8601 string format
     *
     *  Do not convert this to LocalDateTime because it does not have timezone information
     */
    public static boolean checkRequiresChange(String string) {
        return !string.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}");
    }

    /**
     *  Converts ISO8601 format to LocalDateTime to be stored in the database.
     *  Frontend uses "-" as null
     */
    public static LocalDateTime toLocalDateTime(String date) {
        if (date == null || date.isBlank() || date.equals("-")) {
            return null;
        } else {
            return LocalDateTime.ofInstant(Instant.parse(date), ZoneId.of(ZoneOffset.UTC.getId()));
        }
    }


}
