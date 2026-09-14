import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

import {
  getAuditById,
  updateAudit,
} from "../services/auditService";

import { getAllDepartments } from "../services/departmentService";

function AuditEdit() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [departments, setDepartments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [loadError, setLoadError] = useState("");
  const [error, setError] = useState("");

  const [formData, setFormData] = useState({
    title: "",
    auditDate: "",
    score: "",
    totalCriteria: "",
    compliantCriteria: "",
    observations: "",
    departmentId: "",
  });

  useEffect(() => {
    let active = true;

    async function loadData() {
      setLoading(true);
      setLoadError("");

      try {
        const [audit, departments] = await Promise.all([
          getAuditById(id),
          getAllDepartments(),
        ]);

        if (active) {
          setDepartments(departments);

          setFormData({
            title: audit.title || "",
            auditDate: audit.auditDate || "",
            score: audit.score ?? "",
            totalCriteria: audit.totalCriteria ?? "",
            compliantCriteria: audit.compliantCriteria ?? "",
            observations: audit.observations || "",
            departmentId:
              audit.departmentId == null
                ? ""
                : String(audit.departmentId),
          });
        }
      } catch (error) {
        if (active) {
          setLoadError(
            error.response?.data?.message ||
              "Impossible de charger l’audit."
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

  function calculatePreview() {
    const total = Number(formData.totalCriteria);
    const compliant = Number(formData.compliantCriteria);

    if (
      formData.totalCriteria === "" ||
      formData.compliantCriteria === "" ||
      !Number.isInteger(total) ||
      !Number.isInteger(compliant) ||
      total < 1 ||
      compliant < 0 ||
      compliant > total
    ) {
      return "Non calculable";
    }

    const rate = (compliant * 100) / total;

    return (
      rate.toLocaleString("fr-FR", {
        maximumFractionDigits: 2,
      }) + " %"
    );
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (saving) {
      return;
    }

    setError("");

    const total = Number(formData.totalCriteria);
    const compliant = Number(formData.compliantCriteria);
    const score = Number(formData.score);

    if (!formData.title.trim()) {
      setError("Le titre est obligatoire.");
      return;
    }

    if (
      formData.totalCriteria === "" ||
      formData.compliantCriteria === "" ||
      !Number.isInteger(total) ||
      !Number.isInteger(compliant) ||
      total < 1 ||
      compliant < 0 ||
      compliant > total
    ) {
      setError(
        "Le total doit être un entier positif et les critères conformes doivent être compris entre 0 et ce total."
      );
      return;
    }

    if (
      formData.score === "" ||
      !Number.isInteger(score) ||
      score < 0 ||
      score > 100
    ) {
      setError("Le score doit être un entier compris entre 0 et 100.");
      return;
    }

    setSaving(true);

    try {
      await updateAudit(id, {
        title: formData.title.trim(),
        auditDate: formData.auditDate,
        score,
        totalCriteria: total,
        compliantCriteria: compliant,
        observations: formData.observations.trim(),
        departmentId: Number(formData.departmentId),
      });

      navigate("/audits", {
        replace: true,
        state: {
          message: "Audit modifié avec succès.",
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
          messages.join(" ") || "Impossible de modifier l’audit."
        );
      } else {
        setError("Impossible de modifier l’audit.");
      }
    } finally {
      setSaving(false);
    }
  }

  return (
    <main className="page">
      <div className="page-header">
        <h1>Modifier l’audit #{id}</h1>
        <Link to={`/audits/${id}`}>Retour au détail</Link>
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
            <label htmlFor="title">Titre de l’audit</label>
            <input
              id="title"
              name="title"
              value={formData.title}
              onChange={handleChange}
              maxLength={255}
              required
            />

            <div className="form-grid">
              <div className="filter-field">
                <label htmlFor="auditDate">Date de l’audit</label>
                <input
                  id="auditDate"
                  name="auditDate"
                  type="date"
                  value={formData.auditDate}
                  onChange={handleChange}
                  required
                />
              </div>

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
                <label htmlFor="totalCriteria">
                  Nombre total de critères
                </label>
                <input
                  id="totalCriteria"
                  name="totalCriteria"
                  type="number"
                  min="1"
                  step="1"
                  value={formData.totalCriteria}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="filter-field">
                <label htmlFor="compliantCriteria">
                  Nombre de critères conformes
                </label>
                <input
                  id="compliantCriteria"
                  name="compliantCriteria"
                  type="number"
                  min="0"
                  max={formData.totalCriteria || undefined}
                  step="1"
                  value={formData.compliantCriteria}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="filter-field">
                <label htmlFor="score">Score de l’audit / 100</label>
                <input
                  id="score"
                  name="score"
                  type="number"
                  min="0"
                  max="100"
                  step="1"
                  value={formData.score}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            <p role="status">
              <strong>Taux de conformité : </strong>
              {calculatePreview()}
            </p>

            <label htmlFor="observations">Observations</label>
            <textarea
              id="observations"
              name="observations"
              rows={4}
              maxLength={255}
              value={formData.observations}
              onChange={handleChange}
            />

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

export default AuditEdit;