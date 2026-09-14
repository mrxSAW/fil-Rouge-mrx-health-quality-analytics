import { useEffect, useState } from "react";
import { getRecentIncidents } from "../services/incidentService";

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

function RecentIncidents() {
  const [incidents, setIncidents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;

    async function loadIncidents() {
      try {
        const data = await getRecentIncidents();

        if (active) {
          setIncidents(data.content);
        }
      } catch (error) {
        if (active) {
          setError(
            error.response?.data?.message ||
              "Impossible de charger les incidents récents."
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
  }, []);

  function formatDate(date) {
    if (!date) {
      return "Non renseignée";
    }

    const [year, month, day] = date.split("-");
    return `${day}/${month}/${year}`;
  }

  return (
    <section className="recent-incidents">
      <h2>Incidents récents</h2>
      <p className="table-description">
        Les cinq derniers incidents par date d’incident.
      </p>

      {loading ? (
        <p role="status">Chargement des incidents...</p>
      ) : error ? (
        <p className="error-message" role="alert">
          {error}
        </p>
      ) : incidents.length === 0 ? (
        <p>Aucun incident enregistré.</p>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <caption className="sr-only">
              Les cinq incidents les plus récents
            </caption>

            <thead>
              <tr>
                <th scope="col">ID</th>
                <th scope="col">Titre</th>
                <th scope="col">Date</th>
                <th scope="col">Département</th>
                <th scope="col">Gravité</th>
                <th scope="col">Statut</th>
              </tr>
            </thead>

            <tbody>
              {incidents.map((incident) => (
                <tr key={incident.id}>
                  <td>#{incident.id}</td>
                  <td>{incident.title}</td>
                  <td>{formatDate(incident.incidentDate)}</td>
                  <td>{incident.departmentName || "Non renseigné"}</td>

                  <td>
                    <span className={`badge gravity-${incident.gravity}`}>
                      {gravityLabels[incident.gravity] || incident.gravity}
                    </span>
                  </td>

                  <td>
                    <span className={`badge status-${incident.status}`}>
                      {statusLabels[incident.status] || incident.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

export default RecentIncidents;