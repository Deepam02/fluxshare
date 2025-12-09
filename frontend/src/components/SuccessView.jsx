import React from 'react';
import {
  Box,
  Typography,
  Button,
  Paper,
  TextField,
  InputAdornment,
  IconButton,
  Chip,
  Divider,
} from '@mui/material';
import { ContentCopy, Refresh } from '@mui/icons-material';
import { QRCodeSVG } from 'qrcode.react';
import { getShareUrl, formatTimeRemaining, copyToClipboard } from '../utils/helpers.jsx';
import { toast } from 'react-toastify';

const SuccessView = ({ shareData, onCreateAnother }) => {
  const shareUrl = getShareUrl(shareData.shareId);

  const handleCopy = async () => {
    const success = await copyToClipboard(shareUrl);
    if (success) {
      toast.success('Link copied to clipboard!');
    } else {
      toast.error('Failed to copy link');
    }
  };

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        py: 3,
      }}
    >
      <Typography variant="h5" color="success.main" gutterBottom fontWeight="medium">
        ✓ Share Created Successfully!
      </Typography>

      <Paper
        elevation={0}
        sx={{
          width: '100%',
          p: 3,
          mt: 3,
          backgroundColor: 'background.default',
          border: '1px solid',
          borderColor: 'divider',
        }}
      >
        <Typography variant="subtitle2" color="text.secondary" gutterBottom>
          Share URL
        </Typography>
        <TextField
          fullWidth
          value={shareUrl}
          InputProps={{
            readOnly: true,
            endAdornment: (
              <InputAdornment position="end">
                <IconButton onClick={handleCopy} edge="end">
                  <ContentCopy />
                </IconButton>
              </InputAdornment>
            ),
          }}
          sx={{ mb: 2 }}
        />

        <Box sx={{ display: 'flex', justifyContent: 'center', my: 3 }}>
          <Paper
            elevation={3}
            sx={{
              p: 2,
              display: 'inline-flex',
              backgroundColor: 'white',
            }}
          >
            <QRCodeSVG value={shareUrl} size={180} level="H" />
          </Paper>
        </Box>

        <Divider sx={{ my: 2 }} />

        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, justifyContent: 'center' }}>
          <Chip
            label={`Type: ${shareData.type}`}
            color="primary"
            variant="outlined"
            size="small"
          />
          
          <Chip
            label={`Expires in: ${formatTimeRemaining(shareData.expiryTime)}`}
            color="warning"
            variant="outlined"
            size="small"
          />

          {shareData.viewOnce && (
            <Chip
              label="View Once"
              color="error"
              variant="outlined"
              size="small"
            />
          )}

          {shareData.passwordProtected && (
            <Chip
              label="Password Protected"
              color="secondary"
              variant="outlined"
              size="small"
            />
          )}

          {shareData.fileCount && (
            <Chip
              label={`${shareData.fileCount} file${shareData.fileCount > 1 ? 's' : ''}`}
              variant="outlined"
              size="small"
            />
          )}
        </Box>
      </Paper>

      <Button
        variant="contained"
        startIcon={<Refresh />}
        onClick={onCreateAnother}
        sx={{ mt: 3 }}
        size="large"
      >
        Create Another Share
      </Button>
    </Box>
  );
};

export default SuccessView;
