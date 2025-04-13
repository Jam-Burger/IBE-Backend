package com.kdu.hufflepuff.ibe.exception;

public class PromotionException extends BookingException {
    private PromotionException(String message) {
        super(message);
    }

    public static PromotionException promotionNotFound(String promotionId) {
        return new PromotionException(
            String.format("Promotion with ID %s not found", promotionId));
    }

    public static PromotionException invalidPromotionFormat(String promotionId) {
        return new PromotionException(
            String.format("Invalid promotion ID format: %s. Expected format: G_[id] or R_[id]", promotionId));
    }

    public static PromotionException specialOfferNotFound(Long specialOfferId) {
        return new PromotionException(
            String.format("Special offer with ID %d not found", specialOfferId));
    }

    public static PromotionException promotionExpired(String promotionId) {
        return new PromotionException(
            String.format("Promotion with ID %s has expired", promotionId));
    }

    public static PromotionException promotionNotApplicable(String promotionId, String reason) {
        return new PromotionException(
            String.format("Promotion with ID %s is not applicable: %s", promotionId, reason));
    }
} 