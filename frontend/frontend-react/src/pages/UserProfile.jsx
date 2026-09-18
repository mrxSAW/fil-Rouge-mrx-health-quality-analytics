import { useEffect, useState } from "react";
import { getMyProfile } from "../services/userService";

const roleLabels = {
  ADMIN: "Administrateur",
  QUALITY_MANAGER: "Responsable qualité",
  QHSE_MANAGER: "Responsable QHSE",
  STAFF: "Personnel",
};

function UserProfile() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;

    async function loadUser() {
      try {
        const data = await getMyProfile();

        if (active) {
          setUser(data);
        }
      } catch (error) {
        if (active) {
          setError(
            error.response?.data?.message ||
              "Impossible de charger votre profil."
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadUser();

    return () => {
      active = false;
    };
  }, []);

  return (
    <main className="page">
      <h1>Mon profil</h1>
      <p>Consultez les informations de votre compte.</p>

      <section className="form-panel">
        <h2>Informations personnelles</h2>

        {loading ? (
          <p role="status">Chargement du profil...</p>
        ) : error ? (
          <p className="error-message" role="alert">
            {error}
          </p>
        ) : user ? (
          <dl className="details-grid">
            <div>
              <dt>Prénom</dt>
              <dd>{user.firstName}</dd>
            </div>

            <div>
              <dt>Nom</dt>
              <dd>{user.lastName}</dd>
            </div>

            <div>
              <dt>Email</dt>
              <dd>{user.email}</dd>
            </div>

            <div>
              <dt>Rôle</dt>
              <dd>{roleLabels[user.role] || user.role}</dd>
            </div>

            <div>
              <dt>Département</dt>
              <dd>{user.departmentName || "Non affecté"}</dd>
            </div>
          </dl>
        ) : (
          <p>Aucune information disponible.</p>
        )}
      </section>
    </main>
  );
}

export default UserProfile;