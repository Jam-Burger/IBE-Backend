package com.kdu.hufflepuff.ibe.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateRangeUtils {
    public static List<DateRange> splitDateRange(LocalDate startDate, LocalDate endDate, int maxDays) {
        List<DateRange> ranges = new ArrayList<>();
        LocalDate currentStart = startDate;

        while (currentStart.isBefore(endDate)) {
            LocalDate currentEnd = currentStart.plusDays(maxDays);
            if (currentEnd.isAfter(endDate)) {
                currentEnd = endDate;
            }
            ranges.add(new DateRange(currentStart, currentEnd));
            currentStart = currentEnd;
        }

        return ranges;
    }

    @Data
    @AllArgsConstructor
    public static class DateRange {
        private LocalDate start;
        private LocalDate end;
    }
} 