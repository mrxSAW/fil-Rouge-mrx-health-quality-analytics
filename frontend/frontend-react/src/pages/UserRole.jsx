import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

import {
  getUserById,
  updateUserRole,
} from "../services/userService";

import { clearSession } from "../utils/session";

const roleLabels = {
  ADMIN: "Administrateur",
  QUALITY_MANAGER: "Responsable qualité",
  QHSE_MANAGER: "Responsable QHSE",
  STAFF: "Personnel",
};

function UserRole() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [user, setUser] = useState(null);
  const [selectedRole, setSelectedRole] = useState("");

  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState("");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const isCurrentUser = localStorage.getItem("userId") === id;

  useEffect(() => {
    let active = true;

    async function loadUser() {
      setLoading(true);
      setLoadError("");

      try {
        const data = await getUserById(id);

        if (active) {
          setUser(data);
          setSelectedRole(data.role);
        }
      } catch (error) {
        if (active) {
          setLoadError(
            error.response?.data?.message ||
              "Impossible de charger cet utilisateur."
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
  }, [id]);

  async function handleSubmit(event) {
    event.preventDefault();

    if (saving || !user || selectedRole === user.role) {
      return;
    }

    setSaving(true);
    setError("");

    try {
      await updateUserRole(id, selectedRole);

      if (isCurrentUser) {
        clearSession();
        navigate("/login", { replace: true });
        return;
      }

      navigate("/users", {
        replace: true,
        state: {
          message: "Rôle modifié avec succès.",
        },
      });
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Impossible de modifier le rôle."
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <main className="page">
      <div className="page-header">
        <h1>Modifier le rôle</h1>
        <Link to="/users">Retour aux utilisateurs</Link>
      </div>

      {loading ? (
        <p role="status">Chargement...</p>
      ) : loadError ? (
        <p className="error-message" role="alert">
          {loadError}
        </p>
      ) : user ? (
        <section className="form-panel">
          <h2>
            {user.firstName} {user.lastName}
          </h2>

          <p>{user.email}</p>

          <p>
            Rôle actuel : <strong>{roleLabels[user.role]}</strong>
          </p>

          {error && (
            <p className="error-message" role="alert">
              {error}
            </p>
          )}

          <form onSubmit={handleSubmit}>
            <div className="filter-field">
              <label htmlFor="user-role">Nouveau rôle</label>

              <select
                id="user-role"
                value={selectedRole}
                onChange={(event) => setSelectedRole(event.target.value)}
                disabled={saving}
                required
              >
                <option value="ADMIN">Administrateur</option>
                <option value="QUALITY_MANAGER">
                  Responsable qualité
                </option>
                <option value="QHSE_MANAGER">
                  Responsable QHSE
                </option>
                <option value="STAFF">Personnel</option>
              </select>
            </div>

            {isCurrentUser && selectedRole !== user.role && (
              <p className="error-message">
                Vous modifiez votre propre rôle. Vous perdrez vos
                droits administrateur et devrez vous reconnecter.
                Vérifiez qu’un autre administrateur existe.
              </p>
            )}

            <button
              type="submit"
              disabled={saving || selectedRole === user.role}
            >
              {saving ? "Enregistrement..." : "Enregistrer le rôle"}
            </button>
          </form>
        </section>
      ) : (
        <p>Utilisateur indisponible.</p>
      )}
    </main>
  );
}

export default UserRole;