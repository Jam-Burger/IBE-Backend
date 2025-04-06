package com.kdu.hufflepuff.ibe.repository.jpa;

import com.kdu.hufflepuff.ibe.model.entity.RoomDateLock;
import com.kdu.hufflepuff.ibe.model.entity.RoomDateLock.RoomDateLockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomDateLockRepository extends JpaRepository<RoomDateLock, RoomDateLockId> {
    @Modifying
    @Query("DELETE FROM RoomDateLock r WHERE r.createdAt <= :expirationThreshold")
    int deleteExpiredLocks(@Param("expirationThreshold") LocalDateTime expirationThreshold);

    @Query("""
        SELECT COUNT(r) > 0 FROM RoomDateLock r 
        WHERE r.id.roomId IN :roomIds 
        AND r.id.startDate <= :endDate 
        AND r.id.endDate >= :startDate
        AND r.createdAt > :expirationThreshold
        """)
    boolean existsAnyValidLock(
        @Param("roomIds") List<Long> roomIds,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("expirationThreshold") LocalDateTime expirationThreshold
    );
} 