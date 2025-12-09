package com.fluxshare.util;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for MIME type detection and validation.
 * Implements Singleton pattern.
 */
@Component
public class MimeTypeUtil {

    private final Tika tika = new Tika();

    /**
     * Detect MIME type from file
     * 
     * @param file The file to analyze
     * @return The detected MIME type
     */
    public String detectMimeType(Path file) {
        try {
            return tika.detect(file);
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }

    /**
     * Detect MIME type from InputStream
     * 
     * @param inputStream The input stream to analyze
     * @param filename The filename (for extension-based detection)
     * @return The detected MIME type
     */
    public String detectMimeType(InputStream inputStream, String filename) {
        try {
            return tika.detect(inputStream, filename);
        } catch (IOException e) {
            return detectFromFilename(filename);
        }
    }

    /**
     * Detect MIME type from filename extension
     * 
     * @param filename The filename
     * @return The detected MIME type
     */
    public String detectFromFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "application/octet-stream";
        }

        String extension = getFileExtension(filename).toLowerCase();
        
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "pdf" -> "application/pdf";
            case "txt" -> "text/plain";
            case "html", "htm" -> "text/html";
            case "css" -> "text/css";
            case "js" -> "application/javascript";
            case "json" -> "application/json";
            case "xml" -> "application/xml";
            case "zip" -> "application/zip";
            case "tar" -> "application/x-tar";
            case "gz" -> "application/gzip";
            case "mp4" -> "video/mp4";
            case "mp3" -> "audio/mpeg";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default -> "application/octet-stream";
        };
    }

    /**
     * Check if MIME type is an image
     * 
     * @param mimeType The MIME type to check
     * @return true if it's an image type
     */
    public boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    /**
     * Check if MIME type is text
     * 
     * @param mimeType The MIME type to check
     * @return true if it's a text type
     */
    public boolean isText(String mimeType) {
        return mimeType != null && (
            mimeType.startsWith("text/") ||
            mimeType.equals("application/json") ||
            mimeType.equals("application/xml") ||
            mimeType.equals("application/javascript")
        );
    }

    /**
     * Check if MIME type is video
     * 
     * @param mimeType The MIME type to check
     * @return true if it's a video type
     */
    public boolean isVideo(String mimeType) {
        return mimeType != null && mimeType.startsWith("video/");
    }

    /**
     * Check if MIME type is audio
     * 
     * @param mimeType The MIME type to check
     * @return true if it's an audio type
     */
    public boolean isAudio(String mimeType) {
        return mimeType != null && mimeType.startsWith("audio/");
    }

    /**
     * Check if MIME type is PDF
     * 
     * @param mimeType The MIME type to check
     * @return true if it's PDF
     */
    public boolean isPdf(String mimeType) {
        return "application/pdf".equals(mimeType);
    }

    /**
     * Check if file type is previewable
     * 
     * @param mimeType The MIME type to check
     * @return true if file can be previewed
     */
    public boolean isPreviewable(String mimeType) {
        return isImage(mimeType) || isText(mimeType) || isPdf(mimeType);
    }

    /**
     * Get file extension from filename
     * 
     * @param filename The filename
     * @return The file extension without the dot
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
