package com.fluxshare.enums;

/**
 * Enum representing different actions that can be logged
 */
public enum AccessAction {
    VIEW("View"),
    DOWNLOAD("Download"),
    PREVIEW("Preview"),
    VALIDATE_PASSWORD("Validate Password"),
    METADATA_ACCESS("Metadata Access"),
    ZIP_DOWNLOAD("ZIP Download");

    private final String displayName;

    AccessAction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
