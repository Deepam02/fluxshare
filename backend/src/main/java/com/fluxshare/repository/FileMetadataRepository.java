package com.fluxshare.repository;

import com.fluxshare.entity.FileMetadata;
import com.fluxshare.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for FileMetadata entity operations
 */
@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {

    /**
     * Find all files for a share
     */
    List<FileMetadata> findByShare(Share share);

    /**
     * Find all files for a share ID
     */
    @Query("SELECT fm FROM FileMetadata fm WHERE fm.share.id = :shareId")
    List<FileMetadata> findByShareId(@Param("shareId") UUID shareId);

    /**
     * Find file by share and filename
     */
    Optional<FileMetadata> findByShareAndFilename(Share share, String filename);

    /**
     * Find file by share ID and filename
     */
    @Query("SELECT fm FROM FileMetadata fm WHERE fm.share.id = :shareId AND fm.filename = :filename")
    Optional<FileMetadata> findByShareIdAndFilename(
            @Param("shareId") UUID shareId,
            @Param("filename") String filename
    );

    /**
     * Count files for a share
     */
    long countByShare(Share share);

    /**
     * Get total size of all files for a share
     */
    @Query("SELECT SUM(fm.size) FROM FileMetadata fm WHERE fm.share.id = :shareId")
    Long getTotalSizeByShareId(@Param("shareId") UUID shareId);

    /**
     * Find previewable files for a share
     */
    @Query("SELECT fm FROM FileMetadata fm WHERE fm.share.id = :shareId AND fm.isPreviewable = true")
    List<FileMetadata> findPreviewableFilesByShareId(@Param("shareId") UUID shareId);

    /**
     * Delete all files for a share
     */
    void deleteByShare(Share share);
}
