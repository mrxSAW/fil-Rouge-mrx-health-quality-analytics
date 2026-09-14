import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

import {
  getCorrectiveActionById,
  getResponsibleUsers,
  updateCorrectiveAction,
} from "../services/correctiveActionService";

import { getAllIncidents } from "../services/incidentService";

function CorrectiveActionEdit() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [incidents, setIncidents] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState("");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const [formData, setFormData] = useState({
    title: "",
    description: "",
    deadline: "",
    incidentId: "",
    responsibleUserId: "",
  });

  useEffect(() => {
    let active = true;

    async function loadData() {
      setLoading(true);
      setLoadError("");

      try {
        const [action, incidentData, userData] = await Promise.all([
          getCorrectiveActionById(id),
          getAllIncidents(),
          getResponsibleUsers(),
        ]);

        if (active) {
          setIncidents(incidentData);
          setUsers(userData);

          setFormData({
            title: action.title || "",
            description: action.description || "",
            deadline: action.deadline || "",
            incidentId: String(action.incidentId),
            responsibleUserId: String(action.responsibleUserId),
          });
        }
      } catch (error) {
        if (active) {
          setLoadError(
            error.response?.data?.message ||
              "Impossible de charger l’action corrective."
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadData();

    return () => {
      active = false;
    };
  }, [id]);

  function handleChange(event) {
    const { name, value } = event.target;

    setFormData((previousData) => ({
      ...previousData,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (saving) {
      return;
    }

    setError("");

    if (!formData.title.trim() || !formData.description.trim()) {
      setError("Le titre et la description sont obligatoires.");
      return;
    }

    if (!formData.incidentId || !formData.responsibleUserId) {
      setError("Choisissez un incident et un responsable.");
      return;
    }

    setSaving(true);

    try {
      await updateCorrectiveAction(id, {
        title: formData.title.trim(),
        description: formData.description.trim(),
        deadline: formData.deadline,
        incidentId: Number(formData.incidentId),
        responsibleUserId: Number(formData.responsibleUserId),
      });

      navigate("/corrective-actions", {
        replace: true,
        state: {
          message: "Action corrective modifiée avec succès.",
        },
      });
    } catch (error) {
      const response = error.response?.data;

      if (response?.message) {
        setError(response.message);
      } else if (response && typeof response === "object") {
        const messages = Object.values(response).filter(
          (value) => typeof value === "string"
        );

        setError(
          messages.join(" ") || "Impossible de modifier l’action."
        );
      } else {
        setError("Impossible de modifier l’action.");
      }
    } finally {
      setSaving(false);
    }
  }

  return (
    <main className="page">
      <div className="page-header">
        <h1>Modifier l’action #{id}</h1>
        <Link to={`/corrective-actions/${id}`}>
          Retour au détail
        </Link>
      </div>

      {loading ? (
        <p role="status">Chargement...</p>
      ) : loadError ? (
        <p className="error-message" role="alert">
          {loadError}
        </p>
      ) : (
        <section className="form-panel">
          {error && (
            <p className="error-message" role="alert">
              {error}
            </p>
          )}

          <form onSubmit={handleSubmit}>
            <label htmlFor="title">Titre</label>
            <input
              id="title"
              name="title"
              value={formData.title}
              onChange={handleChange}
              maxLength={255}
              required
            />

            <label htmlFor="description">Description</label>
            <textarea
              id="description"
              name="description"
              rows={4}
              value={formData.description}
              onChange={handleChange}
              maxLength={255}
              required
            />

            <div className="form-grid">
              <div className="filter-field">
                <label htmlFor="incidentId">Incident associé</label>
                <select
                  id="incidentId"
                  name="incidentId"
                  value={formData.incidentId}
                  onChange={handleChange}
                  required
                >
                  <option value="">Choisir un incident</option>

                  {incidents.map((incident) => (
                    <option key={incident.id} value={incident.id}>
                      #{incident.id} — {incident.title}
                    </option>
                  ))}
                </select>
              </div>

              <div className="filter-field">
                <label htmlFor="responsibleUserId">Responsable</label>
                <select
                  id="responsibleUserId"
                  name="responsibleUserId"
                  value={formData.responsibleUserId}
                  onChange={handleChange}
                  required
                >
                  <option value="">Choisir un responsable</option>

                  {users.map((user) => (
                    <option key={user.id} value={user.id}>
                      {user.firstName} {user.lastName} — #{user.id}
                    </option>
                  ))}
                </select>
              </div>

              <div className="filter-field">
                <label htmlFor="deadline">Échéance</label>
                <input
                  id="deadline"
                  name="deadline"
                  type="date"
                  value={formData.deadline}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            <button type="submit" disabled={saving}>
              {saving
                ? "Enregistrement..."
                : "Enregistrer les modifications"}
            </button>
          </form>
        </section>
      )}
    </main>
  );
}

export default CorrectiveActionEdit;