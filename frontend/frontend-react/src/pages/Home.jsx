import { getRole } from "../utils/session";

function Home() {
  const role = getRole();

  const roleNames = {
    ADMIN: "Administrateur",
    QUALITY_MANAGER: "Responsable qualité",
    QHSE_MANAGER: "Responsable QHSE",
    STAFF: "Personnel",
  };

  return (
    <main className="page">
      <h1>Bienvenue</h1>
      <p>Votre espace Health Quality Analytics.</p>
      <p>Votre rôle : {roleNames[role] || role}</p>
    </main>
  );
}

export default Home;