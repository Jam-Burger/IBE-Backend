package com.kdu.hufflepuff.ibe.model.enums;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheNames {
    public static final String SPECIAL_OFFERS_CACHE = "specialOffers";
    public static final String CALENDAR_OFFERS_CACHE = "calendarOffers";
    public static final String PROMO_OFFERS_CACHE = "promoOffers";
    public static final String MINIMUM_ROOM_RATES_CACHE = "minimumRoomRates";
}