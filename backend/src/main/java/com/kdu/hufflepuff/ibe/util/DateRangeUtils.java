package com.kdu.hufflepuff.ibe.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DateRangeUtils {

    public static List<DateRange> splitDateRange(LocalDate startDate, LocalDate endDate, int maxDays) {
        List<DateRange> ranges = new ArrayList<>();
        LocalDate currentStart = startDate;

        while (currentStart.isBefore(endDate) || currentStart.isEqual(endDate)) {
            LocalDate currentEnd = currentStart.plusDays(maxDays - 1);
            if (currentEnd.isAfter(endDate)) {
                currentEnd = endDate;
            }
            ranges.add(new DateRange(currentStart, currentEnd));
            currentStart = currentEnd.plusDays(1);
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