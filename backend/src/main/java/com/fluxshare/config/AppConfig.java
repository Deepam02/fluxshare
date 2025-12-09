package com.fluxshare.config;

import com.fluxshare.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * Application startup configuration
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class AppConfig {

    private final FileStorageService fileStorageService;

    /**
     * Initialize application on startup
     */
    @PostConstruct
    public void init() {
        log.info("Initializing FluxShare application...");
        
        // Initialize storage directories
        fileStorageService.initializeStorage();
        
        log.info("FluxShare application initialized successfully");
    }
}
