package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.entity.RoomDateLock;
import com.kdu.hufflepuff.ibe.model.entity.RoomDateLock.RoomDateLockId;
import com.kdu.hufflepuff.ibe.repository.jpa.RoomDateLockRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomLockServiceImpl implements RoomLockService {
    private static final int LOCK_EXPIRATION_MINUTES = 15;
    
    private final RoomDateLockRepository roomDateLockRepository;

    @Override
    @Transactional
    public void createTemporaryLocks(List<Long> roomIds, LocalDate startDate, LocalDate endDate) {
        if (areRoomsLocked(roomIds, startDate, endDate)) {
            throw new IllegalStateException("One or more rooms are already locked for the specified date range");
        }

        LocalDateTime createdAt = LocalDateTime.now();
        List<RoomDateLock> locks = roomIds.stream()
            .map(roomId -> RoomDateLock.builder()
                .id(new RoomDateLockId(roomId, startDate, endDate))
                .createdAt(createdAt)
                .build())
            .toList();
        
        roomDateLockRepository.saveAll(locks);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean areRoomsLocked(List<Long> roomIds, LocalDate startDate, LocalDate endDate) {
        LocalDateTime expirationThreshold = LocalDateTime.now().minusMinutes(LOCK_EXPIRATION_MINUTES);
        return roomDateLockRepository.existsAnyValidLock(roomIds, startDate, endDate, expirationThreshold);
    }

    @Override
    @Transactional
    public void releaseLocks(List<Long> roomIds, LocalDate startDate, LocalDate endDate) {
        roomIds.forEach(roomId -> {
            RoomDateLockId lockId = new RoomDateLockId(roomId, startDate, endDate);
            roomDateLockRepository.findById(lockId).ifPresent(lock -> {
                roomDateLockRepository.delete(lock);
            });
        });
    }

    @Override
    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void cleanupExpiredLocks() {
        LocalDateTime expirationThreshold = LocalDateTime.now().minusMinutes(LOCK_EXPIRATION_MINUTES);
        int deletedCount = roomDateLockRepository.deleteExpiredLocks(expirationThreshold);
        if (deletedCount > 0) {
            log.info("Cleaned up {} expired room date locks", deletedCount);
        }
    }
} 