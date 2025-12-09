# FluxShare UI

A modern, secure file, text, and code sharing application built with React.

## Features

- 🗂️ **File Sharing**: Upload and share multiple files with drag & drop support
- 📝 **Text Sharing**: Share plain text with word and character count
- 💻 **Code Sharing**: Share code snippets with syntax highlighting for 20+ languages
- 🔒 **Security**: Optional password protection for all shares
- ⏰ **Expiry Control**: Set custom expiration times (1 hour to 7 days)
- 🔥 **View Once**: Option to delete share after first view
- 📊 **Access Limits**: Set maximum downloads and views
- 📱 **QR Code**: Generate QR codes for easy mobile sharing
- 🎨 **Modern UI**: Clean, responsive interface built with Material-UI

## Tech Stack

- **React 18** - UI library
- **Material-UI v5** - Component library
- **React Hook Form** - Form management
- **Axios** - HTTP client
- **PrismJS** - Syntax highlighting
- **React Dropzone** - File upload
- **React Toastify** - Toast notifications
- **QRCode.react** - QR code generation

## Getting Started

### Prerequisites

- Node.js 14+ and npm

### Installation

1. Install dependencies:
```bash
npm install
```

2. Configure the API endpoint in `.env`:
```
REACT_APP_API_BASE_URL=http://localhost:8080/api/v1
```

3. Start the development server:
```bash
npm start
```

The app will open at [http://localhost:3000](http://localhost:3000)

### Build for Production

```bash
npm run build
```

## Project Structure

```
src/
├── api/
│   └── api.js              # API client and endpoints
├── components/
│   ├── tabs/
│   │   ├── FileTab.js      # File upload component
│   │   ├── TextTab.js      # Text input component
│   │   └── CodeTab.js      # Code editor component
│   ├── OptionsPanel.js     # Share options (expiry, password, etc.)
│   └── SuccessView.js      # Success view with link and QR code
├── utils/
│   ├── constants.js        # App constants
│   └── helpers.js          # Helper functions
├── App.js                  # Main application component
└── index.js                # App entry point
```

## Usage

### Creating a Share

1. Select a tab (File, Text, or Code)
2. Add your content
3. Configure options (optional):
   - Set expiry time
   - Add password protection
   - Enable view once
   - Set download/view limits
   - Add notes
4. Click "Create Share"
5. Copy the generated link or scan the QR code

### Share Options

- **Expiry Time**: Choose when the share expires (1 hour to 7 days)
- **Password**: Protect your share with a password
- **View Once**: Automatically delete after first access
- **Max Downloads**: Limit the number of downloads (files only)
- **Max Views**: Limit the number of views
- **Notes**: Add context or instructions

## API Integration

The app integrates with the FluxShare backend API:

- `POST /share/file` - Create file share
- `POST /share/text` - Create text share
- `POST /share/code` - Create code share

See `apidoc.md` for complete API documentation.

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## License

MIT
