import { useEffect, useState } from "react";
import {
  CartesianGrid,
  Cell,
  Legend,
  Line,
  LineChart,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";

import {
  getMonthlyIncidents,
  getIncidentsByType,
  getIncidentsByGravity,
} from "../services/statisticsService";

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

const typeColors = [
  "#087f8c",
  "#2563eb",
  "#15803d",
  "#b45309",
  "#7c3aed",
  "#be185d",
  "#64748b",
];

const gravityColors = {
  LOW: "#15803d",
  MEDIUM: "#ca8a04",
  HIGH: "#ea580c",
  CRITICAL: "#dc2626",
};

function DashboardCharts() {
  const [monthlyData, setMonthlyData] = useState([]);
  const [typeData, setTypeData] = useState([]);
  const [gravityData, setGravityData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;

    async function loadCharts() {
      try {
        const [months, types, gravities] = await Promise.all([
          getMonthlyIncidents(),
          getIncidentsByType(),
          getIncidentsByGravity(),
        ]);

        if (!active) {
          return;
        }

        const sortedMonths = [...months].sort(
          (a, b) => a.year - b.year || a.month - b.month
        );

        const formattedMonths = sortedMonths.map((item) => ({
          ...item,
          label: String(item.month).padStart(2, "0") + "/" + item.year,
        }));

        const formattedTypes = types
          .filter((item) => item.totalIncidents > 0)
          .map((item) => ({
            ...item,
            name: typeLabels[item.type] || item.type,
          }));

        const formattedGravities = gravities
          .filter((item) => item.totalIncidents > 0)
          .map((item) => ({
            ...item,
            name: gravityLabels[item.gravity] || item.gravity,
          }));

        setMonthlyData(formattedMonths);
        setTypeData(formattedTypes);
        setGravityData(formattedGravities);
      } catch (error) {
        if (active) {
          setError(
            error.response?.data?.message ||
              "Impossible de charger les graphiques."
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadCharts();

    return () => {
      active = false;
    };
  }, []);

  if (loading) {
    return <p role="status">Chargement des graphiques...</p>;
  }

  if (error) {
    return (
      <p className="error-message" role="alert">
        {error}
      </p>
    );
  }

  return (
    <section className="charts-grid" aria-label="Statistiques des incidents">
      <article className="chart-card">
        <h2>Évolution mensuelle des incidents</h2>

        {monthlyData.length === 0 ? (
          <p>Aucun incident enregistré.</p>
        ) : (
          <>
            <ResponsiveContainer width="100%" height={260}>
              <LineChart
                data={monthlyData}
                margin={{ top: 15, right: 15, bottom: 5, left: 0 }}
              >
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="label" tick={{ fontSize: 11 }} />
                <YAxis allowDecimals={false} width={35} />
                <Tooltip />

                <Line
                  type="linear"
                  dataKey="totalIncidents"
                  name="Incidents"
                  stroke="#087f8c"
                  strokeWidth={3}
                  dot={{ r: 4 }}
                />
              </LineChart>
            </ResponsiveContainer>

            <p className="chart-note">
              Seuls les mois retournés par le backend sont affichés.
            </p>
          </>
        )}
      </article>

      <article className="chart-card">
        <h2>Incidents par type</h2>

        {typeData.length === 0 ? (
          <p>Aucun incident enregistré.</p>
        ) : (
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={typeData}
                dataKey="totalIncidents"
                nameKey="name"
                innerRadius={45}
                outerRadius={75}
                paddingAngle={2}
              >
                {typeData.map((item, index) => (
                  <Cell
                    key={item.type}
                    fill={typeColors[index % typeColors.length]}
                  />
                ))}
              </Pie>

              <Tooltip />
              <Legend wrapperStyle={{ fontSize: 12 }} />
            </PieChart>
          </ResponsiveContainer>
        )}
      </article>

      <article className="chart-card">
        <h2>Incidents par gravité</h2>

        {gravityData.length === 0 ? (
          <p>Aucun incident enregistré.</p>
        ) : (
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={gravityData}
                dataKey="totalIncidents"
                nameKey="name"
                innerRadius={45}
                outerRadius={75}
                paddingAngle={2}
              >
                {gravityData.map((item) => (
                  <Cell
                    key={item.gravity}
                    fill={gravityColors[item.gravity] || "#64748b"}
                  />
                ))}
              </Pie>

              <Tooltip />
              <Legend wrapperStyle={{ fontSize: 12 }} />
            </PieChart>
          </ResponsiveContainer>
        )}
      </article>
    </section>
  );
}

export default DashboardCharts;