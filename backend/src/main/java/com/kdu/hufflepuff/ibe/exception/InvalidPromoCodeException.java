package com.kdu.hufflepuff.ibe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPromoCodeException extends InvalidRequestException {
    public InvalidPromoCodeException(String message) {
        super("Promo code '" + message);
    }

    public static InvalidPromoCodeException notFound(String promoCode) {
        return new InvalidPromoCodeException(promoCode + "' not found");
    }

    public static InvalidPromoCodeException expired(String promoCode) {
        return new InvalidPromoCodeException(promoCode + "' has expired");
    }

    public static InvalidPromoCodeException notApplicable(String promoCode) {
        return new InvalidPromoCodeException(promoCode + "' is not applicable for this booking");
    }
} 