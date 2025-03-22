package com.kdu.hufflepuff.ibe.util;

import com.kdu.hufflepuff.ibe.model.dto.in.CreateSpecialDiscountRequest;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.LongStream;

public class SpecialDiscountDataGenerator {
    private static final Map<LocalDate, String> PUBLIC_HOLIDAYS = new HashMap<>() {{
        // March 2025 Holidays
        put(LocalDate.of(2025, 3, 1), "Maha Shivaratri");
        put(LocalDate.of(2025, 3, 6), "Holika Dahan");
        put(LocalDate.of(2025, 3, 7), "Holi");
        put(LocalDate.of(2025, 3, 8), "International Women's Day");
        put(LocalDate.of(2025, 3, 14), "Chaitra Sukhladi (Ugadi)");
        put(LocalDate.of(2025, 3, 21), "Spring Equinox");
        put(LocalDate.of(2025, 3, 25), "Telugu New Year");
        put(LocalDate.of(2025, 3, 29), "Rama Navami");
        
        // April 2025 Holidays
        put(LocalDate.of(2025, 4, 1), "April Fool's Day");
        put(LocalDate.of(2025, 4, 11), "Vaisakhi");
        put(LocalDate.of(2025, 4, 13), "Bohag Bihu");
        put(LocalDate.of(2025, 4, 14), "Tamil New Year (Puthandu)");
        put(LocalDate.of(2025, 4, 15), "Vishu");
        put(LocalDate.of(2025, 4, 18), "Good Friday");
        put(LocalDate.of(2025, 4, 20), "Easter Sunday");
        put(LocalDate.of(2025, 4, 22), "Earth Day");
        put(LocalDate.of(2025, 4, 23), "Mahavir Jayanti");
        
        // May 2025 Holidays
        put(LocalDate.of(2025, 5, 1), "Labor Day");
        put(LocalDate.of(2025, 5, 4), "Buddha Purnima");
        put(LocalDate.of(2025, 5, 11), "Mother's Day");
        put(LocalDate.of(2025, 5, 14), "Akshaya Tritiya");
        put(LocalDate.of(2025, 5, 24), "Hanuman Jayanti");
        put(LocalDate.of(2025, 5, 26), "Memorial Day");
        
        // June 2025 Holidays
        put(LocalDate.of(2025, 6, 1), "Global Day of Parents");
        put(LocalDate.of(2025, 6, 3), "Ganga Dussehra");
        put(LocalDate.of(2025, 6, 5), "World Environment Day");
        put(LocalDate.of(2025, 6, 14), "Nirjala Ekadashi");
        put(LocalDate.of(2025, 6, 15), "Father's Day");
        put(LocalDate.of(2025, 6, 19), "Juneteenth");
        put(LocalDate.of(2025, 6, 21), "Summer Solstice / International Yoga Day");
        put(LocalDate.of(2025, 6, 23), "Rath Yatra");
    }};

    // Generate array of property IDs from 1 to 24
    private static final Long[] PROPERTY_IDS = LongStream.rangeClosed(1, 24)
            .boxed()
            .toArray(Long[]::new);
            
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        List<CreateSpecialDiscountRequest> requests = generateRequests();
        
        // Print curl commands for easy testing
        requests.forEach(request -> {
            System.out.println("curl -X POST 'http://localhost:8080/api/v1/1/" + request.getPropertyId() + "/special-discounts' \\");
            System.out.println("-H 'Content-Type: application/json' \\");
            System.out.println("-d '{");
            System.out.println("    \"propertyId\": " + request.getPropertyId() + ",");
            System.out.println("    \"discountDate\": \"" + request.getDiscountDate() + "\",");
            System.out.println("    \"discountPercentage\": " + request.getDiscountPercentage());
            System.out.println("}'");
            System.out.println();
        });

        // Print summary
        System.out.println("Generated " + requests.size() + " discount requests");
        System.out.println("Properties covered: " + PROPERTY_IDS.length);
        System.out.println("Date range: March 1, 2025 - June 30, 2025");
        System.out.println("Public holidays included: " + PUBLIC_HOLIDAYS.size());
    }

    public static List<CreateSpecialDiscountRequest> generateRequests() {
        List<CreateSpecialDiscountRequest> requests = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2025, 3, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);

        // Generate discounts for each property
        for (Long propertyId : PROPERTY_IDS) {
            // Add public holiday discounts (higher probability and higher discounts)
            PUBLIC_HOLIDAYS.forEach((date, holiday) -> {
                if (RANDOM.nextDouble() < 0.85) { // 85% chance of having holiday discount
                    requests.add(createRequest(
                        propertyId,
                        date,
                        25.0 + RANDOM.nextDouble() * 35.0 // Holiday discounts: 25-60%
                    ));
                }
            });

            // Add weekend discounts with different patterns
            LocalDate current = startDate;
            while (!current.isAfter(endDate)) {
                if (current.getDayOfWeek().getValue() >= 5) { // Friday, Saturday, Sunday
                    double weekendChance = switch (current.getDayOfWeek().getValue()) {
                        case 5 -> 0.6;  // Friday: 60% chance
                        case 6 -> 0.7;  // Saturday: 70% chance
                        case 7 -> 0.5;  // Sunday: 50% chance
                        default -> 0.0;
                    };

                    if (RANDOM.nextDouble() < weekendChance && !PUBLIC_HOLIDAYS.containsKey(current)) {
                        // Higher discounts for Saturday nights
                        double baseDiscount = current.getDayOfWeek().getValue() == 6 ? 
                            15.0 : 10.0;
                        double maxDiscount = current.getDayOfWeek().getValue() == 6 ?
                            30.0 : 25.0;
                            
                        requests.add(createRequest(
                            propertyId,
                            current,
                            baseDiscount + RANDOM.nextDouble() * (maxDiscount - baseDiscount)
                        ));
                    }
                }
                current = current.plusDays(1);
            }

            // Add off-peak weekday discounts
            current = startDate;
            while (!current.isAfter(endDate)) {
                if (RANDOM.nextDouble() < 0.15 // 15% chance of random discount
                    && !PUBLIC_HOLIDAYS.containsKey(current)
                    && current.getDayOfWeek().getValue() <= 4) { // Only on weekdays
                    requests.add(createRequest(
                        propertyId,
                        current,
                        5.0 + RANDOM.nextDouble() * 15.0 // Off-peak discounts: 5-20%
                    ));
                }
                current = current.plusDays(1);
            }
        }

        return requests;
    }

    private static CreateSpecialDiscountRequest createRequest(Long propertyId, LocalDate date, Double discountPercentage) {
        CreateSpecialDiscountRequest request = new CreateSpecialDiscountRequest();
        request.setPropertyId(propertyId);
        request.setDiscountDate(date);
        request.setDiscountPercentage(Math.round(discountPercentage * 100.0) / 100.0); // Round to 2 decimal places
        return request;
    }
} 