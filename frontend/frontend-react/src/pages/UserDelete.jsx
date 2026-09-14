import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { getUserById, deleteUser } from "../services/userService";

function UserDelete() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState("");

  const [deleting, setDeleting] = useState(false);
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

  async function handleDelete() {
    if (deleting || isCurrentUser || !user) {
      return;
    }

    setDeleting(true);
    setError("");

    try {
      await deleteUser(id);

      navigate("/users", {
        replace: true,
        state: {
          message: "Utilisateur supprimé avec succès.",
        },
      });
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Impossible de supprimer cet utilisateur. Il peut être lié à des incidents ou à des actions correctives."
      );
    } finally {
      setDeleting(false);
    }
  }

  return (
    <main className="page">
      <div className="page-header">
        <h1>Supprimer un utilisateur</h1>
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

          {isCurrentUser ? (
            <p className="error-message">
              La suppression de votre propre compte est désactivée
              dans cette interface.
            </p>
          ) : (
            <div className="delete-confirmation">
              <h3>Confirmer la suppression</h3>

              <p>
                Voulez-vous supprimer cet utilisateur ?
                Cette action est définitive.
              </p>

              {error && (
                <p className="error-message" role="alert">
                  {error}
                </p>
              )}

              <div className="confirmation-actions">
                <button
                  type="button"
                  className="secondary-button"
                  disabled={deleting}
                  onClick={() => navigate("/users")}
                >
                  Annuler
                </button>

                <button
                  type="button"
                  className="danger-button"
                  disabled={deleting}
                  onClick={handleDelete}
                >
                  {deleting
                    ? "Suppression..."
                    : "Confirmer la suppression"}
                </button>
              </div>
            </div>
          )}
        </section>
      ) : (
        <p>Utilisateur indisponible.</p>
      )}
    </main>
  );
}

export default UserDelete;