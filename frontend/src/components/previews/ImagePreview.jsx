import React, { useState } from 'react';
import { Box, IconButton, Tooltip } from '@mui/material';
import { ZoomIn, ZoomOut, RestartAlt } from '@mui/icons-material';

const ImagePreview = ({ src, fileName }) => {
  const [scale, setScale] = useState(1);

  const handleZoomIn = () => setScale(prev => Math.min(prev + 0.25, 3));
  const handleZoomOut = () => setScale(prev => Math.max(prev - 0.25, 0.5));
  const handleReset = () => setScale(1);

  return (
    <Box sx={{ position: 'relative', width: '100%', height: '100%' }}>
      <Box
        sx={{
          position: 'absolute',
          top: 10,
          right: 10,
          display: 'flex',
          gap: 1,
          backgroundColor: 'rgba(0, 0, 0, 0.6)',
          borderRadius: 1,
          padding: '4px',
          zIndex: 1,
        }}
      >
        <Tooltip title="Zoom In">
          <IconButton size="small" onClick={handleZoomIn} sx={{ color: 'white' }}>
            <ZoomIn />
          </IconButton>
        </Tooltip>
        <Tooltip title="Zoom Out">
          <IconButton size="small" onClick={handleZoomOut} sx={{ color: 'white' }}>
            <ZoomOut />
          </IconButton>
        </Tooltip>
        <Tooltip title="Reset">
          <IconButton size="small" onClick={handleReset} sx={{ color: 'white' }}>
            <RestartAlt />
          </IconButton>
        </Tooltip>
      </Box>
      
      <Box
        sx={{
          width: '100%',
          height: '500px',
          overflow: 'auto',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          backgroundColor: '#f5f5f5',
        }}
      >
        <img
          src={src}
          alt={fileName}
          style={{
            maxWidth: '100%',
            maxHeight: '100%',
            transform: `scale(${scale})`,
            transition: 'transform 0.2s',
            objectFit: 'contain',
          }}
        />
      </Box>
    </Box>
  );
};

export default ImagePreview;
