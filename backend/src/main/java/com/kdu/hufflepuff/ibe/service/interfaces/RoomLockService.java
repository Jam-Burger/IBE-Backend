package com.kdu.hufflepuff.ibe.service.interfaces;

import java.time.LocalDate;
import java.util.List;

public interface RoomLockService {
    /**
     * Creates temporary locks for the specified rooms for a given date range.
     * These locks will automatically expire after a configured time period.
     *
     * @param roomIds List of room IDs to lock
     * @param startDate Start date of the lock period
     * @param endDate End date of the lock period
     */
    void createTemporaryLocks(List<Long> roomIds, LocalDate startDate, LocalDate endDate);

    /**
     * Checks if any of the specified rooms are currently locked for the given date range.
     *
     * @param roomIds List of room IDs to check
     * @param startDate Start date to check
     * @param endDate End date to check
     * @return true if any room is locked, false otherwise
     */
    boolean areRoomsLocked(List<Long> roomIds, LocalDate startDate, LocalDate endDate);

    /**
     * Releases locks for the specified rooms.
     *
     * @param roomIds List of room IDs to unlock
     * @param startDate Start date of the lock period
     * @param endDate End date of the lock period
     */
    void releaseLocks(List<Long> roomIds, LocalDate startDate, LocalDate endDate);

    /**
     * Cleans up expired locks. This is typically called automatically by a scheduled task.
     */
    void cleanupExpiredLocks();
} 