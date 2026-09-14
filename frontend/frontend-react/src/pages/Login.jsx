import { useState } from "react";
import { login } from "../services/authService";
import { Link, useLocation, useNavigate } from "react-router-dom";

function Login() {
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();

    setError("");
    setLoading(true);

    try {
      await login(email, password);
      navigate("/", { replace: true });
    } catch (error) {
      if (error.response?.status === 401) {
        setError("Email ou mot de passe incorrect.");
      } else if (!error.response) {
        setError("Impossible de contacter le serveur.");
      } else {
        setError(
          error.response.data?.message || "La connexion a échoué."
        );
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="card">
        <h1>Health Quality Analytics</h1>
        <p>Connectez-vous à votre espace.</p>

        {error && (
          <p className="error-message" role="alert">
            {error}
          </p>
        )}
{location.state?.message && (<p className="success-message" role="status">
    {location.state.message} </p> )}

        <form onSubmit={handleSubmit}>
          <label htmlFor="email">Adresse email</label>
     <input id="email" type="email"  value={email}
   onChange={(event) => setEmail(event.target.value)} autoComplete="username"
            required  />

          <label htmlFor="password">Mot de passe</label>
<input id="password"  type="password" value={password}
  onChange={(event) => setPassword(event.target.value)} autoComplete="current-password"
            required />

          <button type="submit" disabled={loading}>
            {loading ? "Connexion..." : "Se connecter"}
          </button>
        </form>

   <p>Pas encore de compte ? <Link to="/register">Créer un compte</Link> </p>
      
      </section>
    </main>
  );
}

export default Login;