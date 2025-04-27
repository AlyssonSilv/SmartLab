// src/pages/Ambientes.jsx
import { useEffect, useState } from 'react';
import AmbienteService from '../services/ambienteService';
/* import { Button, Modal, Form } from 'react-bootstrap'; */

export default function Ambientes() {
  const [ambientes, setAmbientes] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [ambienteAtual, setAmbienteAtual] = useState({ nome: '', descricao: '' });
  const [editandoId, setEditandoId] = useState(null);

  useEffect(() => {
    carregarAmbientes();
  }, []);

  const carregarAmbientes = async () => {
    const response = await AmbienteService.listar();
    setAmbientes(response.data);
  };

  const abrirModal = (ambiente = { nome: '', descricao: '' }) => {
    setAmbienteAtual(ambiente);
    setEditandoId(ambiente.id || null);
    setShowModal(true);
  };

  const fecharModal = () => {
    setShowModal(false);
    setAmbienteAtual({ nome: '', descricao: '' });
    setEditandoId(null);
  };

  const handleSalvar = async () => {
    if (editandoId) {
      await AmbienteService.atualizar(editandoId, ambienteAtual);
    } else {
      await AmbienteService.criar(ambienteAtual);
    }
    fecharModal();
    carregarAmbientes();
  };

  const handleExcluir = async (id) => {
    if (window.confirm('Deseja excluir este ambiente?')) {
      await AmbienteService.deletar(id);
      carregarAmbientes();
    }
  };

  return (
    <div className="container mt-4">
      <h2>Ambientes</h2>
      <Button variant="primary" className="mb-3" onClick={() => abrirModal()}>
        Novo Ambiente
      </Button>

      <table className="table table-striped">
        <thead>
          <tr>
            <th>Nome</th>
            <th>Descrição</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {ambientes.map((ambiente) => (
            <tr key={ambiente.id}>
              <td>{ambiente.nome}</td>
              <td>{ambiente.descricao}</td>
              <td>
                <Button variant="warning" size="sm" onClick={() => abrirModal(ambiente)} className="me-2">
                  Editar
                </Button>
                <Button variant="danger" size="sm" onClick={() => handleExcluir(ambiente.id)}>
                  Excluir
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Modal */}
      <Modal show={showModal} onHide={fecharModal}>
        <Modal.Header closeButton>
          <Modal.Title>{editandoId ? 'Editar Ambiente' : 'Novo Ambiente'}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Nome</Form.Label>
              <Form.Control
                type="text"
                value={ambienteAtual.nome}
                onChange={(e) => setAmbienteAtual({ ...ambienteAtual, nome: e.target.value })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Descrição</Form.Label>
              <Form.Control
                type="text"
                value={ambienteAtual.descricao}
                onChange={(e) => setAmbienteAtual({ ...ambienteAtual, descricao: e.target.value })}
              />
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={fecharModal}>
            Cancelar
          </Button>
          <Button variant="primary" onClick={handleSalvar}>
            Salvar
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}
