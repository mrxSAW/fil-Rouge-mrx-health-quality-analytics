import { NavLink, useNavigate } from "react-router-dom";
import { clearSession, getRole } from "../utils/session";

function Navbar() {
  const navigate = useNavigate();
  const role = getRole();

  const canViewDashboard = [
    "ADMIN",
    "QUALITY_MANAGER",
    "QHSE_MANAGER",
  ].includes(role);

  function handleLogout() {
    clearSession();
    navigate("/login", { replace: true });
  }

  return (
    <header className="navbar">
      <NavLink to="/" className="brand">
        Health Quality Analytics
      </NavLink>

      <nav aria-label="Navigation principale">
        <NavLink to="/" end>
          Accueil
        </NavLink>

        {canViewDashboard && (
          <NavLink to="/dashboard">Tableau de bord</NavLink>
        )}

        <button type="button" onClick={handleLogout}>
          Se déconnecter
        </button>
      </nav>
    </header>
  );
}

export default Navbar;