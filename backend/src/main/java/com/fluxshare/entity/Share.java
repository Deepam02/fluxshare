package com.fluxshare.entity;

import com.fluxshare.enums.ShareType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a share link.
 * Contains metadata for file, text, or code shares.
 */
@Entity
@Table(name = "share", indexes = {
    @Index(name = "idx_share_id", columnList = "share_id"),
    @Index(name = "idx_expiry_time", columnList = "expiry_time"),
    @Index(name = "idx_has_expired", columnList = "has_expired")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Share {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "share_id", unique = true, nullable = false, length = 20)
    private String shareId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ShareType type;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    @Column(name = "view_once", nullable = false)
    @Builder.Default
    private Boolean viewOnce = false;

    @Column(name = "has_expired", nullable = false)
    @Builder.Default
    private Boolean hasExpired = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "encrypted_key", columnDefinition = "bytea")
    private byte[] encryptedKey;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "download_count", nullable = false)
    @Builder.Default
    private Integer downloadCount = 0;

    @Column(name = "max_downloads")
    private Integer maxDownloads;

    @Column(name = "max_views")
    private Integer maxViews;

    @OneToMany(mappedBy = "share", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<FileMetadata> files = new ArrayList<>();

    @OneToOne(mappedBy = "share", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private TextContent textContent;

    @OneToMany(mappedBy = "share", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AccessLog> accessLogs = new ArrayList<>();

    /**
     * Check if share is expired based on time or download/view limits
     */
    public boolean isExpired() {
        if (hasExpired) {
            return true;
        }
        
        if (LocalDateTime.now().isAfter(expiryTime)) {
            return true;
        }
        
        if (maxDownloads != null && downloadCount >= maxDownloads) {
            return true;
        }
        
        if (maxViews != null && viewCount >= maxViews) {
            return true;
        }
        
        return false;
    }

    /**
     * Check if share requires password
     */
    public boolean isPasswordProtected() {
        return passwordHash != null && !passwordHash.isEmpty();
    }

    /**
     * Increment view count
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * Increment download count
     */
    public void incrementDownloadCount() {
        this.downloadCount++;
    }
}
