import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

export default function Cadastro() {
  const [form, setForm] = useState({
    nome: "",
    email: "",
    senha: "",
    confirmarSenha: "",
  });
  const [erro, setErro] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (form.senha !== form.confirmarSenha) {
      setErro("As senhas não coincidem.");
      return;
    }

    try {
      await axios.post("http://localhost:8080/api/usuarios", {
        nome: form.nome,
        email: form.email,
        senha: form.senha,
      });

      navigate("/login");
    } catch (err) {
      setErro("Erro ao cadastrar. Email já pode estar em uso.");
    }
  };

  return (
    <div className="container mt-5" style={{ maxWidth: "500px" }}>
      <h2 className="mb-4">Criar Conta</h2>
      {erro && <div className="alert alert-danger">{erro}</div>}

      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label htmlFor="nome" className="form-label">Nome</label>
          <input
            type="text"
            className="form-control"
            id="nome"
            name="nome"
            value={form.nome}
            onChange={handleChange}
            required
          />
        </div>

        <div className="mb-3">
          <label htmlFor="email" className="form-label">Email</label>
          <input
            type="email"
            className="form-control"
            id="email"
            name="email"
            value={form.email}
            onChange={handleChange}
            required
          />
        </div>

        <div className="mb-3">
          <label htmlFor="senha" className="form-label">Senha</label>
          <input
            type="password"
            className="form-control"
            id="senha"
            name="senha"
            value={form.senha}
            onChange={handleChange}
            required
          />
        </div>

        <div className="mb-3">
          <label htmlFor="confirmarSenha" className="form-label">Confirmar Senha</label>
          <input
            type="password"
            className="form-control"
            id="confirmarSenha"
            name="confirmarSenha"
            value={form.confirmarSenha}
            onChange={handleChange}
            required
          />
        </div>

        <button type="submit" className="btn btn-primary w-100">Cadastrar</button>
      </form>
    </div>
  );
}
