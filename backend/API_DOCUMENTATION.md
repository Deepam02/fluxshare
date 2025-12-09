# FluxShare API Documentation

## Base URL
```
http://localhost:8080/api/v1
```

## Endpoints

### File Share

#### Create File Share
**POST** `/share/file`

Upload one or multiple files to create a share.

**Request:**
- Content-Type: `multipart/form-data`
- Parameters:
  - `files[]` (required): Array of files to upload
  - `expiryHours` (optional): Hours until expiration (default: 24, max: 168)
  - `viewOnce` (optional): Delete after first view (default: false)
  - `password` (optional): Password protection
  - `notes` (optional): Additional notes
  - `maxDownloads` (optional): Maximum number of downloads
  - `maxViews` (optional): Maximum number of views

**Response:** `201 Created`
```json
{
  "shareId": "a9KpX7mN",
  "expiryTime": "2025-12-07T10:30:00",
  "viewOnce": false,
  "passwordProtected": true,
  "fileCount": 3,
  "type": "FILE"
}
```

#### Get File List
**GET** `/share/{shareId}/files?password=xxx`

Get list of files in a share.

**Response:** `200 OK`
```json
{
  "shareId": "a9KpX7mN",
  "files": [
    {
      "name": "document.pdf",
      "size": 1048576,
      "mimeType": "application/pdf",
      "previewable": true,
      "downloadUrl": "/api/v1/share/a9KpX7mN/files/document.pdf"
    }
  ],
  "totalFiles": 1,
  "totalSize": 1048576
}
```

#### Download Single File
**GET** `/share/{shareId}/files/{fileName}?password=xxx`

Download a specific file from the share.

**Response:** `200 OK`
- Content-Type: File's MIME type
- Content-Disposition: `attachment; filename="..."`
- Body: Binary file data (streamed)

#### Download All Files as ZIP
**GET** `/share/{shareId}/download/all?password=xxx`

Download all files as a ZIP archive.

**Response:** `200 OK`
- Content-Type: `application/zip`
- Content-Disposition: `attachment; filename="{shareId}.zip"`
- Body: ZIP file (streamed)

#### Preview File
**GET** `/share/{shareId}/files/{fileName}/preview?password=xxx&maxBytes=102400`

Get a preview of a file (first N bytes).

**Response:** `200 OK`
- Content-Type: File's MIME type
- Body: Preview data (limited bytes)

---

### Text Share

#### Create Text Share
**POST** `/share/text`

Create a text share.

**Request:**
```json
{
  "text": "Hello, this is a secure text share!",
  "expiryHours": 24,
  "viewOnce": true,
  "password": "secret123",
  "notes": "Important message",
  "maxViews": 5
}
```

**Response:** `201 Created`
```json
{
  "shareId": "b3Xy9pQm",
  "expiryTime": "2025-12-07T10:30:00",
  "viewOnce": true,
  "passwordProtected": true,
  "type": "TEXT"
}
```

#### Get Text Content
**GET** `/share/{shareId}/text?password=xxx`

Retrieve the text content.

**Response:** `200 OK`
```json
{
  "shareId": "b3Xy9pQm",
  "content": "Hello, this is a secure text share!",
  "type": "TEXT"
}
```

---

### Code Share

#### Create Code Share
**POST** `/share/code`

Create a code snippet share.

**Request:**
```json
{
  "code": "public static void main(String[] args) {\n    System.out.println(\"Hello\");\n}",
  "language": "java",
  "expiryHours": 48,
  "viewOnce": false,
  "password": "dev123",
  "notes": "Sample Java code"
}
```

**Response:** `201 Created`
```json
{
  "shareId": "c7Kp2Lmn",
  "expiryTime": "2025-12-08T10:30:00",
  "viewOnce": false,
  "passwordProtected": true,
  "type": "CODE"
}
```

#### Get Code Content
**GET** `/share/{shareId}/code?password=xxx`

Retrieve the code content.

**Response:** `200 OK`
```json
{
  "shareId": "c7Kp2Lmn",
  "code": "public static void main(String[] args) {...}",
  "language": "java",
  "type": "CODE"
}
```

---

### Share Management

#### Get Share Metadata
**GET** `/share/{shareId}/metadata`

