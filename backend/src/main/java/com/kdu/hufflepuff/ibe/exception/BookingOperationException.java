package com.kdu.hufflepuff.ibe.exception;

public class BookingOperationException extends BookingException {
    private BookingOperationException(String message) {
        super(message);
    }

    private BookingOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static BookingOperationException bookingNotFound(Long bookingId) {
        return new BookingOperationException(
            String.format("Booking with ID %d not found", bookingId));
    }

    public static BookingOperationException bookingAlreadyCancelled(Long bookingId) {
        return new BookingOperationException(
            String.format("Booking with ID %d is already cancelled", bookingId));
    }

    public static BookingOperationException bookingCreationFailed(String reason) {
        return new BookingOperationException(
            String.format("Failed to create booking: %s", reason));
    }

    public static BookingOperationException bookingCreationFailed(String reason, Throwable cause) {
        return new BookingOperationException(
            String.format("Failed to create booking: %s", reason),
            cause);
    }

    public static BookingOperationException guestCreationFailed(String reason) {
        return new BookingOperationException(
            String.format("Failed to create guest: %s", reason));
    }

    public static BookingOperationException guestNotFound(Long guestId) {
        return new BookingOperationException(String.format("Guest not found with Id: %d", guestId));
    }

    public static BookingOperationException updateAvailabilityFailed(Long availabilityId, Long bookingId) {
        return new BookingOperationException(
            String.format("Failed to update availability with ID %d for booking %d", availabilityId, bookingId));
    }

    public static BookingOperationException bookingCancellationFailed(Long bookingId) {
        return new BookingOperationException(
            String.format("Failed to cancel booking with ID %d", bookingId));
    }
} 