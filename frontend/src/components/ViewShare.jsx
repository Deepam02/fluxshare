import React, { useState, useEffect, useRef } from 'react';
import { useParams } from 'react-router-dom';
import {
  Container,
  Card,
  CardContent,
  Box,
  Typography,
  TextField,
  Button,
  CircularProgress,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  IconButton,
  Chip,
  Paper,
} from '@mui/material';
import {
  InsertDriveFile,
  Download,
  Lock,
  Visibility,
  ContentCopy,
} from '@mui/icons-material';
import { toast } from 'react-toastify';
import axios from 'axios';
import Prism from 'prismjs';
import 'prismjs/themes/prism-tomorrow.css';
import FilePreviewModal from './FilePreviewModal.jsx';

// Import language components for syntax highlighting
// Note: Some languages have dependencies that must be loaded first
import 'prismjs/components/prism-markup'; // Required for HTML and PHP
import 'prismjs/components/prism-css';
import 'prismjs/components/prism-clike'; // Required for C-like languages
import 'prismjs/components/prism-javascript';
import 'prismjs/components/prism-typescript';
import 'prismjs/components/prism-python';
import 'prismjs/components/prism-java';
import 'prismjs/components/prism-c';
import 'prismjs/components/prism-cpp';
import 'prismjs/components/prism-csharp';
import 'prismjs/components/prism-go';
import 'prismjs/components/prism-rust';
import 'prismjs/components/prism-markup-templating'; // Required for PHP
import 'prismjs/components/prism-php';
import 'prismjs/components/prism-ruby';
import 'prismjs/components/prism-swift';
import 'prismjs/components/prism-kotlin';
import 'prismjs/components/prism-sql';
import 'prismjs/components/prism-json';
import 'prismjs/components/prism-yaml';
import 'prismjs/components/prism-markdown';
import 'prismjs/components/prism-bash';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

