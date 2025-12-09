package com.fluxshare.repository;

import com.fluxshare.entity.Share;
import com.fluxshare.entity.TextContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for TextContent entity operations
 */
@Repository
public interface TextContentRepository extends JpaRepository<TextContent, UUID> {

    /**
     * Find text content by share
     */
    Optional<TextContent> findByShare(Share share);

    /**
     * Find text content by share ID
     */
    @Query("SELECT tc FROM TextContent tc WHERE tc.share.id = :shareId")
    Optional<TextContent> findByShareId(@Param("shareId") UUID shareId);

    /**
     * Check if text content exists for share
     */
    boolean existsByShare(Share share);

    /**
     * Delete text content for a share
     */
    void deleteByShare(Share share);

    /**
     * Find code content by language
     */
    @Query("SELECT tc FROM TextContent tc WHERE tc.isCode = true AND tc.language = :language")
    Optional<TextContent> findCodeByLanguage(@Param("language") String language);
}
