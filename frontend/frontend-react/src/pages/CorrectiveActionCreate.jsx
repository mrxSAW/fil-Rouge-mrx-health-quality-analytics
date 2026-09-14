import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import {
  createCorrectiveAction,
  getResponsibleUsers,
} from "../services/correctiveActionService";

import { getAllIncidents } from "../services/incidentService";

function CorrectiveActionCreate() {
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
      try {
        const [incidentData, userData] = await Promise.all([
          getAllIncidents(),
          getResponsibleUsers(),
        ]);

        if (active) {
          setIncidents(incidentData);
          setUsers(userData);
        }
      } catch (error) {
        if (active) {
          setLoadError(
            error.response?.data?.message ||
              "Impossible de charger les incidents et les responsables."
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
  }, []);

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
      await createCorrectiveAction({
        title: formData.title.trim(),
        description: formData.description.trim(),
        deadline: formData.deadline,
        incidentId: Number(formData.incidentId),
        responsibleUserId: Number(formData.responsibleUserId),
      });

      navigate("/corrective-actions", {
        replace: true,
        state: {
          message: "Action corrective créée avec succès.",
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
          messages.join(" ") || "Impossible de créer l’action."
        );
      } else {
        setError("Impossible de créer l’action.");
      }
    } finally {
      setSaving(false);
    }
  }

  return (
    <main className="page">
      <div className="page-header">
        <h1>Créer une action corrective</h1>
        <Link to="/corrective-actions">Retour aux actions</Link>
      </div>

      {loading ? (
        <p role="status">Chargement...</p>
      ) : loadError ? (
        <p className="error-message" role="alert">
          {loadError}
        </p>
      ) : incidents.length === 0 || users.length === 0 ? (
        <p>
          Un incident et un utilisateur doivent exister avant de créer
          une action corrective.
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

            <p className="chart-note">
              La nouvelle action sera créée avec le statut « À faire ».
            </p>

            <button type="submit" disabled={saving}>
              {saving ? "Enregistrement..." : "Créer l’action"}
            </button>
          </form>
        </section>
      )}
    </main>
  );
}

export default CorrectiveActionCreate;