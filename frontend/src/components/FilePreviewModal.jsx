import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  IconButton,
  CircularProgress,
  Typography,
  Box,
} from '@mui/material';
import { Close, Download } from '@mui/icons-material';
import { toast } from 'react-toastify';
import ImagePreview from './previews/ImagePreview.jsx';
import PDFPreview from './previews/PDFPreview.jsx';
import TextPreview from './previews/TextPreview.jsx';

const FilePreviewModal = ({ open, onClose, file, shareId, password, onDownload }) => {
  const [loading, setLoading] = useState(true);
  const [previewData, setPreviewData] = useState(null);
  const [previewType, setPreviewType] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (open && file) {
      loadPreview();
    }
    return () => {
      // Cleanup blob URLs
      if (previewData && typeof previewData === 'string' && previewData.startsWith('blob:')) {
        URL.revokeObjectURL(previewData);
      }
    };
  }, [open, file]);

  const detectPreviewType = () => {
    const mimeType = file.mimeType;
    const extension = file.name.split('.').pop().toLowerCase();

    if (mimeType.startsWith('image/')) return 'IMAGE';
    if (mimeType === 'application/pdf') return 'PDF';
    if (mimeType.startsWith('text/')) return 'TEXT';

    // Check by extension for code files
    const codeExts = ['js', 'jsx', 'ts', 'tsx', 'py', 'java', 'cpp', 'c', 'cs', 'go', 'rs', 'php', 'rb', 'swift', 'kt', 'sql', 'sh', 'bash', 'json', 'xml', 'yaml', 'yml', 'md', 'html', 'css'];
    if (codeExts.includes(extension)) return 'TEXT';

    return null;
  };

  const loadPreview = async () => {
    setLoading(true);
    setError(null);

    const type = detectPreviewType();
    setPreviewType(type);

    if (!type) {
      setError('Preview not available for this file type');
      setLoading(false);
      return;
    }

    try {
      const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';
      const params = new URLSearchParams();
      if (password) params.append('password', password);
      params.append('maxBytes', '10485760'); // 10MB limit for preview

      const url = `${API_BASE_URL}/share/${shareId}/files/${encodeURIComponent(file.name)}/preview?${params}`;
      
      const response = await fetch(url);
      
      if (!response.ok) {
        throw new Error('Failed to load preview');
      }

      if (type === 'TEXT') {
        const text = await response.text();
        setPreviewData(text);
      } else {
        const blob = await response.blob();
        const blobUrl = URL.createObjectURL(blob);
        setPreviewData(blobUrl);
      }
    } catch (err) {
      console.error('Preview error:', err);
      setError('Failed to load preview');
      toast.error('Failed to load preview');
    } finally {
      setLoading(false);
    }
  };

  const renderPreview = () => {
    if (loading) {
      return (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 300 }}>
          <CircularProgress />
        </Box>
      );
    }

    if (error) {
      return (
        <Box sx={{ textAlign: 'center', py: 5 }}>
          <Typography color="error">{error}</Typography>
        </Box>
      );
    }

    switch (previewType) {
      case 'IMAGE':
        return <ImagePreview src={previewData} fileName={file.name} />;
      case 'PDF':
        return <PDFPreview src={previewData} fileName={file.name} />;
      case 'TEXT':
        return <TextPreview content={previewData} fileName={file.name} />;
      default:
        return (
          <Box sx={{ textAlign: 'center', py: 5 }}>
            <Typography>Preview not available</Typography>
          </Box>
        );
    }
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="lg"
      fullWidth
      PaperProps={{
        sx: {
          minHeight: '80vh',
          maxHeight: '90vh',
        },
      }}
    >
      <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h6" component="div" noWrap sx={{ flex: 1, mr: 2 }}>
          {file?.name}
        </Typography>
        <IconButton onClick={onClose} size="small">
          <Close />
        </IconButton>
      </DialogTitle>
      
      <DialogContent dividers>
        {renderPreview()}
      </DialogContent>
      
      <DialogActions>
        <Typography variant="caption" color="text.secondary" sx={{ flex: 1, ml: 1 }}>
          {file?.size && `Size: ${(file.size / 1024).toFixed(2)} KB`}
        </Typography>
        <Button onClick={onClose}>Close</Button>
        <Button
          variant="contained"
          startIcon={<Download />}
          onClick={() => {
            onDownload(file.name);
            onClose();
          }}
        >
          Download
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default FilePreviewModal;
