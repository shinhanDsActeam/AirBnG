package com.airbng.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime parse(String dateTimeStr) throws DateTimeParseException {
        return LocalDateTime.parse(dateTimeStr, FORMATTER);
    }

    public static boolean isStartTimeAfterEndTime(String startTime, String endTime) {
        LocalDateTime startDateTime = parse(startTime);
        LocalDateTime endDateTime = parse(endTime);
        return startDateTime.isAfter(endDateTime);
    }

    public static boolean isStartTimeEqualEndTime(String startTime, String endTime) {
        LocalDateTime startDateTime = parse(startTime);
        LocalDateTime endDateTime = parse(endTime);
        return startDateTime.isEqual(endDateTime);
    }
}
