// src/services/AmbienteService.js
import api from './api';

const AmbienteService = {
  listar: () => api.get('/ambientes'),
  buscarPorId: (id) => api.get(`/ambientes/${id}`),
  criar: (ambiente) => api.post('/ambientes', ambiente),
  atualizar: (id, ambiente) => api.put(`/ambientes/${id}`, ambiente),
  deletar: (id) => api.delete(`/ambientes/${id}`),
};

export default AmbienteService;
