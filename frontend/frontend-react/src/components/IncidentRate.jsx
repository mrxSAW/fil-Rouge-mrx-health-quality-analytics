import { useEffect, useState } from "react";
import { getAllDepartments } from "../services/departmentService";
import { getIncidentRate } from "../services/incidentRateService";

function IncidentRate() {
  const [departments, setDepartments] = useState([]);
  const [departmentId, setDepartmentId] = useState("");
  const [month, setMonth] = useState("");

  const [statistics, setStatistics] = useState(null);
  const [loadingDepartments, setLoadingDepartments] = useState(true);
  const [departmentError, setDepartmentError] = useState("");
  const [loading, setLoading] = useState(false);
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
          setDepartmentError("Impossible de charger les départements.");
        }
      } finally {
        if (active) {
          setLoadingDepartments(false);
        }
      }
    }

    loadDepartments();

    return () => {
      active = false;
    };
  }, []);

  async function handleSubmit(event) {
    event.preventDefault();

    if (loading) {
      return;
    }

    setError("");
    setStatistics(null);

    if (!departmentId || !month) {
      setError("Veuillez sélectionner un département et un mois.");
      return;
    }

    setLoading(true);

    try {
      const [selectedYear, selectedMonth] = month.split("-");

      const data = await getIncidentRate(
        Number(departmentId),
        Number(selectedYear),
        Number(selectedMonth)
      );

      setStatistics(data);
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Impossible de calculer le taux d’incidents."
      );
    } finally {
      setLoading(false);
    }
  }

    return (
    <section className="incident-rate">
      <div className="incident-rate-heading">
        <h2>Taux d’incidents</h2>
        <p>
          Consultez le nombre d’incidents pour 1 000 admissions.
        </p>
      </div>

      {departmentError && (
        <p className="error-message" role="alert">
          {departmentError}
        </p>
      )}

      <form className="incident-rate-filters" onSubmit={handleSubmit}>
        <div className="incident-rate-field">
          <label htmlFor="rateDepartment">Département</label>

          <select
            id="rateDepartment"
            value={departmentId}
            onChange={(event) => {
              setDepartmentId(event.target.value);
              setStatistics(null);
              setError("");
            }}
            disabled={
              loadingDepartments || loading || Boolean(departmentError)
            }
            required
          >
            <option value="">Sélectionner un département</option>

            {departments.map((department) => (
              <option key={department.id} value={department.id}>
                {department.name}
              </option>
            ))}
          </select>
        </div>

        <div className="incident-rate-field">
          <label htmlFor="rateMonth">Mois</label>

          <input
            id="rateMonth"
            type="month"
            min="0001-01"
            max="9999-12"
            value={month}
            onChange={(event) => {
              setMonth(event.target.value);
              setStatistics(null);
              setError("");
            }}
            disabled={loading}
            required
          />
        </div>

        <button
          type="submit"
          disabled={
            loading ||
            loadingDepartments ||
            Boolean(departmentError) ||
            departments.length === 0
          }
        >
          {loading ? "Calcul en cours..." : "Afficher le taux"}
        </button>
      </form>

      {error && (
        <p className="error-message" role="alert">
          {error}
        </p>
      )}

      {statistics && (
        <div role="status">
          <p className="incident-rate-period">
            {statistics.departmentName} ·{" "}
            {String(statistics.month).padStart(2, "0")}/
            {statistics.year}
          </p>

          <dl className="incident-rate-results">
            <div className="incident-rate-card">
              <dt>Incidents</dt>
              <dd>{statistics.totalIncidents}</dd>
            </div>

            <div className="incident-rate-card">
              <dt>Admissions</dt>
              <dd>
                {statistics.admissionCount == null ? (
                  <span className="incident-rate-empty">
                    Non renseignées
                  </span>
                ) : (
                  statistics.admissionCount
                )}
              </dd>
            </div>

            <div className="incident-rate-card incident-rate-highlight">
              <dt>Incidents pour 1 000 admissions</dt>
              <dd>
                {statistics.incidentsPerThousandAdmissions == null ? (
                  <span className="incident-rate-empty">
                    Non calculable
                  </span>
                ) : (
                  statistics.incidentsPerThousandAdmissions.toLocaleString(
                    "fr-FR",
                    { maximumFractionDigits: 2 }
                  )
                )}
              </dd>
            </div>
          </dl>

          {statistics.admissionCount == null ? (
            <p className="incident-rate-note">
              Renseignez les admissions de ce département pour ce mois
              afin de calculer le taux.
            </p>
          ) : statistics.admissionCount === 0 ? (
            <p className="incident-rate-note">
              Le calcul nécessite un nombre d’admissions supérieur à zéro.
            </p>
          ) : null}
        </div>
      )}
    </section>
  );
}

export default IncidentRate;