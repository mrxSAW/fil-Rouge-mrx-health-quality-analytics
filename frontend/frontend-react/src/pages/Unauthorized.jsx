import { Link } from "react-router-dom";

function Unauthorized() {
  return (
    <main className="page">
      <h1>Accès refusé</h1>
      <p>Vous ne disposez pas des droits nécessaires pour cette page.</p>
      <Link to="/">Retour à l’accueil</Link>
    </main>
  );
}

export default Unauthorized;