import { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { getUsers } from "../services/userService";

const roleLabels = {
  ADMIN: "Administrateur",
  QUALITY_MANAGER: "Responsable qualité",
  QHSE_MANAGER: "Responsable QHSE",
  STAFF: "Personnel",
};

function Users() {
  const location = useLocation();

  const [users, setUsers] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;

    async function loadUsers() {
      setLoading(true);
      setError("");

      try {
        const data = await getUsers(page);

        if (active) {
          setUsers(data.content);
          setTotalPages(data.totalPages);
          setTotalElements(data.totalElements);
        }
      } catch (error) {
        if (active) {
          setError(
            error.response?.data?.message ||
              "Impossible de charger les utilisateurs."
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadUsers();

    return () => {
      active = false;
    };
  }, [page]);

  return (
    <main className="page">
      <h1>Utilisateurs</h1>
      <p>Consultez les comptes, leurs rôles et leurs départements.</p>

      {location.state?.message && (
        <p className="success-message" role="status">
          {location.state.message}
        </p>
      )}

      <section className="recent-incidents">
        <h2>Liste des utilisateurs</h2>

        {loading ? (
          <p role="status">Chargement des utilisateurs...</p>
        ) : error ? (
          <p className="error-message" role="alert">
            {error}
          </p>
        ) : (
          <>
            <p className="table-description">
              {totalElements} utilisateur(s)
            </p>

            {users.length === 0 ? (
              <p>Aucun utilisateur enregistré.</p>
            ) : (
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th scope="col">ID</th>
                      <th scope="col">Prénom</th>
                      <th scope="col">Nom</th>
                      <th scope="col">Email</th>
                      <th scope="col">Rôle</th>
                      <th scope="col">Département</th>
                      <th scope="col">Actions</th>
                    </tr>
                  </thead>

                  <tbody>
                    {users.map((user) => (
                     <tr key={user.id}>
                        <td>#{user.id}</td>
                        <td>{user.firstName}</td>
                        <td>{user.lastName}</td>
                        <td>{user.email}</td>

                        <td>
                          <span className="badge">
                            {roleLabels[user.role] || user.role}
                          </span>
                        </td>

                        <td>
                          {user.departmentName || "Non affecté"}
                        </td>

                       <td>
                            <div className="table-actions">
                             <Link to={`/users/${user.id}/edit`}> Modifier</Link>

                 <Link to={`/users/${user.id}/role`}> Changer le rôle </Link>

                  {String(user.id) !== localStorage.getItem("userId") && (
      <Link  to={`/users/${user.id}/delete`}className="delete-link">  Supprimer</Link>
                        )}
                           </div>
                         </td>
                    </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {totalPages > 0 && (
              <div className="pagination">
                <button
                  type="button"
                  disabled={page === 0}
                  onClick={() => setPage(page - 1)}
                >
                  Précédent
                </button>

                <span>
                  Page {page + 1} sur {totalPages}
                </span>

                <button
                  type="button"
                  disabled={page >= totalPages - 1}
                  onClick={() => setPage(page + 1)}
                >
                  Suivant
                </button>
              </div>
            )}
          </>
        )}
      </section>
    </main>
  );
}

export default Users;