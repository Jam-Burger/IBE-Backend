package com.kdu.hufflepuff.ibe.util;

import com.kdu.hufflepuff.ibe.model.graphql.Booking;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GuestCountConverter {

    public static GuestCounts extractGuestCounts(Map<String, Integer> guests) {
        int adults = guests.getOrDefault("Adults", 0);
        int seniorCitizens = guests.getOrDefault("Senior Citizens", 0);
        int teens = guests.getOrDefault("Teens", 0);
        int children = guests.getOrDefault("Children", 0);
        int infants = guests.getOrDefault("Infants", 0);

        return new GuestCounts(
            adults + seniorCitizens, // Adults in Booking includes both Adults and Senior Citizens
            children + teens + infants // Store Teens count in extension
        );
    }

    public static void updateBookingWithGuestCounts(Booking booking, GuestCounts guestCounts) {
        booking.setAdultCount(guestCounts.adults);
        booking.setChildCount(guestCounts.children);
    }

    public static int getTotalGuestCount(GuestCounts guestCounts) {
        return guestCounts.adults + guestCounts.children;
    }

    public static class GuestCounts {
        public final int adults;
        public final int children;

        private GuestCounts(int adults, int children) {
            this.adults = adults;
            this.children = children;
        }
    }
}