const ViewShare = () => {
  const { shareId } = useParams();
  const codeRef = useRef(null);
  const [loading, setLoading] = useState(true);
  const [metadata, setMetadata] = useState(null);
  const [content, setContent] = useState(null);
  const [password, setPassword] = useState('');
  const [passwordRequired, setPasswordRequired] = useState(false);
  const [error, setError] = useState(null);
  const [previewOpen, setPreviewOpen] = useState(false);
  const [selectedFile, setSelectedFile] = useState(null);

  useEffect(() => {
    fetchMetadata();
  }, [shareId]);

  useEffect(() => {
    if (content && content.type === 'CODE' && content.code && codeRef.current) {
      // Use setTimeout to ensure DOM is updated before highlighting
      setTimeout(() => {
        try {
          Prism.highlightElement(codeRef.current);
        } catch (error) {
          console.warn('Prism highlighting failed:', error);
        }
      }, 10);
    }
  }, [content]);

  const fetchMetadata = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/share/${shareId}/metadata`);
      const metadataResponse = response.data;
      setMetadata(metadataResponse);
      
      if (metadataResponse.passwordProtected) {
        setPasswordRequired(true);
        setLoading(false);
      } else {
        // Pass metadata directly since state hasn't updated yet
        fetchContent('', metadataResponse);
      }
    } catch (err) {
      if (err.response?.status === 404) {
        setError('Share not found');
      } else if (err.response?.status === 410) {
        setError('Share has expired');
      } else {
        setError('Failed to load share');
      }
      setLoading(false);
    }
  };

  const fetchContent = async (pwd = '', metadataParam = null) => {
    setLoading(true);
    try {
      const params = pwd ? { password: pwd } : {};
      const shareMetadata = metadataParam || metadata;
      
      if (!shareMetadata) {
        console.error('No metadata available');
        toast.error('Failed to load content');
        setLoading(false);
        return;
      }
      
      if (shareMetadata.type === 'FILE') {
        const response = await axios.get(`${API_BASE_URL}/share/${shareId}/files`, { params });
        console.log('File response:', response.data);
        setContent(response.data);
      } else if (shareMetadata.type === 'TEXT') {
        const response = await axios.get(`${API_BASE_URL}/share/${shareId}/text`, { params });
        console.log('Text response:', response.data);
        setContent(response.data);
      } else if (shareMetadata.type === 'CODE') {
        const response = await axios.get(`${API_BASE_URL}/share/${shareId}/code`, { params });
        console.log('Code response:', response.data);
        setContent(response.data);
      }
      
      // Store the password for subsequent API calls (like downloads)
      if (pwd) {
        setPassword(pwd);
      }
      
      setPasswordRequired(false);
    } catch (err) {
      if (err.response?.status === 401) {
        toast.error('Invalid password');
      } else if (err.response?.status === 410) {
        setError('Share has expired or been deleted');
      } else {
        toast.error('Failed to load content');
      }
    } finally {
      setLoading(false);
    }
  };

  const handlePasswordSubmit = (e) => {
    e.preventDefault();
    fetchContent(password);
  };

  const handleDownload = async (fileName) => {
    try {
      const params = password ? { password } : {};
      const response = await axios.get(
        `${API_BASE_URL}/share/${shareId}/files/${fileName}`,
        { 
          params,
          responseType: 'blob',
        }
      );
      
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      toast.error('Failed to download file');
    }
  };

  const handleDownloadAll = async () => {
    try {
      const params = password ? { password } : {};
      const response = await axios.get(
        `${API_BASE_URL}/share/${shareId}/download/all`,
        { 
          params,
          responseType: 'blob',
        }
      );
      
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${shareId}.zip`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      toast.error('Failed to download files');
    }
  };

  const handleCopyContent = () => {
    const textToCopy = content?.content || content?.code || '';
    navigator.clipboard.writeText(textToCopy).then(() => {
      toast.success('Copied to clipboard!');
    }).catch(() => {
      toast.error('Failed to copy');
    });
  };

  const isPreviewable = (file) => {
    // Check backend flag first
    if (file.previewable !== undefined) {
      return file.previewable;
    }

    // Fallback: detect by MIME type or extension
    const mimeType = file.mimeType || '';
    const extension = file.name.split('.').pop().toLowerCase();

    // Images
    if (mimeType.startsWith('image/')) return true;
    
    // PDFs
    if (mimeType === 'application/pdf') return true;
    
    // Text files
    if (mimeType.startsWith('text/')) return true;
    
    // JavaScript files
    if (mimeType === 'application/javascript' || mimeType === 'application/x-javascript') return true;
    
    // JSON
    if (mimeType === 'application/json') return true;
    
    // XML
    if (mimeType === 'application/xml' || mimeType === 'text/xml') return true;
    
    // Code files by extension
    const codeExts = ['js', 'jsx', 'ts', 'tsx', 'py', 'java', 'cpp', 'c', 'cs', 'go', 'rs', 'php', 'rb', 'swift', 'kt', 'sql', 'sh', 'bash', 'json', 'xml', 'yaml', 'yml', 'md', 'html', 'css', 'log', 'txt', 'csv'];
    if (codeExts.includes(extension)) return true;

    return false;
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
        <Container maxWidth="sm">
          <Card>
            <CardContent sx={{ textAlign: 'center', py: 5 }}>
              <Typography variant="h5" color="error" gutterBottom>
                {error}
              </Typography>
              <Button variant="contained" href="/" sx={{ mt: 2 }}>
                Create New Share
              </Button>
            </CardContent>
          </Card>
        </Container>
      </Box>
    );
  }

  if (passwordRequired) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', backgroundColor: '#f5f5f5' }}>
        <Container maxWidth="sm">
          <Card>
            <CardContent sx={{ textAlign: 'center', py: 5 }}>
              <Lock sx={{ fontSize: 60, color: 'primary.main', mb: 2 }} />
              <Typography variant="h5" gutterBottom>
                Password Required
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                This share is password protected
              </Typography>
              <form onSubmit={handlePasswordSubmit}>
                <TextField
                  fullWidth
                  type="password"
                  label="Enter Password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  sx={{ mb: 2 }}
                />
                <Button type="submit" variant="contained" fullWidth size="large">
                  Unlock
                </Button>
              </form>
            </CardContent>
          </Card>
        </Container>
      </Box>
    );
  }

  return (
    <Box sx={{ minHeight: '100vh', backgroundColor: '#f5f5f5', py: 4 }}>
      <Container maxWidth="md">
        <Card>
          <CardContent sx={{ p: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 3 }}>
              <Typography variant="h5" fontWeight="medium">
                Shared {metadata?.type}
              </Typography>
              <Box sx={{ display: 'flex', gap: 1 }}>
                {metadata?.viewOnce && (
                  <Chip label="View Once" color="error" size="small" />
                )}
                {metadata?.timeRemaining && (
                  <Chip label={`Expires: ${metadata.timeRemaining}`} color="warning" size="small" />
                )}
              </Box>
            </Box>

            {metadata?.notes && (
              <Paper sx={{ p: 2, mb: 3, backgroundColor: 'info.light' }}>
                <Typography variant="body2">{metadata.notes}</Typography>
              </Paper>
            )}

            {metadata?.type === 'FILE' && content && (
              <>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                  <Typography variant="subtitle1">
                    {content.totalFiles} file{content.totalFiles > 1 ? 's' : ''}
                  </Typography>
                  {content.totalFiles > 1 && (
                    <Button variant="contained" onClick={handleDownloadAll}>
                      Download All as ZIP
                    </Button>
                  )}
                </Box>
                <List>
                  {content.files?.map((file, index) => (
                    <ListItem
                      key={index}
                      sx={{ border: '1px solid', borderColor: 'divider', borderRadius: 1, mb: 1 }}
                      secondaryAction={
                        <Box sx={{ display: 'flex', gap: 1 }}>
                          {isPreviewable(file) && (
                            <IconButton
                              onClick={() => {
                                setSelectedFile(file);
                                setPreviewOpen(true);
                              }}
                              title="Preview"
                            >
                              <Visibility />
                            </IconButton>
                          )}
                          <IconButton edge="end" onClick={() => handleDownload(file.name)} title="Download">
                            <Download />
                          </IconButton>
                        </Box>
                      }
                    >
                      <ListItemIcon>
                        <InsertDriveFile color="primary" />
                      </ListItemIcon>
                      <ListItemText
                        primary={file.name}
                        secondary={`${(file.size / 1024).toFixed(2)} KB • ${file.mimeType}`}
                      />
                    </ListItem>
                  ))}
                </List>
              </>
            )}

            {metadata?.type === 'TEXT' && content && (
              <>
                <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 2 }}>
                  <Button 
                    variant="outlined" 
                    size="small"
                    startIcon={<ContentCopy />}
                    onClick={handleCopyContent}
                  >
                    Copy
                  </Button>
                </Box>
                <Paper sx={{ p: 3, backgroundColor: 'background.default' }}>
                  <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap', fontFamily: 'monospace' }}>
                    {content.content}
                  </Typography>
                </Paper>
              </>
            )}

            {metadata?.type === 'CODE' && content && (
              <>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                  <Chip label={content.language} variant="outlined" />
                  <Button 
                    variant="outlined" 
                    size="small"
                    startIcon={<ContentCopy />}
                    onClick={handleCopyContent}
                  >
                    Copy
                  </Button>
                </Box>
                <Paper sx={{ p: 2, backgroundColor: '#2d2d2d', overflow: 'auto' }}>
                  <pre style={{ margin: 0 }}>
                    <code ref={codeRef} className={`language-${content.language}`}>
                      {content.code}
                    </code>
                  </pre>
                </Paper>
              </>
            )}

            <Box sx={{ mt: 3, textAlign: 'center' }}>
              <Button variant="outlined" href="/">
                Create Your Own Share
              </Button>
            </Box>
          </CardContent>
        </Card>
      </Container>

      <FilePreviewModal
        open={previewOpen}
        onClose={() => setPreviewOpen(false)}
        file={selectedFile}
        shareId={shareId}
        password={password}
        onDownload={handleDownload}
      />
    </Box>
  );
};

export default ViewShare;
