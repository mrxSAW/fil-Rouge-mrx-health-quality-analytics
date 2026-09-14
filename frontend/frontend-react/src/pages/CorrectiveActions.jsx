import { useEffect, useState } from "react";
import { getCorrectiveActions } from "../services/correctiveActionService";
import { getRole } from "../utils/session";
import { Link, useLocation } from "react-router-dom"; 

const statusLabels = {
  TODO: "À faire",
  IN_PROGRESS: "En cours",
  COMPLETED: "Terminée",
  OVERDUE: "En retard",
  CANCELLED: "Annulée",
};

function CorrectiveActions() {

  const location = useLocation();
  const canCreate = ["ADMIN", "QHSE_MANAGER"].includes(getRole());
  const [actions, setActions] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [overdueOnly, setOverdueOnly] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const isStaff = getRole() === "STAFF";

  useEffect(() => {
    let active = true;

    async function loadActions() {
      setLoading(true);
      setError("");

      try {
        const data = await getCorrectiveActions(page, overdueOnly);

        if (active) {
          setActions(data.content);
          setTotalPages(data.totalPages);
          setTotalElements(data.totalElements);
        }
      } catch (error) {
        if (active) {
          setError(
            error.response?.data?.message ||
              "Impossible de charger les actions correctives."
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadActions();

    return () => {
      active = false;
    };
  }, [page, overdueOnly]);

  function formatDate(date) {
    if (!date) {
      return "Non renseignée";
    }

    const [year, month, day] = date.split("-");
    return `${day}/${month}/${year}`;
  }

  return (
    <main className="page">
 <div className="page-header">
     <div>
       <h1>Actions correctives</h1>
       <p>
        {isStaff ? "Consultez les actions qui vous sont assignées." : "Consultez les actions correctives et leurs échéances."}
       </p>
     </div>

       {canCreate && (
          <Link to="/corrective-actions/new" className="primary-link">
             Créer une action
          </Link> )}
 </div>

     {location.state?.message && (
      <p className="success-message" role="status"> {location.state.message} </p>
       )}

      <section className="filters-bar" aria-label="Filtres des actions">
        <div className="filter-field">
          <label htmlFor="action-filter">Afficher</label>

          <select
            id="action-filter"
            value={overdueOnly ? "overdue" : "all"}
            onChange={(event) => {
              setOverdueOnly(event.target.value === "overdue");
              setPage(0);
            }}
          >
            <option value="all">Toutes les actions accessibles</option>
            <option value="overdue">Actions en retard</option>
          </select>
        </div>

        <button
          type="button"
          className="secondary-button"
          onClick={() => {
            setOverdueOnly(false);
            setPage(0);
          }}
        >
          Réinitialiser
        </button>
      </section>

      <section className="recent-incidents">
        <h2>
          {overdueOnly ? "Actions en retard" : "Liste des actions"}
        </h2>

        {overdueOnly && (
          <p className="table-description">
            Échéance dépassée, hors actions terminées ou annulées.
          </p>
        )}

        {loading ? (
          <p role="status">Chargement des actions...</p>
        ) : error ? (
          <p className="error-message" role="alert">
            {error}
          </p>
        ) : (
          <>
            <p className="table-description">
              {totalElements} action(s) trouvée(s)
            </p>

            {actions.length === 0 ? (
              <p>Aucune action ne correspond à votre recherche.</p>
            ) : (
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th scope="col">ID</th>
                      <th scope="col">Titre</th>
                      <th scope="col">Incident associé</th>
                      <th scope="col">Responsable</th>
                      <th scope="col">Échéance</th>
                      <th scope="col">Statut</th>
                    </tr>
                  </thead>

                  <tbody>
                    {actions.map((action) => (
                      <tr key={action.id}>
                        <td>#{action.id}</td>

                        <td>
                         <Link to={`/corrective-actions/${action.id}`}> {action.title} </Link>
                        </td>

                        <td>
                          {action.incidentTitle || "Non renseigné"}
                        </td>
                        <td>
                          {action.responsibleUserName || "Non renseigné"}
                        </td>
                        <td>{formatDate(action.deadline)}</td>
                        <td>
                          <span className={`badge action-${action.status}`}>
                            {statusLabels[action.status] || action.status}
                          </span>
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

export default CorrectiveActions;