import { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";

export default function Reservas() {
  const [reservas, setReservas] = useState([]);

  useEffect(() => {
    async function fetchReservas() {
      try {
        const response = await axios.get("http://localhost:8080/api/reservas");
        setReservas(response.data);
      } catch (error) {
        console.error("Erro ao buscar reservas", error);
      }
    }

    fetchReservas();
  }, []);

  return (
    <div className="container mt-5">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Reservas</h2>
        <Link to="/reservas/nova" className="btn btn-primary">
          Nova Reserva
        </Link>
      </div>

      <div className="table-responsive">
        <table className="table table-striped table-hover">
          <thead className="table-dark">
            <tr>
              <th>ID</th>
              <th>Descrição</th>
              <th>Data Início</th>
              <th>Data Fim</th>
              <th>Status</th>
              <th>Ambiente</th>
              <th>Usuário</th>
            </tr>
          </thead>
          <tbody>
            {reservas.length > 0 ? (
              reservas.map((reserva) => (
                <tr key={reserva.id}>
                  <td>{reserva.id}</td>
                  <td>{reserva.descricao}</td>
                  <td>{new Date(reserva.dataInicio).toLocaleString()}</td>
                  <td>{new Date(reserva.dataFim).toLocaleString()}</td>
                  <td>{reserva.status}</td>
                  <td>{reserva.ambiente?.nome || "N/A"}</td>
                  <td>{reserva.usuario?.nome || "N/A"}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="7" className="text-center">Nenhuma reserva encontrada.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
