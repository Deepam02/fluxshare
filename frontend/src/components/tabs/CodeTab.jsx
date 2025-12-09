import React from 'react';
import { Box, TextField, MenuItem } from '@mui/material';
import Editor from '@monaco-editor/react';
import { CODE_LANGUAGES } from '../../utils/constants.jsx';

// Map our language names to Monaco language IDs
const MONACO_LANGUAGE_MAP = {
  'javascript': 'javascript',
  'typescript': 'typescript',
  'python': 'python',
  'java': 'java',
  'c': 'c',
  'cpp': 'cpp',
  'csharp': 'csharp',
  'go': 'go',
  'rust': 'rust',
  'php': 'php',
  'ruby': 'ruby',
  'swift': 'swift',
  'kotlin': 'kotlin',
  'sql': 'sql',
  'html': 'html',
  'css': 'css',
  'json': 'json',
  'yaml': 'yaml',
  'markdown': 'markdown',
  'bash': 'shell',
  'plaintext': 'plaintext',
};

const CodeTab = ({ code, setCode, language, setLanguage }) => {
  const monacoLanguage = MONACO_LANGUAGE_MAP[language] || 'plaintext';

  const handleEditorChange = (value) => {
    setCode(value || '');
  };

  const handleEditorDidMount = (editor, monaco) => {
    // Enable more aggressive IntelliSense
    monaco.languages.typescript.javascriptDefaults.setCompilerOptions({
      target: monaco.languages.typescript.ScriptTarget.Latest,
      allowNonTsExtensions: true,
      moduleResolution: monaco.languages.typescript.ModuleResolutionKind.NodeJs,
      module: monaco.languages.typescript.ModuleKind.CommonJS,
      noEmit: true,
      esModuleInterop: true,
      allowSyntheticDefaultImports: true,
    });

    monaco.languages.typescript.typescriptDefaults.setCompilerOptions({
      target: monaco.languages.typescript.ScriptTarget.Latest,
      allowNonTsExtensions: true,
      moduleResolution: monaco.languages.typescript.ModuleResolutionKind.NodeJs,
      module: monaco.languages.typescript.ModuleKind.CommonJS,
      noEmit: true,
      esModuleInterop: true,
      allowSyntheticDefaultImports: true,
    });

    // Enable more features
    monaco.languages.typescript.javascriptDefaults.setDiagnosticsOptions({
      noSemanticValidation: false,
      noSyntaxValidation: false,
    });

    monaco.languages.typescript.typescriptDefaults.setDiagnosticsOptions({
      noSemanticValidation: false,
      noSyntaxValidation: false,
    });
  };

  return (
    <Box>
      <TextField
        select
        fullWidth
        label="Language"
        value={language}
        onChange={(e) => setLanguage(e.target.value)}
        sx={{ mb: 2 }}
      >
        {CODE_LANGUAGES.map((lang) => (
          <MenuItem key={lang} value={lang}>
            {lang.charAt(0).toUpperCase() + lang.slice(1)}
          </MenuItem>
        ))}
      </TextField>
      
      <Box
        sx={{
          border: '1px solid',
          borderColor: 'divider',
          borderRadius: 1,
          overflow: 'hidden',
        }}
      >
        <Editor
          height="400px"
          language={monacoLanguage}
          value={code}
          onChange={handleEditorChange}
          onMount={handleEditorDidMount}
          theme="vs-dark"
          options={{
            minimap: { enabled: true },
            fontSize: 14,
            lineNumbers: 'on',
            roundedSelection: true,
            scrollBeyondLastLine: false,
            automaticLayout: true,
            tabSize: 2,
            insertSpaces: true,
            wordWrap: 'on',
            formatOnPaste: true,
            formatOnType: true,
            autoIndent: 'full',
            bracketPairColorization: { enabled: true },
            suggest: {
              enabled: true,
              showKeywords: true,
              showSnippets: true,
              showWords: true,
              showMethods: true,
              showFunctions: true,
              showConstructors: true,
              showFields: true,
              showVariables: true,
              showClasses: true,
              showStructs: true,
              showInterfaces: true,
              showModules: true,
              showProperties: true,
              showEvents: true,
              showOperators: true,
              showUnits: true,
              showValues: true,
              showConstants: true,
              showEnums: true,
              showEnumMembers: true,
            },
            quickSuggestions: {
              other: true,
              comments: true,
              strings: true,
            },
            suggestOnTriggerCharacters: true,
            acceptSuggestionOnCommitCharacter: true,
            acceptSuggestionOnEnter: 'on',
            snippetSuggestions: 'top',
            wordBasedSuggestions: 'allDocuments',
            folding: true,
            foldingStrategy: 'indentation',
            showFoldingControls: 'always',
            matchBrackets: 'always',
            autoClosingBrackets: 'always',
            autoClosingQuotes: 'always',
            smoothScrolling: true,
            cursorBlinking: 'smooth',
            cursorSmoothCaretAnimation: 'on',
            padding: { top: 10, bottom: 10 },
          }}
          loading={
            <Box sx={{ p: 3, textAlign: 'center', color: 'text.secondary' }}>
              Loading editor...
            </Box>
          }
        />
      </Box>
    </Box>
  );
};

export default CodeTab;
