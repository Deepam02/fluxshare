package com.fluxshare.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing encrypted text or code content.
 * Used for text and code shares.
 */
@Entity
@Table(name = "text_content", indexes = {
    @Index(name = "idx_text_share_id", columnList = "share_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextContent {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_id", nullable = false, unique = true)
    private Share share;

    @Column(name = "content_encrypted", columnDefinition = "bytea", nullable = false)
    private byte[] contentEncrypted;

    @Column(name = "language", length = 50)
    private String language;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "content_length", nullable = false)
    private Integer contentLength;

    @Column(name = "is_code", nullable = false)
    @Builder.Default
    private Boolean isCode = false;
}
