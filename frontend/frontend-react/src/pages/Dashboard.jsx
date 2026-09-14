import { useEffect, useState } from "react";
import { getDashboard } from "../services/statisticsService";
import DashboardCharts from "../components/DashboardCharts";
import DepartmentCharts from "../components/DepartmentCharts";
import RecentIncidents from "../components/RecentIncidents";
import IncidentRate from "../components/IncidentRate";


function Dashboard() {
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;

    async function loadDashboard() {
      try {
        const data = await getDashboard();

        if (active) {
          setDashboard(data);
        }
      } catch (error) {
        if (active) {
          setError( error.response?.data?.message || "Impossible de charger le tableau de bord." );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadDashboard();

    return () => {
      active = false;
    };
  }, []);

  function formatValue(value, suffix = "") {
    if (value === null || value === undefined) {
      return "Non calculable";
    }

    return (
      value.toLocaleString("fr-FR", {
        maximumFractionDigits: 2,
      }) + suffix
    );
  }

  if (loading) {
    return (
      <main className="page">
        <p role="status">Chargement du tableau de bord...</p>
      </main>
    );
  }

  if (error) {
    return (
      <main className="page">
        <h1>Tableau de bord</h1>
        <p className="error-message" role="alert">
          {error}
        </p>
      </main>
    );
  }

  if (!dashboard) {
    return (
      <main className="page">
        <p>Aucune donnée disponible.</p>
      </main>
    );
  }

  return (
    <main className="page">
      <h1>Tableau de bord</h1>
      <p>Vue globale de tous les départements.</p>

      <div className="statistics-grid">
        <article className="stat-card">
          <h2>Incidents</h2>
          <p className="stat-value">
            {formatValue(dashboard.totalIncidents)}
          </p>
        </article>

        <article className="stat-card">
          <h2>Audits</h2>
          <p className="stat-value">
            {formatValue(dashboard.totalAudits)}
          </p>
        </article>

        <article className="stat-card">
          <h2>Incidents critiques</h2>
          <p className="stat-value">
            {formatValue(dashboard.criticalIncidents)}
          </p>
        </article>

        <article className="stat-card">
          <h2>Actions en retard</h2>
          <p className="stat-value">
            {formatValue(dashboard.overdueActions)}
          </p>
        </article>

        <article className="stat-card">
          <h2>Taux de conformité</h2>
          <p className="stat-value">
            {formatValue(dashboard.conformityRate, " %")}
          </p>
        </article>

        <article className="stat-card">
          <h2>Score qualité</h2>
          <p className="stat-value">
            {formatValue(dashboard.qualityScore, " / 100")}
          </p>
        </article>

        <article className="stat-card">
          <h2>Score de risque</h2>
          <p className="stat-value">
            {formatValue(dashboard.riskScore)}
          </p>
          <p className="stat-description">
            Score pondéré, exprimé sans pourcentage.
          </p>
        </article>
      </div>

      

      <DashboardCharts />
    
      <DepartmentCharts />

      <RecentIncidents />
      <IncidentRate />
      
    </main>
  );
}

export default Dashboard;