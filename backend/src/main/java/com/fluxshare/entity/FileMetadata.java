package com.fluxshare.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing file metadata in a share.
 * Stores information about individual files within a file share.
 */
@Entity
@Table(name = "file_metadata", indexes = {
    @Index(name = "idx_file_share_id", columnList = "share_id"),
    @Index(name = "idx_filename", columnList = "filename")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMetadata {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_id", nullable = false)
    private Share share;

    @Column(name = "filename", nullable = false, length = 500)
    private String filename;

    @Column(name = "stored_path", nullable = false, length = 1000)
    private String storedPath;

    @Column(name = "mime_type", length = 255)
    private String mimeType;

    @Column(name = "size", nullable = false)
    private Long size;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "original_checksum", length = 64)
    private String originalChecksum;

    @Column(name = "is_previewable", nullable = false)
    @Builder.Default
    private Boolean isPreviewable = false;

    /**
     * Check if file can be previewed based on MIME type
     */
    public boolean canPreview() {
        if (mimeType == null) {
            return false;
        }
        
        return isPreviewable && (
            mimeType.startsWith("image/") ||
            mimeType.startsWith("text/") ||
            mimeType.equals("application/pdf") ||
            mimeType.equals("application/json") ||
            mimeType.equals("application/xml")
        );
    }
}
