// src/services/ReservaService.js
import api from './api';

const ReservaService = {
  listar: () => api.get('/reservas'),
  buscarPorId: (id) => api.get(`/reservas/${id}`),
  criar: (reserva) => api.post('/reservas', reserva),
  atualizar: (id, reserva) => api.put(`/reservas/${id}`, reserva),
  deletar: (id) => api.delete(`/reservas/${id}`),
};

export default ReservaService;
