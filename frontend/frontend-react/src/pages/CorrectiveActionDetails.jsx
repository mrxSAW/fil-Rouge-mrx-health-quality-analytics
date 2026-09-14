import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import {
  getCorrectiveActionById,
  updateCorrectiveActionStatus,
} from "../services/correctiveActionService";

import { getRole } from "../utils/session";

const statusLabels = {
  TODO: "À faire",
  IN_PROGRESS: "En cours",
  COMPLETED: "Terminée",
  OVERDUE: "En retard",
  CANCELLED: "Annulée",
};

function CorrectiveActionDetails() {
  const { id } = useParams();

  const [action, setAction] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [selectedStatus, setSelectedStatus] = useState("");
  const [saving, setSaving] = useState(false);
  const [statusError, setStatusError] = useState("");
  const [success, setSuccess] = useState("");

  const canEdit = ["ADMIN", "QHSE_MANAGER"].includes(getRole());

  useEffect(() => {
    let active = true;

    async function loadAction() {
      setLoading(true);
      setError("");
      setStatusError("");
      setSuccess("");

      try {
        const data = await getCorrectiveActionById(id);

        if (active) {
          setAction(data);
          setSelectedStatus(
            data.status === "OVERDUE" ? "" : data.status
          );
        }
      } catch (error) {
        if (active) {
          setError(
            error.response?.data?.message ||
              "Impossible de charger cette action."
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadAction();

    return () => {
      active = false;
    };
  }, [id]);

  function formatDate(date) {
    if (!date) {
      return "Non renseignée";
    }

    const [year, month, day] = date.split("-");
    return `${day}/${month}/${year}`;
  }

  async function handleStatusChange(event) {
    event.preventDefault();

    if (saving || !canEdit || !selectedStatus) {
      return;
    }

    setSaving(true);
    setStatusError("");
    setSuccess("");

    try {
      const updatedAction = await updateCorrectiveActionStatus(
        id,
        selectedStatus
      );

      setAction(updatedAction);
      setSelectedStatus(updatedAction.status);
      setSuccess("Le statut a été mis à jour.");
    } catch (error) {
      setStatusError(
        error.response?.data?.message ||
          "Impossible de modifier le statut."
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <main className="page">
      <div className="page-header">
        <h1>Détail de l’action corrective</h1>
        <Link to="/corrective-actions">Retour aux actions</Link>
      
       {canEdit && action && !loading && !error && !saving && (
           <Link to={`/corrective-actions/${id}/edit`}  className="primary-link" >
              Modifier l’action
            </Link>  )}
      
      </div>

      {loading ? (
        <p role="status">Chargement...</p>
      ) : error ? (
        <p className="error-message" role="alert">
          {error}
        </p>
      ) : action ? (
        <section className="form-panel">
          <h2>
            #{action.id} — {action.title}
          </h2>

          <dl className="details-grid">
            <div>
              <dt>Incident associé</dt>
              <dd>{action.incidentTitle || "Non renseigné"}</dd>
            </div>

            <div>
              <dt>Responsable</dt>
              <dd>{action.responsibleUserName || "Non renseigné"}</dd>
            </div>

            <div>
              <dt>Échéance</dt>
              <dd>{formatDate(action.deadline)}</dd>
            </div>

            <div>
              <dt>Statut</dt>
              <dd>
                <span className={`badge action-${action.status}`}>
                  {statusLabels[action.status] || action.status}
                </span>
              </dd>
            </div>
          </dl>

          <h3>Description</h3>
          <p className="incident-description">
            {action.description || "Aucune description."}
          </p>

          {canEdit && (
            <form
              className="status-form"
              onSubmit={handleStatusChange}
            >
              <h3>Changer le statut</h3>

              {statusError && (
                <p className="error-message" role="alert">
                  {statusError}
                </p>
              )}

              {success && (
                <p className="success-message" role="status">
                  {success}
                </p>
              )}

              <div className="filter-field">
                <label htmlFor="action-status">Nouveau statut</label>

                <select
                  id="action-status"
                  value={selectedStatus}
                  onChange={(event) => {
                    setSelectedStatus(event.target.value);
                    setStatusError("");
                    setSuccess("");
                  }}
                  disabled={saving}
                  required
                >
                  <option value="" disabled>
                    Choisir un statut
                  </option>
                  <option value="TODO">À faire</option>
                  <option value="IN_PROGRESS">En cours</option>
                  <option value="COMPLETED">Terminée</option>
                  <option value="CANCELLED">Annulée</option>
                </select>
              </div>

              <p className="chart-note">
                Le retard est déterminé à partir de l’échéance.
                Il ne se sélectionne pas manuellement.
              </p>

              <button
                type="submit"
                disabled={
                  saving ||
                  !selectedStatus ||
                  selectedStatus === action.status
                }
              >
                {saving ? "Enregistrement..." : "Modifier le statut"}
              </button>
            </form>
          )}
        </section>
      ) : (
        <p>Action indisponible.</p>
      )}
    </main>
  );
}

export default CorrectiveActionDetails;