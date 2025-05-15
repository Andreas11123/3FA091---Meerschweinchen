// src/services/api.js
import axios from 'axios'

// Basis-URL für die API-Anfragen

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/test/resources',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  timeout: 10000
})

// Add request interceptor for debugging
apiClient.interceptors.request.use(request => {
  console.log('Request:', {
    method: request.method,
    url: request.url,
    baseURL: request.baseURL,
    headers: request.headers,
    params: request.params,
    data: request.data
  });
  return request;
}, error => {
  console.error('Request error:', error);
  return Promise.reject(error);
});

// Add response interceptor for debugging
apiClient.interceptors.response.use(response => {
  console.log('Response:', {
    status: response.status,
    headers: response.headers,
    data: response.data
  });
  return response;
}, error => {
  console.error('Response error:', {
    message: error.message,
    status: error.response?.status,
    statusText: error.response?.statusText,
    headers: error.response?.headers,
    data: error.response?.data,
    config: error.config
  });
  return Promise.reject(error);
});

export default apiClient