import { useEffect, useState } from "react";
import { getAllDepartments } from "../services/departmentService";
import {downloadIncidentPdf,downloadAuditPdf,downloadIncidentExcel, downloadMonthlyQualityPdf,downloadDepartmentQhsePdf,} from "../services/reportService";
import ReportList from "./ReportList";


function Reports() {
  const [reportType, setReportType] = useState("INCIDENT_PDF");
  const [departmentId, setDepartmentId] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [month, setMonth] = useState("");

  const [departments, setDepartments] = useState([]);
  const [loadingDepartments, setLoadingDepartments] = useState(true);
  const [departmentError, setDepartmentError] = useState("");

  const [downloading, setDownloading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const isMonthlyReport = reportType === "MONTHLY_QUALITY";
  const isQhseReport = reportType === "DEPARTMENT_QHSE";
  const [historyVersion, setHistoryVersion] = useState(0);

  useEffect(() => {
    let active = true;

    async function loadDepartments() {
      try {
        const data = await getAllDepartments();

        if (active) {
          setDepartments(data);
        }
      } catch (error) {
        if (active) {
          setDepartmentError(
            error.response?.data?.message ||
              "Impossible de charger les départements."
          );
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

  function handleReportTypeChange(event) {
    setReportType(event.target.value);
    setError("");
    setSuccess("");
  }

  async function handleDownload(event) {
    event.preventDefault();

    
    
    if (downloading) {
      return;
    }

    setError("");
    setSuccess("");

    if (isMonthlyReport && !month) {
      setError("Veuillez sélectionner un mois.");
      return;
    }

    if (isQhseReport && !departmentId) {
      setError("Veuillez sélectionner un département.");
      return;
    }

    if (!isMonthlyReport && !isQhseReport && startDate &&  endDate && startDate > endDate) {
      setError("La date de début doit précéder la date de fin.");
      return;
    }

    setDownloading(true);

    try {
      const selectedDepartmentId = departmentId ? Number(departmentId): undefined;

      switch (reportType) {
        case "INCIDENT_PDF":
          await downloadIncidentPdf(  selectedDepartmentId, startDate, endDate);
          break;

        case "AUDIT_PDF":
          await downloadAuditPdf(selectedDepartmentId,startDate,endDate);
          break;

        case "INCIDENT_EXCEL":
          await downloadIncidentExcel(selectedDepartmentId,startDate, endDate);
          break;

        case "MONTHLY_QUALITY": {
          const [selectedYear, selectedMonth] = month.split("-");

          await downloadMonthlyQualityPdf(
            Number(selectedYear),
            Number(selectedMonth)
          );
          break;
        }

        case "DEPARTMENT_QHSE":
          await downloadDepartmentQhsePdf(selectedDepartmentId);
          break;

        default:
          throw new Error("Veuillez sélectionner un rapport valide.");
      }

      setSuccess("Le téléchargement du rapport a été lancé.");
      setHistoryVersion((value) => value + 1);
    } catch (error) {
      setError(error.message || "Impossible de télécharger le rapport.");
    } finally {
      setDownloading(false);
    }
  }

  return (
    <main className="page">
      <h1>Rapports</h1>
      <p>
        Sélectionnez un rapport et ses critères pour le télécharger.
      </p>

      <section className="form-panel">
        <h2>Exporter un rapport</h2>

        {error && (
          <p className="error-message" role="alert">
            {error}
          </p>
        )}

        {success && (
          <p className="success-message" role="status">
            {success}
          </p>
        )}

        <form onSubmit={handleDownload}>
          <div className="filter-field">
            <label htmlFor="reportType">Type de rapport</label>
            <select
              id="reportType"
              value={reportType}
              onChange={handleReportTypeChange}
              disabled={downloading}
            >
              <option value="INCIDENT_PDF">Incidents — PDF</option>
              <option value="AUDIT_PDF">Audits — PDF</option>
              <option value="INCIDENT_EXCEL">Incidents — Excel</option>
              <option value="MONTHLY_QUALITY">
                Qualité mensuelle — PDF
              </option>
              <option value="DEPARTMENT_QHSE">
                QHSE par département — PDF
              </option>
            </select>
          </div>

          {!isMonthlyReport && (
            <div className="filter-field">
              <label htmlFor="departmentId">Département</label>
              <select
                id="departmentId"
                value={departmentId}
                onChange={(event) => setDepartmentId(event.target.value)}
                disabled={
                  downloading ||
                  loadingDepartments ||
                  Boolean(departmentError)
                }
                required={isQhseReport}
              >
                <option value="">
                  {isQhseReport
                    ? "Sélectionner un département"
                    : "Tous les départements"}
                </option>

                {departments.map((department) => (
                  <option key={department.id} value={department.id}>
                    {department.name}
                  </option>
                ))}
              </select>

              {loadingDepartments && (
                <p role="status">Chargement des départements...</p>
              )}

              {departmentError && (
                <p className="error-message" role="alert">
                  {departmentError}
                </p>
              )}
            </div>
          )}

          {!isMonthlyReport && !isQhseReport && (
            <>
              <div className="form-grid">
                <div className="filter-field">
                  <label htmlFor="startDate">Date de début</label>
                  <input
                    id="startDate"
                    type="date"
                    value={startDate}
                    onChange={(event) => setStartDate(event.target.value)}
                    disabled={downloading}
                  />
                </div>

                <div className="filter-field">
                  <label htmlFor="endDate">Date de fin</label>
                  <input
                    id="endDate"
                    type="date"
                    value={endDate}
                    min={startDate || undefined}
                    onChange={(event) => setEndDate(event.target.value)}
                    disabled={downloading}
                  />
                </div>
              </div>

              <p className="table-description">
                Laissez les dates vides pour couvrir toutes les dates.
              </p>
            </>
          )}

          {isMonthlyReport && (
            <div className="filter-field">
              <label htmlFor="month">Mois du rapport</label>
              <input
                id="month"
                type="month"
                value={month}
                onChange={(event) => setMonth(event.target.value)}
                disabled={downloading}
                required
              />
              <p className="table-description">
                Ce rapport concerne l’ensemble des départements.
              </p>
            </div>
          )}

          <button
            type="submit"
            disabled={
              downloading ||
              (isQhseReport &&
                (loadingDepartments || Boolean(departmentError)))
            }
          >
            {downloading
              ? "Génération du rapport..."
              : "Télécharger le rapport"}
          </button>
        </form>
      </section>

      <ReportList key={historyVersion} />
    </main>
  );
}

export default Reports;