import { useEffect, useState } from "react";
import { getAllDepartments } from "../services/departmentService";
import {
  getReports,
  downloadStoredReport,
} from "../services/reportService";

const typeLabels = {
  INCIDENT_PDF: "Incidents — PDF",
  AUDIT_PDF: "Audits — PDF",
  INCIDENT_EXCEL: "Incidents — Excel",
  MONTHLY_QUALITY: "Qualité mensuelle — PDF",
  QHSE_DEPARTMENT: "QHSE département — PDF",
};

function ReportList() {
  const [reports, setReports] = useState([]);
  const [departments, setDepartments] = useState([]);

  const [departmentId, setDepartmentId] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [loading, setLoading] = useState(true);
  const [loadingDepartments, setLoadingDepartments] = useState(true);
  const [departmentError, setDepartmentError] = useState("");
  const [error, setError] = useState("");

  const [downloadingId, setDownloadingId] = useState(null);
  const [downloadError, setDownloadError] = useState("");

  const invalidDates = Boolean(
    startDate && endDate && startDate > endDate
  );

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

  useEffect(() => {
    let active = true;

    if (invalidDates) {
      return;
    }

    async function loadReports() {
      setLoading(true);
      setError("");

      try {
        const data = await getReports(
          page,
          departmentId,
          startDate,
          endDate
        );

        if (active) {
          setReports(data.content);
          setTotalPages(data.totalPages);
          setTotalElements(data.totalElements);
        }
      } catch (error) {
        if (active) {
          setError(
            error.response?.data?.message ||
              "Impossible de charger les rapports."
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadReports();

    return () => {
      active = false;
    };
  }, [page, departmentId, startDate, endDate, invalidDates]);

  function resetFilters() {
    setDepartmentId("");
    setStartDate("");
    setEndDate("");
    setPage(0);
    setDownloadError("");
  }

  async function handleDownload(report) {
    if (downloadingId !== null) {
      return;
    }

    setDownloadingId(report.id);
    setDownloadError("");

    try {
      await downloadStoredReport(report.id, report.type);
    } catch (error) {
      setDownloadError(
        error.message || "Impossible de télécharger ce rapport."
      );
    } finally {
      setDownloadingId(null);
    }
  }

  function formatDate(date) {
    if (!date) {
      return "Non renseignée";
    }

    const [year, month, day] = date.split("-");

    return `${day}/${month}/${year}`;
  }

  return (
    <section className="recent-incidents">
      <h2>Historique des rapports</h2>

      <p className="table-description">
        Filtrez les rapports par département et date de génération.
      </p>

      <div className="report-history-filters">
        <div className="filter-field">
          <label htmlFor="historyDepartment">Département</label>

          <select
            id="historyDepartment"
            value={departmentId}
            onChange={(event) => {
              setDepartmentId(event.target.value);
              setPage(0);
            }}
            disabled={loadingDepartments || Boolean(departmentError)}
          >
            <option value="">Tous les départements</option>

            {departments.map((department) => (
              <option key={department.id} value={department.id}>
                {department.name}
              </option>
            ))}
          </select>
        </div>

        <div className="filter-field">
          <label htmlFor="historyStartDate">Généré à partir du</label>

          <input
            id="historyStartDate"
            type="date"
            value={startDate}
            onChange={(event) => {
              setStartDate(event.target.value);
              setPage(0);
            }}
          />
        </div>

        <div className="filter-field">
          <label htmlFor="historyEndDate">Généré jusqu’au</label>

          <input
            id="historyEndDate"
            type="date"
            value={endDate}
            min={startDate || undefined}
            onChange={(event) => {
              setEndDate(event.target.value);
              setPage(0);
            }}
          />
        </div>

        <button
          type="button"
          className="secondary-button"
          onClick={resetFilters}
        >
          Réinitialiser
        </button>
      </div>

      {departmentError && (
        <p className="error-message" role="alert">
          {departmentError}
        </p>
      )}

      {downloadError && (
        <p className="error-message" role="alert">
          {downloadError}
        </p>
      )}

      {invalidDates ? (
        <p className="error-message" role="alert">
          La date de début doit précéder ou être égale à la date de fin.
        </p>
      ) : loading ? (
        <p role="status">Chargement des rapports...</p>
      ) : error ? (
        <p className="error-message" role="alert">
          {error}
        </p>
      ) : (
        <>
          <p className="table-description">
            {totalElements} rapport(s) trouvé(s)
          </p>

          {reports.length === 0 ? (
            <p>Aucun rapport ne correspond à votre sélection.</p>
          ) : (
            <div className="table-container">
              <table className="data-table">
                <thead>
                  <tr>
                    <th scope="col">Titre</th>
                    <th scope="col">Type</th>
                    <th scope="col">Date de génération</th>
                    <th scope="col">Département</th>
                    <th scope="col">Action</th>
                  </tr>
                </thead>

                <tbody>
                  {reports.map((report) => (
                    <tr key={report.id}>
                      <td>{report.title}</td>
                      <td>{typeLabels[report.type] || report.type}</td>
                      <td>{formatDate(report.createdAt)}</td>
                      <td>
                        {report.departmentName || "Tous les départements"}
                      </td>
                      <td>
                        <button
                          type="button"
                          onClick={() => handleDownload(report)}
                          disabled={downloadingId !== null}
                        >
                          {downloadingId === report.id
                            ? "Téléchargement..."
                            : "Télécharger"}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {totalPages > 0 && (
            <div className="pagination">
              <button
                type="button"
                disabled={page === 0 || downloadingId !== null}
                onClick={() => setPage(page - 1)}
              >
                Précédent
              </button>

              <span>
                Page {page + 1} sur {totalPages}
              </span>

              <button
                type="button"
                disabled={
                  page >= totalPages - 1 || downloadingId !== null
                }
                onClick={() => setPage(page + 1)}
              >
                Suivant
              </button>
            </div>
          )}
        </>
      )}
    </section>
  );
}

export default ReportList;