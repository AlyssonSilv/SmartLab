import { Link, Outlet, useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

export default function Layout() {
  const { logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const isActive = (path) => location.pathname === path;

  return (
    <div className="d-flex" style={{ minHeight: "100vh" }}>
      <div
        className="p-3"
        style={{
          width: "220px",
          backgroundColor: "var(--sidebar-bg)",
          color: "var(--sidebar-text)",
        }}
      >
        <h4 className="mb-4 text-white">SmartLab</h4>
        <ul className="nav flex-column">
          <li className="nav-item">
            <Link
              to="/"
              className={`nav-link ${isActive("/") ? "active" : ""} text-white`}
            >
              Dashboard
            </Link>
          </li>
          <li className="nav-item">
            <Link
              to="/reservas"
              className={`nav-link ${isActive("/reservas") ? "active" : ""} text-white`}
            >
              Reservas
            </Link>
          </li>
          <li className="nav-item">
            <Link
              to="/ambientes"
              className={`nav-link ${isActive("/ambientes") ? "active" : ""} text-white`}
            >
              Ambientes
            </Link>
          </li>
          <li className="nav-item">
            <Link
              to="/perfil"
              className={`nav-link ${isActive("/perfil") ? "active" : ""} text-white`}
            >
              Perfil
            </Link>
          </li>
        </ul>

        <div className="mt-4">
          <button onClick={handleLogout} className="btn btn-outline-light w-100">
            Sair
          </button>
        </div>
      </div>

      <div className="flex-grow-1 p-4">
        <Outlet />
      </div>
    </div>
  );
}
