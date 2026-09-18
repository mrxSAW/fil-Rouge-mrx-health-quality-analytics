import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import {getIncidentById,updateIncidentStatus, deleteIncident} from "../services/incidentService";
import { getRole } from "../utils/session";

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

function IncidentDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const role = getRole();

  const [incident, setIncident] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [selectedStatus, setSelectedStatus] = useState("");
  const [saving, setSaving] = useState(false);
  const [statusError, setStatusError] = useState("");
  const [success, setSuccess] = useState("");

  const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);

  const [deleting, setDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState("");
  const canEdit = ["ADMIN","QUALITY_MANAGER","QHSE_MANAGER"].includes(role);
  const canDelete = role === "ADMIN";

  useEffect(() => {
    let active = true;

    async function loadIncident() {
      setLoading(true);
      setError("");
      setStatusError("");
      setSuccess("");
      setDeleteError("");
      setShowDeleteConfirmation(false);

      try {
        const data = await getIncidentById(id);

        if (active) {
          setIncident(data);
          setSelectedStatus(data.status);
        }
      } catch (error) {
        if (active) {
          setError(
            error.response?.data?.message ||
              "Impossible de charger cet incident."
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadIncident();

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

    if (
      saving ||
      deleting ||
      showDeleteConfirmation ||
      !canEdit
    ) {
      return;
    }

    setSaving(true);
    setStatusError("");
    setSuccess("");

    try {
      const updatedIncident = await updateIncidentStatus(
        id,
        selectedStatus
      );

      setIncident(updatedIncident);
      setSelectedStatus(updatedIncident.status);
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

  async function handleDelete() {
    if (deleting || saving || !canDelete) {
      return;
    }

    setDeleting(true);
    setDeleteError("");

    try {
      await deleteIncident(id);

      navigate("/incidents", {
        replace: true,
        state: {
          message: "Incident supprimé avec succès.",
        },
      });
    } catch (error) {
      setDeleteError(
        error.response?.data?.message ||
          "Impossible de supprimer cet incident. Il peut être lié à des actions correctives."
      );
    } finally {
      setDeleting(false);
    }
  }

  return (
    <main className="page">
      <div className="page-header">
        <h1>Détail de l’incident</h1>
        <Link to="/incidents">Retour aux incidents</Link>

        {canEdit &&
          incident &&
          !loading &&
          !error &&
          !saving &&
          !showDeleteConfirmation && (
            <Link
              to={`/incidents/${id}/edit`}
              className="primary-link"
            >
              Modifier l’incident
            </Link>
          )}
      </div>

      {loading ? (
        <p role="status">Chargement...</p>
      ) : error ? (
        <p className="error-message" role="alert">
          {error}
        </p>
      ) : incident ? (
        <section className="form-panel">
          <h2>
            #{incident.id} — {incident.title}
          </h2>

          <dl className="details-grid">
            <div>
              <dt>Date de l’incident</dt>
              <dd>{formatDate(incident.incidentDate)}</dd>
            </div>

            <div>
              <dt>Département</dt>
              <dd>{incident.departmentName || "Non renseigné"}</dd>
            </div>

            <div>
              <dt>Déclaré par</dt>
              <dd>{incident.reporterName || "Non renseigné"}</dd>
            </div>

            <div>
              <dt>Type</dt>
              <dd>{typeLabels[incident.type] || incident.type}</dd>
            </div>

            <div>
              <dt>Gravité</dt>
              <dd>
                <span className={`badge gravity-${incident.gravity}`}>
                  {gravityLabels[incident.gravity] || incident.gravity}
                </span>
              </dd>
            </div>

            <div>
              <dt>Statut</dt>
              <dd>
                <span className={`badge status-${incident.status}`}>
                  {statusLabels[incident.status] || incident.status}
                </span>
              </dd>
            </div>
          </dl>

          <h3>Description</h3>
          <p className="incident-description">
            {incident.description}
          </p>

          {canEdit && !showDeleteConfirmation && (
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
                <label htmlFor="incident-status">
                  Nouveau statut
                </label>

                <select
                  id="incident-status"
                  value={selectedStatus}
                  onChange={(event) => {
                    setSelectedStatus(event.target.value);
                    setSuccess("");
                    setStatusError("");
                  }}
                  disabled={saving}
                  required
                >
                  <option value="OPEN">Ouvert</option>
                  <option value="IN_PROGRESS">En cours</option>
                  <option value="RESOLVED">Résolu</option>
                  <option value="CLOSED">Clôturé</option>
                </select>
              </div>

              <button
                type="submit"
                disabled={
                  saving || selectedStatus === incident.status
                }
              >
                {saving ? "Enregistrement..." : "Modifier le statut"}
              </button>
            </form>
          )}

          {canDelete && (
            <div className="delete-section">
              {!showDeleteConfirmation ? (
                <button
                  type="button"
                  className="danger-button"
                  disabled={saving}
                  onClick={() => {
                    setDeleteError("");
                    setShowDeleteConfirmation(true);
                  }}
                >
                  Supprimer l’incident
                </button>
              ) : (
                <div className="delete-confirmation">
                  <h3>Confirmer la suppression</h3>

                  <p>
                    Voulez-vous supprimer l’incident
                    « {incident.title} » ? Cette action est définitive.
                  </p>

                  {deleteError && (
                    <p className="error-message" role="alert">
                      {deleteError}
                    </p>
                  )}

                  <div className="confirmation-actions">
                    <button
                      type="button"
                      className="secondary-button"
                      disabled={deleting}
                      onClick={() => {
                        setShowDeleteConfirmation(false);
                        setDeleteError("");
                      }}
                    >
                      Annuler
                    </button>

                    <button
                      type="button"
                      className="danger-button"
                      disabled={deleting || saving}
                      onClick={handleDelete}
                    >
                      {deleting
                        ? "Suppression..."
                        : "Confirmer la suppression"}
                    </button>
                  </div>
                </div>
              )}
            </div>
          )}
        </section>
      ) : (
        <p>Incident indisponible.</p>
      )}
    </main>
  );
}

export default IncidentDetails;