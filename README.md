# FluxShare

**Secure, ephemeral file and content sharing system**

[![Live Demo](https://img.shields.io/badge/demo-live-brightgreen)](https://fluxshare-ivory.vercel.app/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

**🚀 Live Application:** [https://fluxshare-ivory.vercel.app/](https://fluxshare-ivory.vercel.app/)

---

## 📖 Overview

FluxShare is a modern, secure platform for sharing files, text, and code snippets with anyone. It provides enterprise-grade encryption, flexible expiry options, and a clean user interface—all without requiring user accounts or authentication.

### Key Features

- 🗂️ **Multi-File Sharing** - Upload and share multiple files with drag & drop support
- 📝 **Text Sharing** - Share plain text with word and character count
- 💻 **Code Sharing** - Share code snippets with syntax highlighting for 20+ languages
- 🔒 **End-to-End Encryption** - AES-GCM encryption for all content
- 🔐 **Password Protection** - Optional password authentication
- ⏰ **Auto-Expiry** - Set custom expiration times (1 hour to 7 days)
- 🔥 **View Once Mode** - Self-destruct after first access
- 📊 **Access Controls** - Limit maximum downloads and views
- 📱 **QR Code Generation** - Easy mobile sharing
- 🚫 **Rate Limiting** - Built-in abuse prevention
- 🎨 **Modern UI** - Responsive Material-UI interface
- 📦 **ZIP Downloads** - Package multiple files for easy download

---

## 🏗️ Architecture

FluxShare is built as a full-stack application with clear separation of concerns:

### Frontend
- **Framework:** React 18
- **UI Library:** Material-UI v5
- **Form Management:** React Hook Form
- **HTTP Client:** Axios
- **Syntax Highlighting:** PrismJS
- **File Upload:** React Dropzone
- **Notifications:** React Toastify

### Backend
- **Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Database:** PostgreSQL 14+
- **Build Tool:** Maven
- **Encryption:** AES-GCM with key wrapping
- **Design Patterns:** Strategy, Factory, Builder, Template Method

---

## 🚀 Quick Start

### Prerequisites

- **Frontend:** Node.js 14+ and npm
- **Backend:** JDK 17+, PostgreSQL 14+, Maven 3.8+

### Frontend Setup

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Configure the API endpoint in `.env`:
```env
REACT_APP_API_BASE_URL=http://localhost:8080/api/v1
```

4. Start the development server:
```bash
npm start
```

The app will open at [http://localhost:3000](http://localhost:3000)

### Backend Setup

1. Navigate to the backend directory:
```bash
cd backend
```

2. Create PostgreSQL database:
```sql
CREATE DATABASE fluxshare;
```

3. Update `src/main/resources/application.yml` with your database credentials

4. Set the master encryption key as environment variable:
```bash
export FLUXSHARE_MASTER_KEY=your-secure-32-byte-base64-key
```

5. Build the project:
```bash
mvn clean install
```

6. Run the application:
```bash
mvn spring-boot:run
```

The API will be available at [http://localhost:8080](http://localhost:8080)

---

## 📚 Documentation

Detailed documentation is available in the respective directories:

- **[Backend README](backend/README.md)** - Backend setup, architecture, and features
- **[API Documentation](backend/API_DOCUMENTATION.md)** - Complete API reference with examples
- **[Frontend README](frontend/README.md)** - Frontend setup and project structure
- **[Design Document](frontend/designdoc.md)** - UI/UX design specifications

---

## 🎯 Usage

### Creating a Share

1. Visit [https://fluxshare-ivory.vercel.app/](https://fluxshare-ivory.vercel.app/)
2. Select your content type (File, Text, or Code)
3. Add your content
4. Configure options:
   - Set expiry time (1 hour to 7 days)
   - Add password protection (optional)
   - Enable view once mode (optional)
   - Set download/view limits (optional)
   - Add notes (optional)
5. Click "Create Share"
6. Copy the generated link or scan the QR code

### Accessing a Share

1. Open the share link
2. Enter password if required
3. View or download the content
4. The share will automatically expire based on configured settings

---

## 🔐 Security Features

FluxShare implements multiple layers of security:

- **AES-GCM Encryption** - All files and content are encrypted at rest
- **Key Wrapping** - Content encryption keys are wrapped with a master key
- **Password Protection** - Bcrypt-based password hashing for protected shares
- **Rate Limiting** - IP-based throttling to prevent abuse
- **Automatic Cleanup** - Scheduled deletion of expired shares
- **Access Logging** - Track all access attempts with IP and user agent
- **Secure Streaming** - Efficient file streaming without full loading into memory

---

## 🛠️ API Endpoints

### File Sharing
- `POST /api/v1/share/file` - Upload files
- `GET /api/v1/share/{shareId}/files` - List files
- `GET /api/v1/share/{shareId}/files/{fileName}` - Download single file
- `GET /api/v1/share/{shareId}/download/all` - Download as ZIP

### Text Sharing
- `POST /api/v1/share/text` - Create text share
- `GET /api/v1/share/{shareId}/text` - Get text content

### Code Sharing
- `POST /api/v1/share/code` - Create code share
- `GET /api/v1/share/{shareId}/code` - Get code content

### Share Management
- `GET /api/v1/share/{shareId}/metadata` - Get share metadata
- `POST /api/v1/share/{shareId}/validate` - Validate password
- `DELETE /api/v1/share/{shareId}` - Delete share
- `GET /api/v1/share/{shareId}/preview` - Get link preview

For complete API documentation, see [API_DOCUMENTATION.md](backend/API_DOCUMENTATION.md)

---

## 📁 Project Structure

```
fluxshare/
├── frontend/                 # React frontend application
│   ├── src/
│   │   ├── api/             # API client
│   │   ├── components/      # React components
│   │   │   ├── tabs/        # File, Text, Code tabs
│   │   │   └── previews/    # File preview components
│   │   └── utils/           # Helper functions
│   ├── package.json
│   └── README.md
│
├── backend/                 # Spring Boot backend
│   ├── src/main/java/com/fluxshare/
│   │   ├── config/         # Configuration classes
│   │   ├── controller/     # REST controllers
│   │   ├── dto/            # Data transfer objects
│   │   ├── entity/         # JPA entities
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access
│   │   ├── scheduler/      # Scheduled tasks
│   │   └── util/           # Utilities
│   ├── pom.xml
│   ├── README.md
│   └── API_DOCUMENTATION.md
│
└── README.md               # This file
```

---

## 🎨 Design Philosophy

FluxShare follows a **single-page application** approach with:

- **No route changes** - Everything happens on one page
- **Tab-based interface** - Switch between File, Text, and Code sharing
- **Instant feedback** - Real-time validation and notifications
- **Mobile-first design** - Fully responsive across all devices
- **Minimal UI** - Clean, distraction-free interface

---

## 🔧 Configuration

### Backend Configuration

Key properties in `application.yml`:

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
```

---

## 🐳 Deployment

### Frontend (Vercel)

The frontend is deployed on Vercel with automatic deployments from the main branch.

**Live URL:** [https://fluxshare-ivory.vercel.app/](https://fluxshare-ivory.vercel.app/)

### Backend (Docker)

A `Dockerfile` and `docker-compose.yml` are provided for easy containerization:

```bash
docker-compose up -d
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

## 👤 Author

**Deepam02**

- GitHub: [@Deepam02](https://github.com/Deepam02)

---

## 🙏 Acknowledgments

- Material-UI for the component library
- Spring Boot for the robust backend framework
- PrismJS for syntax highlighting
- The open-source community for inspiration and tools

---

**Built with ❤️ for secure, hassle-free sharing**
