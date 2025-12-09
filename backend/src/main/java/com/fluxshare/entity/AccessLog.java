package com.fluxshare.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for tracking access logs for each share.
 * Records when and how shares are accessed.
 */
@Entity
@Table(name = "access_log", indexes = {
    @Index(name = "idx_access_share_id", columnList = "share_id"),
    @Index(name = "idx_access_timestamp", columnList = "access_timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_id", nullable = false)
    private Share share;

    @CreationTimestamp
    @Column(name = "access_timestamp", nullable = false, updatable = false)
    private LocalDateTime accessTimestamp;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "action", nullable = false, length = 50)
    private String action; // VIEW, DOWNLOAD, PREVIEW, etc.

    @Column(name = "file_name", length = 500)
    private String fileName; // For file-specific downloads

    @Column(name = "success", nullable = false)
    @Builder.Default
    private Boolean success = true;

    @Column(name = "error_message", length = 500)
    private String errorMessage;
}
