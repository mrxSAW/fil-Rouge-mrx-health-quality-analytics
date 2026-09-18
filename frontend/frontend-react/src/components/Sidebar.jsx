import { NavLink, useNavigate } from "react-router-dom";
import { clearSession, getRole } from "../utils/session";
import ThemeToggle from "./ThemeToggle";

function Sidebar() {
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
    <aside className="sidebar">
      <NavLink to="/" className="sidebar-brand">
        Health Quality
        <span>Analytics</span>
      </NavLink>

      <nav className="sidebar-nav" aria-label="Navigation principale">
        <NavLink to="/" end>
          Accueil
        </NavLink>
        
       {role === "ADMIN" && (  <NavLink to="/users">
                                     Utilisateurs
                                </NavLink>       )}


         <NavLink to="/departments">
            Départements
         </NavLink>

         {["ADMIN", "QUALITY_MANAGER"].includes(role) && (<NavLink to="/audits">
              Audits
            </NavLink>  )}


        <NavLink to="/incidents">
          Incidents
        </NavLink>

        {["ADMIN", "QHSE_MANAGER", "STAFF"].includes(role) && (
         <NavLink to="/corrective-actions">
              Actions correctives
         </NavLink>
          )}


        {canViewDashboard && (
          <NavLink to="/dashboard">
            Tableau de bord
          </NavLink> )}
      
        {["ADMIN", "QUALITY_MANAGER", "QHSE_MANAGER"].includes(role) && (
             <NavLink to="/reports">Rapports</NavLink>   )}
      
        {["ADMIN", "QUALITY_MANAGER"].includes(role) && (
               <NavLink to="/monthly-admissions">Admissions mensuelles</NavLink> )}

       {["ADMIN", "QHSE_MANAGER", "STAFF"].includes(role) &&  (
          <NavLink to="/User/profile">
           user profile 
          </NavLink>
      
      )}

      </nav>
        
     
      
      <div className="sidebar-bottom">
         
         <ThemeToggle />

        <button type="button" className="logout-button" onClick={handleLogout} >
          Déconnexion
        </button>

        <div className="sidebar-note">
          <strong>Qualité des soins</strong>
          <p>Suivre les incidents et les actions d’amélioration.</p>
        </div>
      </div>
    </aside>
  );
}

export default Sidebar;