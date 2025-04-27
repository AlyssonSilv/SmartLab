import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

export default function CadastroReserva() {
  const [form, setForm] = useState({
    descricao: "",
    dataInicio: "",
    dataFim: "",
    status: "PENDENTE",
    ambienteId: "",
    usuarioId: "",
  });

  const [ambientes, setAmbientes] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [erro, setErro] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    async function fetchDados() {
      try {
        const [resAmbientes, resUsuarios] = await Promise.all([
          axios.get("http://localhost:8080/api/ambientes"),
          axios.get("http://localhost:8080/api/usuarios"),
        ]);
        setAmbientes(resAmbientes.data);
        setUsuarios(resUsuarios.data);
      } catch (error) {
        console.error("Erro ao carregar ambientes ou usuários", error);
      }
    }

    fetchDados();
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await axios.post("http://localhost:8080/api/reservas", {
        descricao: form.descricao,
        dataInicio: form.dataInicio,
        dataFim: form.dataFim,
        status: form.status,
        ambiente: { id: form.ambienteId },
        usuario: { id: form.usuarioId },
      });

      navigate("/reservas");
    } catch (err) {
      console.error(err);
      setErro("Erro ao cadastrar a reserva.");
    }
  };

  return (
    <div className="container mt-5">
      <h2>Nova Reserva</h2>

      {erro && <div className="alert alert-danger">{erro}</div>}

      <form onSubmit={handleSubmit} className="mt-4">
        <div className="mb-3">
          <label className="form-label">Descrição</label>
          <input
            type="text"
            name="descricao"
            className="form-control"
            value={form.descricao}
            onChange={handleChange}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Data Início</label>
          <input
            type="datetime-local"
            name="dataInicio"
            className="form-control"
            value={form.dataInicio}
            onChange={handleChange}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Data Fim</label>
          <input
            type="datetime-local"
            name="dataFim"
            className="form-control"
            value={form.dataFim}
            onChange={handleChange}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Status</label>
          <select
            name="status"
            className="form-select"
            value={form.status}
            onChange={handleChange}
          >
            <option value="PENDENTE">Pendente</option>
            <option value="APROVADO">Aprovado</option>
            <option value="CANCELADO">Cancelado</option>
          </select>
        </div>

        <div className="mb-3">
          <label className="form-label">Ambiente</label>
          <select
            name="ambienteId"
            className="form-select"
            value={form.ambienteId}
            onChange={handleChange}
            required
          >
            <option value="">Selecione o ambiente</option>
            {ambientes.map((amb) => (
              <option key={amb.id} value={amb.id}>
                {amb.nome}
              </option>
            ))}
          </select>
        </div>

        <div className="mb-4">
          <label className="form-label">Usuário</label>
          <select
            name="usuarioId"
            className="form-select"
            value={form.usuarioId}
            onChange={handleChange}
            required
          >
            <option value="">Selecione o usuário</option>
            {usuarios.map((user) => (
              <option key={user.id} value={user.id}>
                {user.nome}
              </option>
            ))}
          </select>
        </div>

        <button type="submit" className="btn btn-success">Salvar Reserva</button>
      </form>
    </div>
  );
}