Get detailed metadata about a share.

**Response:** `200 OK`
```json
{
  "shareId": "a9KpX7mN",
  "type": "FILE",
  "expiryTime": "2025-12-07T10:30:00",
  "timeRemaining": "23 hours 45 minutes",
  "viewOnce": false,
  "passwordProtected": true,
  "createdAt": "2025-12-06T10:30:00",
  "notes": "Important files",
  "viewCount": 5,
  "downloadCount": 2,
  "maxDownloads": 10,
  "maxViews": null,
  "files": [
    {
      "name": "document.pdf",
      "size": 1048576,
      "mimeType": "application/pdf",
      "previewable": true
    }
  ]
}
```

#### Validate Password
**POST** `/share/{shareId}/validate`

Check if a password is correct.

**Request:**
```json
{
  "password": "secret123"
}
```

**Response:** `200 OK`
```json
{
  "valid": true,
  "message": "Password is valid"
}
```

#### Delete Share
**DELETE** `/share/{shareId}?password=xxx`

Manually delete a share.

**Response:** `204 No Content`

#### Get Link Preview
**GET** `/share/{shareId}/preview`

Get a preview for the share (WhatsApp-style).

**Response:** `200 OK`
```json
{
  "title": "FluxShare - Secure File Share",
  "description": "3 files",
  "type": "FILE",
  "expiresIn": "23 hours 45 minutes",
  "fileCount": 3,
  "requiresPassword": true
}
```

---

## Error Responses

### 404 Not Found
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Share not found: xyz123",
  "path": "/api/v1/share/xyz123/metadata",
  "timestamp": "2025-12-06T10:30:00"
}
```

### 410 Gone
```json
{
  "status": 410,
  "error": "Gone",
  "message": "Share has expired: abc789",
  "path": "/api/v1/share/abc789/text",
  "timestamp": "2025-12-06T10:30:00"
}
```

### 401 Unauthorized
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid password",
  "path": "/api/v1/share/def456/files",
  "timestamp": "2025-12-06T10:30:00"
}
```

### 429 Too Many Requests
```json
{
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Please try again later.",
  "path": "/api/v1/share/ghi789/download/all",
  "timestamp": "2025-12-06T10:30:00"
}
```

### 413 Payload Too Large
```json
{
  "status": 413,
  "error": "Payload Too Large",
  "message": "File size exceeds maximum allowed limit",
  "path": "/api/v1/share/file",
  "timestamp": "2025-12-06T10:30:00"
}
```

---

## Features

### Security
- **AES-GCM Encryption**: All files and content are encrypted at rest
- **Password Protection**: Optional password for accessing shares
- **Key Wrapping**: Content keys are encrypted with a master key
- **Rate Limiting**: Prevents abuse with configurable limits

### Expiry Management
- **Time-based Expiry**: Shares expire after configured hours
- **View-once Mode**: Content deleted after first access
- **Download Limits**: Maximum number of downloads
- **View Limits**: Maximum number of views
- **Automatic Cleanup**: Scheduled cleanup of expired shares

### File Handling
- **Streaming Downloads**: Efficient memory usage for large files
- **Multi-file Upload**: Upload multiple files at once
- **ZIP Generation**: Download multiple files as ZIP
- **Preview Support**: Preview images, PDFs, and text files
- **MIME Detection**: Automatic file type detection

### Access Control
- **Password Validation**: Secure password checking
- **Access Logging**: Track all access attempts
- **IP-based Rate Limiting**: Per-IP request throttling
- **Link Preview**: Get metadata without downloading

---

## Configuration

Key configuration properties in `application.yml`:

```yaml
fluxshare:
  storage:
    base-path: ./storage/encrypted
    temp-path: ./storage/temp
  encryption:
    master-key: ${FLUXSHARE_MASTER_KEY}
  share:
    default-expiry-hours: 24
    max-expiry-hours: 168
  rate-limit:
    enabled: true
    requests-per-minute: 10
  cleanup:
    cron: "0 */15 * * * *"
    enabled: true
  access-log:
    enabled: true
```

---

## Database Schema

### Tables
- `share`: Main share metadata
- `file_metadata`: File information
- `text_content`: Text/code content
- `access_log`: Access tracking

See entity classes for detailed schema.
