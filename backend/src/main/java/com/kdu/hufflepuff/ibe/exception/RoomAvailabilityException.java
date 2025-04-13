package com.kdu.hufflepuff.ibe.exception;

public class RoomAvailabilityException extends BookingException {
    private RoomAvailabilityException(String message) {
        super(message);
    }

    public static RoomAvailabilityException notEnoughRooms(int requested, int available) {
        return new RoomAvailabilityException(
            String.format("Not enough rooms available. Requested: %d, Available: %d", requested, available));
    }

    public static RoomAvailabilityException noRoomsAvailableForCount(int count) {
        return new RoomAvailabilityException(
            String.format("No rooms available for the selected count: %d", count));
    }

    public static RoomAvailabilityException roomAlreadyBooked(Long roomId) {
        return new RoomAvailabilityException(
            String.format("Room with ID %d is already booked for the selected dates", roomId));
    }
} 