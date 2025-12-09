import React from 'react';
import { TextField, Box, Typography } from '@mui/material';

const TextTab = ({ text, setText }) => {
  const wordCount = text.trim() ? text.trim().split(/\s+/).length : 0;
  const charCount = text.length;

  return (
    <Box>
      <TextField
        fullWidth
        multiline
        rows={12}
        placeholder="Enter your text here..."
        value={text}
        onChange={(e) => setText(e.target.value)}
        variant="outlined"
        sx={{
          '& .MuiOutlinedInput-root': {
            fontFamily: 'monospace',
          },
        }}
      />
      <Box sx={{ mt: 1, display: 'flex', justifyContent: 'space-between' }}>
        <Typography variant="caption" color="text.secondary">
          {charCount} characters
        </Typography>
        <Typography variant="caption" color="text.secondary">
          {wordCount} words
        </Typography>
      </Box>
    </Box>
  );
};

export default TextTab;
