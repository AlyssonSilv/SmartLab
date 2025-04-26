import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from '../pages/Login';
import Dashboard from '../pages/Dashboard';
import Cadastro from '../pages/Cadastro';
import Reservas from '../pages/Reservas';
import Ambientes from '../pages/Ambientes';
import Perfil from '../pages/Perfil';
import Layout from '../components/Layout';
import { useAuth } from '../contexts/AuthContext';

export default function Router() {
  const { token } = useAuth();

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/cadastro" element={<Cadastro />} />

        {token ? (
          <Route element={<Layout />}>
            <Route path="/" element={<Dashboard />} />
            <Route path="/reservas" element={<Reservas />} />
            <Route path="/ambientes" element={<Ambientes />} />
            <Route path="/perfil" element={<Perfil />} />
          </Route>
        ) : (
          <Route path="*" element={<Navigate to="/login" />} />
        )}
      </Routes>
    </BrowserRouter>
  );
}
