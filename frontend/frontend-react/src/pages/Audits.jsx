import { useEffect, useState } from "react";
import { getAudits } from "../services/auditService";
import { getAllDepartments } from "../services/departmentService";
import { Link, useLocation } from "react-router-dom";

function Audits() {
  const location = useLocation();
  const [audits, setAudits] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [departmentId, setDepartmentId] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [departmentsLoading, setDepartmentsLoading] = useState(true);
  const [departmentsError, setDepartmentsError] = useState("");

  useEffect(() => {
    let active = true;

    async function loadDepartments() {
      try {
        const data = await getAllDepartments();

        if (active) {
          setDepartments(data);
        }
      } catch {
        if (active) {
          setDepartmentsError(
            "Impossible de charger le filtre des départements."
          );
        }
      } finally {
        if (active) {
          setDepartmentsLoading(false);
        }
      }
    }

    loadDepartments();

    return () => {
      active = false;
    };
  }, []);

  useEffect(() => {
    let active = true;

    async function loadAudits() {
      setLoading(true);
      setError("");

      try {
        const data = await getAudits(page, departmentId);

        if (active) {
          setAudits(data.content);
          setTotalPages(data.totalPages);
          setTotalElements(data.totalElements);
        }
      } catch (error) {
        if (active) {
          setError(
            error.response?.data?.message ||
              "Impossible de charger les audits."
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadAudits();

    return () => {
      active = false;
    };
  }, [page, departmentId]);

  function formatDate(date) {
    if (!date) {
      return "Non renseignée";
    }

    const [year, month, day] = date.split("-");
    return `${day}/${month}/${year}`;
  }

  function formatValue(value, suffix = "") {
    if (value === null || value === undefined) {
      return "Non renseigné";
    }

    return (
      value.toLocaleString("fr-FR", {
        maximumFractionDigits: 2,
      }) + suffix
    );
  }

  function resetFilters() {
    setDepartmentId("");
    setPage(0);
  }

  return (
    <main className="page">
    <div className="page-header">
     <div>
      <h1>Audits</h1>
      <p>Consultez les audits et leurs résultats de conformité.</p>
      </div>

     <Link to="/audits/new" className="primary-link">
       Créer un audit
      </Link>
    </div>

{location.state?.message && (
  <p className="success-message" role="status">
    {location.state.message}
  </p>
)}
      {departmentsError && (
        <p className="error-message" role="alert">
          {departmentsError}
        </p>
      )}

      <section className="filters-bar" aria-label="Filtres des audits">
        <div className="filter-field">
          <label htmlFor="audit-department">Département</label>

          <select
            id="audit-department"
            value={departmentId}
            disabled={departmentsLoading || Boolean(departmentsError)}
            onChange={(event) => {
              setDepartmentId(event.target.value);
              setPage(0);
            }}
          >
            <option value="">
              {departmentsLoading
                ? "Chargement..."
                : "Tous les départements"}
            </option>

            {departments.map((department) => (
              <option key={department.id} value={department.id}>
                {department.name}
              </option>
            ))}
          </select>
        </div>

        <button
          type="button"
          className="secondary-button"
          onClick={resetFilters}
        >
          Réinitialiser
        </button>
      </section>

      <section className="recent-incidents">
        <h2>Liste des audits</h2>

        {loading ? (
          <p role="status">Chargement des audits...</p>
        ) : error ? (
          <p className="error-message" role="alert">
            {error}
          </p>
        ) : (
          <>
            <p className="table-description">
              {totalElements} audit(s) trouvé(s)
            </p>

            {audits.length === 0 ? (
              <p>Aucun audit ne correspond à votre recherche.</p>
            ) : (
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th scope="col">ID</th>
                      <th scope="col">Titre</th>
                      <th scope="col">Date</th>
                      <th scope="col">Département</th>
                      <th scope="col">Score / 100</th>
                      <th scope="col">Critères</th>
                      <th scope="col">Conformes</th>
                      <th scope="col">Conformité</th>
                    </tr>
                  </thead>

                  <tbody>
                    {audits.map((audit) => (
                      <tr key={audit.id}>
                        <td>#{audit.id}</td>
                        <td>
                         <Link to={`/audits/${audit.id}`}> {audit.title}</Link>
                       </td>
                        <td>{formatDate(audit.auditDate)}</td>
                        <td>
                          {audit.departmentName || "Non renseigné"}
                        </td>
                        <td>{formatValue(audit.score)}</td>
                        <td>{formatValue(audit.totalCriteria)}</td>
                        <td>{formatValue(audit.compliantCriteria)}</td>
                        <td>
                          {audit.conformityRate == null ? "Non calculable" : formatValue(audit.conformityRate, " %")}
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

export default Audits;