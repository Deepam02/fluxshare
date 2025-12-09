import React, { useState } from 'react';
import { Routes, Route } from 'react-router-dom';
import {
  Container,
  Card,
  CardContent,
  Tabs,
  Tab,
  Box,
  Button,
  CircularProgress,
  Typography,
} from '@mui/material';
import { Send } from '@mui/icons-material';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import FileTab from './components/tabs/FileTab.jsx';
import TextTab from './components/tabs/TextTab.jsx';
import CodeTab from './components/tabs/CodeTab.jsx';
import OptionsPanel from './components/OptionsPanel.jsx';
import SuccessView from './components/SuccessView.jsx';
import ViewShare from './components/ViewShare.jsx';
import { createFileShare, createTextShare, createCodeShare } from './api/api.jsx';

function HomePage() {
  const [activeTab, setActiveTab] = useState(0);
  const [loading, setLoading] = useState(false);
  const [shareData, setShareData] = useState(null);

  // File tab state
  const [files, setFiles] = useState([]);

  // Text tab state
  const [text, setText] = useState('');

  // Code tab state
  const [code, setCode] = useState('');
  const [language, setLanguage] = useState('javascript');

  // Common options state
  const [options, setOptions] = useState({
    expiryHours: 24,
    password: '',
    viewOnce: false,
    notes: '',
    maxDownloads: '',
    maxViews: '',
  });

  const tabLabels = ['file', 'text', 'code'];

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  const resetForm = () => {
    setFiles([]);
    setText('');
    setCode('');
    setLanguage('javascript');
    setOptions({
      expiryHours: 24,
      password: '',
      viewOnce: false,
      notes: '',
      maxDownloads: '',
      maxViews: '',
    });
    setShareData(null);
  };

  const handleCreateAnother = () => {
    resetForm();
  };

  const validateForm = () => {
    if (activeTab === 0 && files.length === 0) {
      toast.error('Please select at least one file');
      return false;
    }
    if (activeTab === 1 && !text.trim()) {
      toast.error('Please enter some text');
      return false;
    }
    if (activeTab === 2 && !code.trim()) {
      toast.error('Please enter some code');
      return false;
    }
    return true;
  };

  const handleSubmit = async () => {
    if (!validateForm()) return;

    setLoading(true);

    try {
      let response;
      const commonData = {
        expiryHours: options.expiryHours,
        viewOnce: options.viewOnce,
        password: options.password || undefined,
        notes: options.notes || undefined,
      };

      if (activeTab === 0) {
        // File share
        const formData = new FormData();
        files.forEach((file) => {
          formData.append('files', file);
        });
        formData.append('expiryHours', commonData.expiryHours);
        formData.append('viewOnce', commonData.viewOnce);
        if (commonData.password) formData.append('password', commonData.password);
        if (commonData.notes) formData.append('notes', commonData.notes);
        if (options.maxDownloads) formData.append('maxDownloads', options.maxDownloads);
        if (options.maxViews) formData.append('maxViews', options.maxViews);

        console.log('FormData being sent:');
        for (let pair of formData.entries()) {
          console.log(pair[0], pair[1]);
        }

        response = await createFileShare(formData);
      } else if (activeTab === 1) {
        // Text share
        const textData = {
          text: text,
          ...commonData,
        };
        if (options.maxViews) textData.maxViews = parseInt(options.maxViews);

        response = await createTextShare(textData);
      } else {
        // Code share
        const codeData = {
          code: code,
          language: language,
          ...commonData,
        };
        if (options.maxViews) codeData.maxViews = parseInt(options.maxViews);

        response = await createCodeShare(codeData);
      }

      setShareData(response);
      toast.success('Share created successfully!');
    } catch (error) {
      console.error('Error creating share:', error);
      console.error('Error details:', {
        message: error.message,
        response: error.response?.data,
        status: error.response?.status,
        headers: error.response?.headers,
        config: {
          url: error.config?.url,
          method: error.config?.method,
          baseURL: error.config?.baseURL,
          data: error.config?.data instanceof FormData ? 'FormData (cannot log)' : error.config?.data,
        },
      });
      
      if (error.code === 'ECONNABORTED') {
        toast.error('Request timeout. The server took too long to respond.');
      } else if (error.response) {
        const errorData = error.response.data;
        const errorMsg = errorData?.message || errorData?.error || 'Failed to create share';
        
        console.error('Full error response:', errorData);
        
        if (error.response.status === 413) {
          toast.error('File size exceeds maximum allowed limit');
        } else if (error.response.status === 429) {
          toast.error('Rate limit exceeded. Please try again later.');
        } else if (error.response.status === 401) {
          toast.error('Unauthorized access');
        } else if (error.response.status === 404) {
          toast.error('API endpoint not found. Please check the backend URL.');
        } else if (error.response.status >= 500) {
          toast.error(`Server error: ${errorMsg}`);
        } else {
          toast.error(errorMsg);
        }
      } else if (error.request) {
        toast.error('Network error. Cannot reach the server. Please check your connection.');
      } else {
        toast.error('An unexpected error occurred: ' + error.message);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: '#f5f5f5',
        py: 4,
      }}
    >
      <Container maxWidth="md">
        <Typography
          variant="h3"
          align="center"
          gutterBottom
          sx={{
            fontWeight: 600,
            color: 'primary.main',
            mb: 4,
          }}
        >
          FluxShare
        </Typography>

        <Card
          elevation={3}
          sx={{
            borderRadius: 2,
            transition: 'transform 0.2s',
          }}
        >
          <CardContent sx={{ p: 3 }}>
            {!shareData ? (
              <>
                <Tabs
                  value={activeTab}
                  onChange={handleTabChange}
                  variant="fullWidth"
                  sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}
                >
                  <Tab label="File" />
                  <Tab label="Text" />
                  <Tab label="Code" />
                </Tabs>

                <Box sx={{ minHeight: 300 }}>
                  {activeTab === 0 && <FileTab files={files} setFiles={setFiles} />}
                  {activeTab === 1 && <TextTab text={text} setText={setText} />}
                  {activeTab === 2 && (
                    <CodeTab
                      code={code}
                      setCode={setCode}
                      language={language}
                      setLanguage={setLanguage}
                    />
                  )}
                </Box>

                <OptionsPanel
                  options={options}
                  setOptions={setOptions}
                  activeTab={tabLabels[activeTab]}
                />

                <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
                  <Button
                    variant="contained"
                    size="large"
                    startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <Send />}
                    onClick={handleSubmit}
                    disabled={loading}
                    sx={{ px: 6 }}
                  >
                    {loading ? 'Creating Share...' : 'Create Share'}
                  </Button>
                </Box>
              </>
            ) : (
              <SuccessView shareData={shareData} onCreateAnother={handleCreateAnother} />
            )}
          </CardContent>
        </Card>

        <Typography
          variant="body2"
          align="center"
          color="text.secondary"
          sx={{ mt: 3 }}
        >
          Secure file, text, and code sharing
        </Typography>
      </Container>
    </Box>
  );
}

function App() {
  return (
    <>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/s/:shareId" element={<ViewShare />} />
      </Routes>
      <ToastContainer
        position="top-right"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
      />
    </>
  );
}

export default App;
