package com.kdu.hufflepuff.ibe.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateFormatUtils {
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    /**
     * Converts a LocalDate to an ISO formatted string at UTC midnight for GraphQL queries.
     * This ensures consistent date handling across the application, particularly for GraphQL parameters.
     *
     * @param date The LocalDate to convert
     * @return ISO formatted string representing the date at UTC midnight
     */
    public static String toGraphQLDateString(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay()
            .atOffset(ZoneOffset.UTC)
            .format(ISO_DATE_FORMATTER);
    }
} 