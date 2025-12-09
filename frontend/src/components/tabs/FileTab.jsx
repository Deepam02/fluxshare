import React, { useCallback } from 'react';
import { useDropzone } from 'react-dropzone';
import {
  Box,
  Typography,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
  Paper,
} from '@mui/material';
import {
  CloudUpload,
  InsertDriveFile,
  Delete,
} from '@mui/icons-material';
import { formatFileSize } from '../../utils/helpers.jsx';
import { MAX_FILE_SIZE, MAX_FILES } from '../../utils/constants.jsx';
import { toast } from 'react-toastify';

const FileTab = ({ files, setFiles }) => {
  const onDrop = useCallback((acceptedFiles, rejectedFiles) => {
    // Handle rejected files
    rejectedFiles.forEach((file) => {
      file.errors.forEach((err) => {
        if (err.code === 'file-too-large') {
          toast.error(`File ${file.file.name} is too large. Maximum size is ${formatFileSize(MAX_FILE_SIZE)}`);
        } else if (err.code === 'too-many-files') {
          toast.error(`Too many files. Maximum is ${MAX_FILES} files`);
        } else {
          toast.error(`Error with file ${file.file.name}: ${err.message}`);
        }
      });
    });

    // Handle accepted files
    if (acceptedFiles.length > 0) {
      setFiles((prevFiles) => {
        const newFiles = [...prevFiles, ...acceptedFiles];
        if (newFiles.length > MAX_FILES) {
          toast.error(`Maximum ${MAX_FILES} files allowed`);
          return prevFiles;
        }
        return newFiles;
      });
    }
  }, [setFiles]);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    maxSize: MAX_FILE_SIZE,
    maxFiles: MAX_FILES,
  });

  const removeFile = (index) => {
    setFiles((prevFiles) => prevFiles.filter((_, i) => i !== index));
  };

  return (
    <Box>
      <Paper
        {...getRootProps()}
        sx={{
          p: 4,
          textAlign: 'center',
          cursor: 'pointer',
          backgroundColor: isDragActive ? 'action.hover' : 'background.default',
          border: '2px dashed',
          borderColor: isDragActive ? 'primary.main' : 'divider',
          transition: 'all 0.2s',
          '&:hover': {
            borderColor: 'primary.main',
            backgroundColor: 'action.hover',
          },
        }}
      >
        <input {...getInputProps()} />
        <CloudUpload sx={{ fontSize: 48, color: 'primary.main', mb: 2 }} />
        <Typography variant="h6" gutterBottom>
          {isDragActive ? 'Drop files here' : 'Drag & drop files here'}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          or click to browse
        </Typography>
        <Typography variant="caption" display="block" sx={{ mt: 1 }} color="text.secondary">
          Maximum {MAX_FILES} files, {formatFileSize(MAX_FILE_SIZE)} per file
        </Typography>
      </Paper>

      {files.length > 0 && (
        <List sx={{ mt: 2 }}>
          {files.map((file, index) => (
            <ListItem
              key={index}
              sx={{
                border: '1px solid',
                borderColor: 'divider',
                borderRadius: 1,
                mb: 1,
              }}
            >
              <ListItemIcon>
                <InsertDriveFile color="primary" />
              </ListItemIcon>
              <ListItemText
                primary={file.name}
                secondary={formatFileSize(file.size)}
              />
              <ListItemSecondaryAction>
                <IconButton edge="end" onClick={() => removeFile(index)}>
                  <Delete />
                </IconButton>
              </ListItemSecondaryAction>
            </ListItem>
          ))}
        </List>
      )}
    </Box>
  );
};

export default FileTab;
