import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { getAuditById, deleteAudit } from "../services/auditService";

function AuditDetails() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [audit, setAudit] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [showDeleteConfirmation, setShowDeleteConfirmation] =
    useState(false);

  const [deleting, setDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState("");

  useEffect(() => {
    let active = true;

    async function loadAudit() {
      setLoading(true);
      setError("");
      setDeleteError("");
      setShowDeleteConfirmation(false);

      try {
        const data = await getAuditById(id);

        if (active) {
          setAudit(data);
        }
      } catch (error) {
        if (active) {
          setError(
            error.response?.data?.message ||
              "Impossible de charger cet audit."
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadAudit();

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

  function getNonCompliantCriteria() {
    if (
      audit.totalCriteria == null ||
      audit.compliantCriteria == null
    ) {
      return "Non calculable";
    }

    return audit.totalCriteria - audit.compliantCriteria;
  }

  async function handleDelete() {
    if (deleting) {
      return;
    }

    setDeleting(true);
    setDeleteError("");

    try {
      await deleteAudit(id);

      navigate("/audits", {
        replace: true,
        state: {
          message: "Audit supprimé avec succès.",
        },
      });
    } catch (error) {
      setDeleteError(
        error.response?.data?.message ||
          "Impossible de supprimer cet audit."
      );
    } finally {
      setDeleting(false);
    }
  }

  return (
    <main className="page">
      <div className="page-header">
        <h1>Détail de l’audit</h1>
        <Link to="/audits">Retour aux audits</Link>

        {audit && !loading && !error && !showDeleteConfirmation && (
          <Link to={`/audits/${id}/edit`} className="primary-link">
            Modifier l’audit
          </Link>
        )}
      </div>

      {loading ? (
        <p role="status">Chargement de l’audit...</p>
      ) : error ? (
        <p className="error-message" role="alert">
          {error}
        </p>
      ) : audit ? (
        <section className="form-panel">
          <h2>
            #{audit.id} — {audit.title}
          </h2>

          <dl className="details-grid">
            <div>
              <dt>Date de l’audit</dt>
              <dd>{formatDate(audit.auditDate)}</dd>
            </div>

            <div>
              <dt>Département</dt>
              <dd>{audit.departmentName || "Non renseigné"}</dd>
            </div>

            <div>
              <dt>Score de l’audit</dt>
              <dd>{formatValue(audit.score, " / 100")}</dd>
            </div>

            <div>
              <dt>Taux de conformité</dt>
              <dd>
                {audit.conformityRate == null
                  ? "Non calculable"
                  : formatValue(audit.conformityRate, " %")}
              </dd>
            </div>

            <div>
              <dt>Nombre total de critères</dt>
              <dd>{formatValue(audit.totalCriteria)}</dd>
            </div>

            <div>
              <dt>Critères conformes</dt>
              <dd>{formatValue(audit.compliantCriteria)}</dd>
            </div>

            <div>
              <dt>Critères non conformes</dt>
              <dd>{getNonCompliantCriteria()}</dd>
            </div>
          </dl>

          <h3>Observations</h3>
          <p className="incident-description">
            {audit.observations || "Aucune observation renseignée."}
          </p>

          <div className="delete-section">
            {!showDeleteConfirmation ? (
              <button
                type="button"
                className="danger-button"
                onClick={() => {
                  setDeleteError("");
                  setShowDeleteConfirmation(true);
                }}
              >
                Supprimer l’audit
              </button>
            ) : (
              <div className="delete-confirmation">
                <h3>Confirmer la suppression</h3>

                <p>
                  Voulez-vous supprimer l’audit « {audit.title} » ?
                  Cette action est définitive.
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
          </div>
        </section>
      ) : (
        <p>Audit indisponible.</p>
      )}
    </main>
  );
}

export default AuditDetails;


