import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { getIncidentById,updateIncident,} from "../services/incidentService";
import { getAllDepartments } from "../services/departmentService";

function IncidentEdit() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [departments, setDepartments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [loadError, setLoadError] = useState("");
  const [error, setError] = useState("");

  const [formData, setFormData] = useState({
    title: "",
    description: "",
    departmentId: "",
    type: "",
    gravity: "",
    incidentDate: "",
  });

  useEffect(() => {
    let active = true;

    async function loadData() {
      setLoading(true);
      setLoadError("");

      try {
        const [incident, departments] = await Promise.all([
          getIncidentById(id),
          getAllDepartments(),
        ]);

        if (active) {
          setDepartments(departments);

          setFormData({
            title: incident.title,
            description: incident.description,
            departmentId: String(incident.departmentId),
            type: incident.type,
            gravity: incident.gravity,
            incidentDate: incident.incidentDate,
          });
        }
      } catch (error) {
        if (active) {
          setLoadError(
            error.response?.data?.message ||
              "Impossible de charger l’incident."
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

    setSaving(true);

    try {
      const data = {
        ...formData,
        title: formData.title.trim(),
        description: formData.description.trim(),
        departmentId: Number(formData.departmentId),
      };

      await updateIncident(id, data);

      navigate("/incidents", {
        replace: true,
        state: {
          message: "Incident modifié avec succès.",
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
          messages.join(" ") || "Impossible de modifier l’incident."
        );
      } else {
        setError("Impossible de modifier l’incident.");
      }
    } finally {
      setSaving(false);
    }
  }

  return (
    <main className="page">
      <div className="page-header">
        <h1>Modifier l’incident #{id}</h1>
        <Link to={`/incidents/${id}`}>Retour au détail</Link>
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
              value={formData.description}
              onChange={handleChange}
              rows={5}
              maxLength={3000}
              required
            />

            <div className="form-grid">
              <div className="filter-field">
                <label htmlFor="departmentId">Département</label>
                <select
                  id="departmentId"
                  name="departmentId"
                  value={formData.departmentId}
                  onChange={handleChange}
                  required
                >
                  <option value="">Choisir un département</option>

                  {departments.map((department) => (
                    <option key={department.id} value={department.id}>
                      {department.name}
                    </option>
                  ))}
                </select>
              </div>

              <div className="filter-field">
                <label htmlFor="incidentDate">Date de l’incident</label>
                <input
                  id="incidentDate"
                  name="incidentDate"
                  type="date"
                  value={formData.incidentDate}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="filter-field">
                <label htmlFor="type">Type</label>
                <select
                  id="type"
                  name="type"
                  value={formData.type}
                  onChange={handleChange}
                  required
                >
                  <option value="MEDICAL">Médical</option>
                  <option value="SECURITY">Sécurité</option>
                  <option value="HYGIENE">Hygiène</option>
                  <option value="EQUIPMENT">Équipement</option>
                  <option value="MEDICATION">Médicament</option>
                  <option value="ORGANIZATIONAL">Organisation</option>
                  <option value="OTHER">Autre</option>
                </select>
              </div>

              <div className="filter-field">
                <label htmlFor="gravity">Gravité</label>
                <select
                  id="gravity"
                  name="gravity"
                  value={formData.gravity}
                  onChange={handleChange}
                  required
                >
                  <option value="LOW">Faible</option>
                  <option value="MEDIUM">Moyenne</option>
                  <option value="HIGH">Élevée</option>
                  <option value="CRITICAL">Critique</option>
                </select>
              </div>
            </div>

            <button type="submit" disabled={saving}>
              {saving ? "Enregistrement..." : "Enregistrer les modifications"}
            </button>
          </form>
        </section>
      )}
    </main>
  );
}

export default IncidentEdit;