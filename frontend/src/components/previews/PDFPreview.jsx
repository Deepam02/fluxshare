import React from 'react';
import { Box } from '@mui/material';

const PDFPreview = ({ src, fileName }) => {
  // Add #toolbar=0 to disable PDF toolbar (print, download buttons)
  const pdfSrc = src.includes('#') ? src : `${src}#toolbar=0&navpanes=0&scrollbar=1`;

  return (
    <Box
      sx={{
        width: '100%',
        height: '600px',
        border: '1px solid',
        borderColor: 'divider',
        position: 'relative',
        // Disable right-click context menu
        '& iframe': {
          pointerEvents: 'auto',
        },
      }}
      onContextMenu={(e) => e.preventDefault()}
    >
      <iframe
        src={pdfSrc}
        title={fileName}
        style={{
          width: '100%',
          height: '100%',
          border: 'none',
        }}
      />
    </Box>
  );
};

export default PDFPreview;
