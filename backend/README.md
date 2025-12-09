# FluxShare Backend

Secure ephemeral file and content sharing system built with Spring Boot and PostgreSQL.

## Features

- Multi-file upload with encryption
- Text and code snippet sharing
- Password protection
- Expiry time management
- View-once functionality
- ZIP download for multiple files
- Rate limiting
- Access logging
- Automatic cleanup of expired shares
- Preview support for images, PDFs, and text

## Tech Stack

- Java 17
- Spring Boot 3.2.0
- PostgreSQL
- Maven
- AES-GCM Encryption
- Lombok

## Getting Started

### Prerequisites

- JDK 17 or higher
- PostgreSQL 14+
- Maven 3.8+

### Setup

1. Create PostgreSQL database:
```sql
CREATE DATABASE fluxshare;
```

2. Update `application.yml` with your database credentials

3. Set the master encryption key as environment variable:
```bash
export FLUXSHARE_MASTER_KEY=your-secure-32-byte-base64-key
```

4. Build the project:
```bash
mvn clean install
```

5. Run the application:
```bash
mvn spring-boot:run
```

## API Endpoints

### File Sharing
- `POST /api/v1/share/file` - Upload files
- `GET /api/v1/share/{shareId}/metadata` - Get share metadata
- `GET /api/v1/share/{shareId}/files` - List files
- `GET /api/v1/share/{shareId}/files/{fileName}` - Download single file
- `GET /api/v1/share/{shareId}/download/all` - Download as ZIP

### Text Sharing
- `POST /api/v1/share/text` - Create text share
- `GET /api/v1/share/{shareId}/text` - Get text content

### Code Sharing
- `POST /api/v1/share/code` - Create code share
- `GET /api/v1/share/{shareId}/code` - Get code content

### Access Management
- `POST /api/v1/share/{shareId}/validate` - Validate password
- `DELETE /api/v1/share/{shareId}` - Delete share

## Architecture

The project follows SOLID principles and implements common design patterns:

- **Strategy Pattern**: Encryption handling
- **Factory Pattern**: Share creation
- **Builder Pattern**: Response DTOs
- **Template Method**: File streaming workflows
- **Singleton**: Utility components

## License

MIT
