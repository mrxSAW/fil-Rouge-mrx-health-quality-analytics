import { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";

import { getIncidents } from "../services/incidentService";
import { getAllDepartments } from "../services/departmentService";

const typeLabels = {
  MEDICAL: "Médical",
  SECURITY: "Sécurité",
  HYGIENE: "Hygiène",
  EQUIPMENT: "Équipement",
  MEDICATION: "Médicament",
  ORGANIZATIONAL: "Organisation",
  OTHER: "Autre",
};

const gravityLabels = {
  LOW: "Faible",
  MEDIUM: "Moyenne",
  HIGH: "Élevée",
  CRITICAL: "Critique",
};

const statusLabels = {
  OPEN: "Ouvert",
  IN_PROGRESS: "En cours",
  RESOLVED: "Résolu",
  CLOSED: "Clôturé",
};

function Incidents() {
  const location = useLocation();

  const [incidents, setIncidents] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [type, setType] = useState("");
  const [gravity, setGravity] = useState("");
  const [status, setStatus] = useState("");
  const [departmentId, setDepartmentId] = useState("");

  const [departments, setDepartments] = useState([]);
  const [departmentsLoading, setDepartmentsLoading] = useState(true);
  const [departmentsError, setDepartmentsError] = useState("");

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

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

    async function loadIncidents() {
      setLoading(true);
      setError("");

      try {
        const data = await getIncidents(
          page,
          type,
          gravity,
          status,
          departmentId
        );

        if (active) {
          setIncidents(data.content);
          setTotalPages(data.totalPages);
          setTotalElements(data.totalElements);
        }
      } catch (error) {
        if (active) {
          setError(
            error.response?.data?.message ||
              "Impossible de charger les incidents."
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadIncidents();

    return () => {
      active = false;
    };
  }, [page, type, gravity, status, departmentId]);

  function resetFilters() {
    setType("");
    setGravity("");
    setStatus("");
    setDepartmentId("");
    setPage(0);
  }

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
          <h1>Incidents</h1>
          <p>
            Consultez et filtrez les incidents accessibles à votre compte.
          </p>
        </div>

        <Link to="/incidents/new" className="primary-link">
          Déclarer un incident
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

      <section className="filters-bar" aria-label="Filtres des incidents">
        <div className="filter-field">
          <label htmlFor="departmentId">Département</label>

          <select
            id="departmentId"
            value={departmentId}
            disabled={departmentsLoading || Boolean(departmentsError)}
            onChange={(event) => {
              setDepartmentId(event.target.value);
              setPage(0);
            }}
          >
            <option value="">
              {departmentsLoading ? "Chargement...": "Tous les départements"}
            </option>

            {departments.map((department) => (
              <option key={department.id} value={department.id}>
                {department.name}
              </option>
            ))}
          </select>
        </div>

        <div className="filter-field">
          <label htmlFor="type">Type</label>

          <select
            id="type"
            value={type}
            onChange={(event) => {
              setType(event.target.value);
              setPage(0);
            }}
          >
            <option value="">Tous les types</option>

            {Object.entries(typeLabels).map(([value, label]) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
        </div>

        <div className="filter-field">
          <label htmlFor="gravity">Gravité</label>

          <select
            id="gravity"
            value={gravity}
            onChange={(event) => {
              setGravity(event.target.value);
              setPage(0);
            }}
          >
            <option value="">Toutes les gravités</option>

            {Object.entries(gravityLabels).map(([value, label]) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
        </div>

        <div className="filter-field">
          <label htmlFor="status">Statut</label>

          <select
            id="status"
            value={status}
            onChange={(event) => {
              setStatus(event.target.value);
              setPage(0);
            }}
          >
            <option value="">Tous les statuts</option>

            {Object.entries(statusLabels).map(([value, label]) => (
              <option key={value} value={value}>
                {label}
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
        <h2>Liste des incidents</h2>

        {loading ? (
          <p role="status">Chargement des incidents...</p>
        ) : error ? (
          <p className="error-message" role="alert">
            {error}
          </p>
        ) : (
          <>
            <p className="table-description">
              {totalElements} incident(s) trouvé(s)
            </p>

            {incidents.length === 0 ? (
              <p>Aucun incident ne correspond à votre recherche.</p>
            ) : (
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th scope="col">ID</th>
                      <th scope="col">Titre</th>
                      <th scope="col">Date</th>
                      <th scope="col">Département</th>
                      <th scope="col">Type</th>
                      <th scope="col">Gravité</th>
                      <th scope="col">Statut</th>
                    </tr>
                  </thead>

                  <tbody>
                    {incidents.map((incident) => (
                      <tr key={incident.id}>
                        <td>#{incident.id}</td>

                        <td>
                          <Link to={`/incidents/${incident.id}`}>
                            {incident.title}
                          </Link>
                        </td>

                        <td>{formatDate(incident.incidentDate)}</td>

                        <td>
                          {incident.departmentName || "Non renseigné"}
                        </td>

                        <td>
                          {typeLabels[incident.type] || incident.type}
                        </td>

                        <td>
                          <span className={`badge gravity-${incident.gravity}`} >
                            {gravityLabels[incident.gravity] || incident.gravity}
                          </span>
                        </td>

                        <td>
                          <span className={`badge status-${incident.status}`}  >
                            {statusLabels[incident.status] ||incident.status}
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
                <button type="button" disabled={page === 0}
                  onClick={() => setPage(page - 1)} >
                  Précédent
                </button>

                <span>
                  Page {page + 1} sur {totalPages}
                </span>

                <button  type="button" disabled={page >= totalPages - 1}
                  onClick={() => setPage(page + 1)}  >
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

export default Incidents;