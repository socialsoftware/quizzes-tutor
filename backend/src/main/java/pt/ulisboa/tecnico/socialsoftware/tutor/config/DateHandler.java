package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateHandler {

    public static String toString(LocalDateTime time) {
        return ZonedDateTime.of(time, ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT);
    }

    public static LocalDateTime toLocalDateTime(String date) {
        if (date == null || date.isBlank() || date.equals("-")) {
            return null;
        } else if (date.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } else {
            return LocalDateTime.ofInstant(Instant.parse(date), ZoneId.of(ZoneOffset.UTC.getId()));
        }
    }
}
