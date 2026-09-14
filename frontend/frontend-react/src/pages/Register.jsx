import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { register } from "../services/authService";

function Register() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  function handleChange(event) {
    const { name, value } = event.target;

    setFormData((previousData) => ({  ...previousData,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (loading) {
      return;
    }

    setError("");

    if (
      !formData.firstName.trim() || !formData.lastName.trim() ||
      !formData.email.trim() || !formData.password.trim()
    ) {
      setError("Veuillez remplir tous les champs.");
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      setError("Les deux mots de passe ne correspondent pas.");
      return;
    }

    setLoading(true);

    try {
      await register({
        firstName: formData.firstName.trim(),lastName: formData.lastName.trim(),
        email: formData.email.trim(), password: formData.password,
      });

      navigate("/login", {
        replace: true,
        state: {
          message: "Compte créé avec succès. Vous pouvez vous connecter.",
        },
      });
    } catch (error) {
      const response = error.response?.data;

      if (response?.message) {
        setError(response.message);
      } else if (response && typeof response === "object") {
        const messages = Object.values(response).filter(
          (value) => typeof value === "string"
        );

        setError(
          messages.join(" ") || "Impossible de créer le compte."
        );
      } else {
        setError("Impossible de contacter le serveur.");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="card">
        <h1>Health Quality Analytics</h1>
        <h2>Créer un compte</h2>

        {error && (  <p className="error-message" role="alert"> {error}</p>)}

        <form onSubmit={handleSubmit}>
          <label htmlFor="firstName">Prénom</label>
 <input id="firstName" name="firstName" value={formData.firstName}
     onChange={handleChange} autoComplete="given-name" maxLength={255} required />

          <label htmlFor="lastName">Nom</label>
<input id="lastName" name="lastName" value={formData.lastName} onChange={handleChange}
    autoComplete="family-name" maxLength={255} required   />

          <label htmlFor="email">Email</label>
<input id="email" name="email" type="email"  value={formData.email}
 onChange={handleChange}  autoComplete="email"  maxLength={255}  required/>

          <label htmlFor="password">Mot de passe</label>
<input id="password" name="password" type="password" value={formData.password}
       onChange={handleChange}  autoComplete="new-password"  required />

          <label htmlFor="confirmPassword">  Confirmer le mot de passe </label>
 <input id="confirmPassword" name="confirmPassword" type="password"
value={formData.confirmPassword} onChange={handleChange} autoComplete="new-password"
            required />

          <button type="submit" disabled={loading}>
            {loading ? "Création..." : "Créer mon compte"}
          </button>
        </form>

        <p>
          Déjà un compte ? <Link to="/login">Se connecter</Link>
        </p>
      </section>
    </main>
  );
}

export default Register;