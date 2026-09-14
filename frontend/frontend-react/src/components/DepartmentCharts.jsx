import { useEffect, useState } from "react";
import {
  Bar,
  BarChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";

import {
  getConformityByDepartment,
  getRiskByDepartment,
} from "../services/statisticsService";

function DepartmentCharts() {
  const [conformityData, setConformityData] = useState([]);
  const [riskData, setRiskData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;

    async function loadData() {
      try {
        const [conformity, risk] = await Promise.all([
          getConformityByDepartment(),
          getRiskByDepartment(),
        ]);

        if (active) {
          setConformityData(conformity);
          setRiskData(risk);
        }
      } catch (error) {
        if (active) {
          setError(
            error.response?.data?.message ||
              "Impossible de charger les statistiques par département."
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

  if (loading) {
    return <p role="status">Chargement des départements...</p>;
  }

  if (error) {
    return (
      <p className="error-message" role="alert">
        {error}
      </p>
    );
  }

  const calculatedConformity = conformityData.filter(
    (item) => item.conformityRate !== null
      && item.conformityRate !== undefined
  );

  const missingConformity = conformityData.filter(
    (item) => item.conformityRate === null
      || item.conformityRate === undefined
  );

  return (
    <section
      className="department-charts"
      aria-label="Indicateurs par département"
    >
      <article className="chart-card">
        <h2>Taux de conformité par département</h2>

        {calculatedConformity.length === 0 ? (
          <p>Aucun taux de conformité calculable.</p>
        ) : (
          <ResponsiveContainer
            width="100%"
            height={Math.max(260, calculatedConformity.length * 55)}
          >
            <BarChart
              data={calculatedConformity}
              layout="vertical"
              margin={{ top: 10, right: 25, bottom: 10, left: 0 }}
            >
              <CartesianGrid strokeDasharray="3 3" horizontal={false} />

              <XAxis type="number" domain={[0, 100]} unit=" %" />

              <YAxis
                type="category"
                dataKey="departmentName"
                width={120}
                tick={{ fontSize: 11 }}
              />

              <Tooltip
                formatter={(value) => [
                  `${Number(value).toLocaleString("fr-FR")} %`,
                  "Conformité",
                ]}
              />

              <Bar
                dataKey="conformityRate"
                fill="#087f8c"
                radius={[0, 5, 5, 0]}
                maxBarSize={28}
              />
            </BarChart>
          </ResponsiveContainer>
        )}

        {missingConformity.length > 0 && (
          <p className="chart-note">
            Non calculable :{" "}
            {missingConformity
              .map((item) => item.departmentName)
              .join(", ")}
            .
          </p>
        )}
      </article>

      <article className="chart-card">
        <h2>Score de risque par département</h2>

        {riskData.length === 0 ? (
          <p>Aucun département disponible.</p>
        ) : (
          <ResponsiveContainer
            width="100%"
            height={Math.max(260, riskData.length * 55)}
          >
            <BarChart
              data={riskData}
              layout="vertical"
              margin={{ top: 10, right: 25, bottom: 10, left: 0 }}
            >
              <CartesianGrid strokeDasharray="3 3" horizontal={false} />

              <XAxis type="number" domain={[0, "auto"]} />

              <YAxis
                type="category"
                dataKey="departmentName"
                width={120}
                tick={{ fontSize: 11 }}
              />

              <Tooltip
                formatter={(value) => [
                  Number(value).toLocaleString("fr-FR"),
                  "Score de risque",
                ]}
              />

              <Bar
                dataKey="riskScore"
                fill="#b45309"
                radius={[0, 5, 5, 0]}
                maxBarSize={28}
              />
            </BarChart>
          </ResponsiveContainer>
        )}

        <p className="chart-note">
          Score pondéré : il ne représente pas un pourcentage.
        </p>
      </article>
    </section>
  );
}

export default DepartmentCharts;