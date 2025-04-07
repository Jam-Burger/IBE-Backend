package com.kdu.hufflepuff.ibe.mapper;

import com.kdu.hufflepuff.ibe.model.entity.GuestExtension;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GuestExtensionMapper {

    public static GuestExtension fromFormData(Map<String, String> formData) {
        return GuestExtension.builder()
            // Billing Information
            .billingFirstName(formData.get("billingFirstName"))
            .billingLastName(formData.get("billingLastName"))
            .billingEmail(formData.get("billingEmail"))
            .billingPhone(formData.get("billingPhone"))
            .billingAddress1(formData.get("billingAddress1"))
            .billingAddress2(formData.get("billingAddress2"))
            .billingCity(formData.get("billingCity"))
            .billingState(formData.get("billingState"))
            .billingZip(formData.get("billingZip"))
            .billingCountry(formData.get("billingCountry"))
            // Traveler Information
            .travelerFirstName(formData.get("travelerFirstName"))
            .travelerLastName(formData.get("travelerLastName"))
            .travelerEmail(formData.get("travelerEmail"))
            .travelerPhone(formData.get("travelerPhone"))
            // Consent Information
            .agreedToTerms(Boolean.parseBoolean(formData.get("agreedToTerms")))
            .receiveOffers(Boolean.parseBoolean(formData.get("receiveOffers")))
            .build();
    }

    public static GuestExtension fromFormDataWithValidation(Map<String, String> formData) {
        validateRequiredFields(formData);
        return fromFormData(formData);
    }

    private static void validateRequiredFields(Map<String, String> formData) {
        // Required billing fields
        validateField(formData, "billingFirstName", "Billing first name is required");
        validateField(formData, "billingLastName", "Billing last name is required");
        validateField(formData, "billingEmail", "Billing email is required");
        validateField(formData, "billingPhone", "Billing phone is required");
        validateField(formData, "billingAddress1", "Billing address is required");
        validateField(formData, "billingCity", "Billing city is required");
        validateField(formData, "billingState", "Billing state is required");
        validateField(formData, "billingZip", "Billing zip code is required");
        validateField(formData, "billingCountry", "Billing country is required");

        // Required traveler fields
        validateField(formData, "travelerFirstName", "Traveler first name is required");
        validateField(formData, "travelerLastName", "Traveler last name is required");
        validateField(formData, "travelerEmail", "Traveler email is required");
        validateField(formData, "travelerPhone", "Traveler phone is required");

        // Required consent
        validateField(formData, "agreedToTerms", "Terms agreement is required");
    }

    private static void validateField(Map<String, String> formData, String fieldName, String errorMessage) {
        String value = formData.get(fieldName);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
} 