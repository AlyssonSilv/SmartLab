// src/services/api.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080', // Endereço do seu backend Spring Boot
});

// (Opcional) Adicionar automaticamente o token de autenticação, se existir
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token'); // Buscamos o token armazenado
  if (token) {
    config.headers.Authorization = `Bearer ${token}`; // Adiciona o token no cabeçalho Authorization
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

export default api;
