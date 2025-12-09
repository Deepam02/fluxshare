package com.fluxshare.repository;

import com.fluxshare.entity.AccessLog;
import com.fluxshare.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for AccessLog entity operations
 */
@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, UUID> {

    /**
     * Find all access logs for a share
     */
    List<AccessLog> findByShareOrderByAccessTimestampDesc(Share share);

    /**
     * Find all access logs for a share ID
     */
    @Query("SELECT al FROM AccessLog al WHERE al.share.id = :shareId ORDER BY al.accessTimestamp DESC")
    List<AccessLog> findByShareIdOrderByAccessTimestampDesc(@Param("shareId") UUID shareId);

    /**
     * Find access logs by action
     */
    List<AccessLog> findByAction(String action);

    /**
     * Count access logs for a share
     */
    long countByShare(Share share);

    /**
     * Find access logs within time range
     */
    @Query("SELECT al FROM AccessLog al WHERE al.accessTimestamp BETWEEN :start AND :end")
    List<AccessLog> findAccessLogsBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * Find recent access logs for a share
     */
    @Query("SELECT al FROM AccessLog al WHERE al.share.id = :shareId AND al.accessTimestamp > :since ORDER BY al.accessTimestamp DESC")
    List<AccessLog> findRecentAccessLogs(
            @Param("shareId") UUID shareId,
            @Param("since") LocalDateTime since
    );

    /**
     * Count access logs by IP address for a share within time window
     */
    @Query("SELECT COUNT(al) FROM AccessLog al WHERE al.share.id = :shareId AND al.ipAddress = :ipAddress AND al.accessTimestamp > :since")
    long countByShareIdAndIpAddressSince(
            @Param("shareId") UUID shareId,
            @Param("ipAddress") String ipAddress,
            @Param("since") LocalDateTime since
    );

    /**
     * Delete access logs for a share
     */
    void deleteByShare(Share share);

    /**
     * Delete old access logs
     */
    @Query("DELETE FROM AccessLog al WHERE al.accessTimestamp < :olderThan")
    void deleteAccessLogsOlderThan(@Param("olderThan") LocalDateTime olderThan);
}
