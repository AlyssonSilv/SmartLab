// src/services/UsuarioService.js
import api from './api';

const UsuarioService = {
  login: (dados) => api.post('/auth/login', dados),
  cadastrar: (dados) => api.post('/usuarios', dados),
  buscarPerfil: () => api.get('/usuarios/perfil'), // exemplo de rota para buscar dados do usuário logado
};

export default UsuarioService;
