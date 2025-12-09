import React, { useEffect, useRef } from 'react';
import { Box, Chip } from '@mui/material';
import Prism from 'prismjs';
import 'prismjs/themes/prism-tomorrow.css';

// Import language dependencies
import 'prismjs/components/prism-markup';
import 'prismjs/components/prism-css';
import 'prismjs/components/prism-clike';
import 'prismjs/components/prism-javascript';
import 'prismjs/components/prism-typescript';
import 'prismjs/components/prism-python';
import 'prismjs/components/prism-java';
import 'prismjs/components/prism-c';
import 'prismjs/components/prism-cpp';
import 'prismjs/components/prism-csharp';
import 'prismjs/components/prism-go';
import 'prismjs/components/prism-rust';
import 'prismjs/components/prism-markup-templating';
import 'prismjs/components/prism-php';
import 'prismjs/components/prism-ruby';
import 'prismjs/components/prism-swift';
import 'prismjs/components/prism-kotlin';
import 'prismjs/components/prism-sql';
import 'prismjs/components/prism-json';
import 'prismjs/components/prism-yaml';
import 'prismjs/components/prism-markdown';
import 'prismjs/components/prism-bash';

const TextPreview = ({ content, fileName }) => {
  const codeRef = useRef(null);

  // Detect language from file extension
  const getLanguage = () => {
    const ext = fileName.split('.').pop().toLowerCase();
    const langMap = {
      js: 'javascript',
      jsx: 'javascript',
      ts: 'typescript',
      tsx: 'typescript',
      py: 'python',
      java: 'java',
      c: 'c',
      cpp: 'cpp',
      cc: 'cpp',
      cxx: 'cpp',
      cs: 'csharp',
      go: 'go',
      rs: 'rust',
      php: 'php',
      rb: 'ruby',
      swift: 'swift',
      kt: 'kotlin',
      sql: 'sql',
      html: 'markup',
      xml: 'markup',
      css: 'css',
      json: 'json',
      yaml: 'yaml',
      yml: 'yaml',
      md: 'markdown',
      sh: 'bash',
      bash: 'bash',
    };
    return langMap[ext] || 'plaintext';
  };

  const language = getLanguage();

  useEffect(() => {
    if (codeRef.current && content) {
      setTimeout(() => {
        try {
          Prism.highlightElement(codeRef.current);
        } catch (error) {
          console.warn('Prism highlighting failed:', error);
        }
      }, 10);
    }
  }, [content]);

  return (
    <Box>
      <Box sx={{ mb: 2 }}>
        <Chip label={language} variant="outlined" size="small" />
      </Box>
      <Box
        sx={{
          maxHeight: '500px',
          overflow: 'auto',
          backgroundColor: '#2d2d2d',
          borderRadius: 1,
          p: 2,
        }}
      >
        <pre style={{ margin: 0 }}>
          <code ref={codeRef} className={`language-${language}`}>
            {content}
          </code>
        </pre>
      </Box>
    </Box>
  );
};

export default TextPreview;
