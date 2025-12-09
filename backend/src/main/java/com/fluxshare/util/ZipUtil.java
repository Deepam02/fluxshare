package com.fluxshare.util;

import com.fluxshare.exception.FileStorageException;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for ZIP file operations.
 * Implements Singleton pattern.
 */
@Component
public class ZipUtil {

    private static final int BUFFER_SIZE = 8192;

    /**
     * Create a ZIP file from multiple files
     * 
     * @param files List of files to include in the ZIP
     * @param zipFilePath Path where the ZIP file should be created
     * @throws FileStorageException if ZIP creation fails
     */
    public void createZip(List<File> files, Path zipFilePath) throws FileStorageException {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            
            for (File file : files) {
                addToZip(file, file.getName(), zos);
            }
            
        } catch (IOException e) {
            throw new FileStorageException("Failed to create ZIP file", e);
        }
    }

    /**
     * Create a ZIP file from multiple InputStreams with their names
     * 
     * @param fileData List of file data entries containing name and InputStream
     * @param zipOutputStream The output stream to write ZIP data to
     * @throws IOException if writing fails
     */
    public void createZipFromStreams(List<FileData> fileData, OutputStream zipOutputStream) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(zipOutputStream)) {
            
            for (FileData data : fileData) {
                ZipEntry zipEntry = new ZipEntry(data.getName());
                zos.putNextEntry(zipEntry);
                
                byte[] buffer = new byte[BUFFER_SIZE];
                int length;
                while ((length = data.getInputStream().read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                
                zos.closeEntry();
                data.getInputStream().close();
            }
            
            zos.finish();
        }
    }

    /**
     * Add a file to an existing ZIP output stream
     * 
     * @param file The file to add
     * @param fileName The name to use in the ZIP
     * @param zos The ZIP output stream
     * @throws IOException if adding file fails
     */
    private void addToZip(File file, String fileName, ZipOutputStream zos) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);
            
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            
            zos.closeEntry();
        }
    }

    /**
     * Get estimated ZIP size for given files
     * 
     * @param files List of files to estimate
     * @return Estimated ZIP size in bytes
     */
    public long estimateZipSize(List<File> files) {
        long totalSize = 0;
        for (File file : files) {
            totalSize += file.length();
        }
        // ZIP typically compresses to 60-90% of original size
        // Return conservative estimate of 80%
        return (long) (totalSize * 0.8);
    }

    /**
     * Data class for holding file information for ZIP creation
     */
    public static class FileData {
        private final String name;
        private final InputStream inputStream;

        public FileData(String name, InputStream inputStream) {
            this.name = name;
            this.inputStream = inputStream;
        }

        public String getName() {
            return name;
        }

        public InputStream getInputStream() {
            return inputStream;
        }
    }
}
