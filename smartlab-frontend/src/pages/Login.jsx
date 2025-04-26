import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../contexts/AuthContext";

export default function Login() {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [mensagem, setMensagem] = useState("");
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post("http://localhost:8080/api/auth/login", {
        email,
        senha,
      });

      const token = response.data.token;
      login(token);
      navigate("/");
    } catch (error) {
      setMensagem("Email ou senha inválidos.");
      console.error(error);
    }
  };

  return (
    <div className="container mt-5" style={{ maxWidth: "400px" }}>
      <h2 className="text-center mb-4">Login</h2>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label">E-mail</label>
          <input
            type="email"
            className="form-control"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Senha</label>
          <input
            type="password"
            className="form-control"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            required
          />
        </div>

        {mensagem && <div className="alert alert-danger">{mensagem}</div>}

        <div className="d-grid gap-2 mb-3">
          <button type="submit" className="btn btn-primary">Entrar</button>
        </div>

        <div className="text-center">
          <span>Não tem conta? </span>
          <Link to="/cadastro" className="btn btn-link">Criar conta</Link>
        </div>
      </form>
    </div>
  );
}
