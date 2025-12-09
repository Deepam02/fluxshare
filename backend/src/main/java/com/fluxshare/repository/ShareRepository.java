package com.fluxshare.repository;

import com.fluxshare.entity.Share;
import com.fluxshare.enums.ShareType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Share entity operations
 */
@Repository
public interface ShareRepository extends JpaRepository<Share, UUID> {

    /**
     * Find share by public share ID
     */
    Optional<Share> findByShareId(String shareId);

    /**
     * Check if share ID exists
     */
    boolean existsByShareId(String shareId);

    /**
     * Find all expired shares
     */
    @Query("SELECT s FROM Share s WHERE s.expiryTime < :currentTime AND s.hasExpired = false")
    List<Share> findExpiredShares(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find shares by type
     */
    List<Share> findByType(ShareType type);

    /**
     * Mark share as expired
     */
    @Modifying
    @Query("UPDATE Share s SET s.hasExpired = true WHERE s.id = :id")
    void markAsExpired(@Param("id") UUID id);

    /**
     * Find shares expiring within specified hours
     */
    @Query("SELECT s FROM Share s WHERE s.expiryTime BETWEEN :start AND :end AND s.hasExpired = false")
    List<Share> findSharesExpiringBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * Count active shares
     */
    @Query("SELECT COUNT(s) FROM Share s WHERE s.hasExpired = false AND s.expiryTime > :currentTime")
    long countActiveShares(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Delete expired shares older than specified date
     */
    @Modifying
    @Query("DELETE FROM Share s WHERE s.hasExpired = true AND s.expiryTime < :olderThan")
    void deleteExpiredSharesOlderThan(@Param("olderThan") LocalDateTime olderThan);
}
