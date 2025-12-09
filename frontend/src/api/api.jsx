import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 120000, // 120 seconds timeout for large file uploads
  headers: {
    'Content-Type': 'application/json',
  },
});

// File Share APIs
export const createFileShare = async (formData) => {
  const response = await api.post('/share/file', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return response.data;
};

// Text Share APIs
export const createTextShare = async (data) => {
  const response = await api.post('/share/text', data);
  return response.data;
};

// Code Share APIs
export const createCodeShare = async (data) => {
  const response = await api.post('/share/code', data);
  return response.data;
};

// Share Metadata API
export const getShareMetadata = async (shareId) => {
  const response = await api.get(`/share/${shareId}/metadata`);
  return response.data;
};

export default api;
