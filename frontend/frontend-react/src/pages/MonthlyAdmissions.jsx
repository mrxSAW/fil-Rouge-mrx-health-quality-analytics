import { useEffect, useState } from "react";
import { getAllDepartments } from "../services/departmentService";
import {
  getMonthlyAdmissions,
  createMonthlyAdmissions,
  updateMonthlyAdmissions,
} from "../services/monthlyAdmissionsService";

function MonthlyAdmissions() {
  const [admissions, setAdmissions] = useState([]);
  const [departments, setDepartments] = useState([]);

  const [departmentFilter, setDepartmentFilter] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [refresh, setRefresh] = useState(0);

  const [loading, setLoading] = useState(true);
  const [loadingDepartments, setLoadingDepartments] = useState(true);
  const [listError, setListError] = useState("");
  const [departmentError, setDepartmentError] = useState("");

  const [editingId, setEditingId] = useState(null);
  const [departmentId, setDepartmentId] = useState("");
  const [month, setMonth] = useState("");
  const [admissionCount, setAdmissionCount] = useState("");

  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState("");
  const [success, setSuccess] = useState("");

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

    async function loadAdmissions() {
      setLoading(true);
      setListError("");

      try {
        const data = await getMonthlyAdmissions(page, departmentFilter);

        if (active) {
          setAdmissions(data.content);
          setTotalPages(data.totalPages);
          setTotalElements(data.totalElements);
        }
      } catch (error) {
        if (active) {
          setListError(
            error.response?.data?.message ||
              "Impossible de charger les admissions."
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadAdmissions();

    return () => {
      active = false;
    };
  }, [page, departmentFilter, refresh]);

  function resetForm() {
    setEditingId(null);
    setDepartmentId("");
    setMonth("");
    setAdmissionCount("");
    setFormError("");
  }

  function handleEdit(admission) {
    setEditingId(admission.id);
    setDepartmentId(String(admission.departmentId));

    const year = String(admission.year).padStart(4, "0");
    const monthNumber = String(admission.month).padStart(2, "0");

    setMonth(`${year}-${monthNumber}`);
    setAdmissionCount(String(admission.admissionCount));
    setFormError("");
    setSuccess("");
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (saving) {
      return;
    }

    setFormError("");
    setSuccess("");

    const count = Number(admissionCount);

    if (
      admissionCount.trim() === "" ||
      !Number.isInteger(count) ||
      count < 0 ||
      count > 2147483647
    ) {
      setFormError("Veuillez saisir un nombre entier d’admissions valide.");
      return;
    }

    if (editingId === null && (!departmentId || !month)) {
      setFormError("Veuillez sélectionner un département et un mois.");
      return;
    }

    setSaving(true);

    try {
      if (editingId !== null) {
        await updateMonthlyAdmissions(editingId, {
          admissionCount: count,
        });
      } else {
        const [selectedYear, selectedMonth] = month.split("-");

        await createMonthlyAdmissions({
          departmentId: Number(departmentId),
          year: Number(selectedYear),
          month: Number(selectedMonth),
          admissionCount: count,
        });
      }

      setSuccess(
        editingId !== null
          ? "Le nombre d’admissions a été modifié."
          : "Les admissions ont été enregistrées."
      );

      resetForm();
      setPage(0);
      setRefresh((value) => value + 1);
    } catch (error) {
      const data = error.response?.data;

      const validationMessages =
        data && typeof data === "object"
          ? Object.values(data)
              .filter((value) => typeof value === "string")
              .join(" ")
          : "";

      setFormError(
        data?.message ||
          validationMessages ||
          "Impossible d’enregistrer les admissions."
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <main className="page">
      <h1>Admissions mensuelles</h1>
      <p>
        Renseignez le nombre d’admissions par département et par mois.
      </p>

      {success && (
        <p className="success-message" role="status">
          {success}
        </p>
      )}

      {departmentError && (
        <p className="error-message" role="alert">
          {departmentError}
        </p>
      )}

      <section className="form-panel">
        <h2>
          {editingId !== null
            ? "Modifier les admissions"
            : "Ajouter des admissions"}
        </h2>

        {formError && (
          <p className="error-message" role="alert">
            {formError}
          </p>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            <div className="filter-field">
              <label htmlFor="departmentId">Département</label>

              <select
                id="departmentId"
                value={departmentId}
                onChange={(event) => setDepartmentId(event.target.value)}
                disabled={
                  saving ||
                  loadingDepartments ||
                  Boolean(departmentError) ||
                  editingId !== null
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

            <div className="filter-field">
              <label htmlFor="month">Mois</label>

              <input
                id="month"
                type="month"
                min="0001-01"
                max="9999-12"
                value={month}
                onChange={(event) => setMonth(event.target.value)}
                disabled={saving || editingId !== null}
                required
              />
            </div>
          </div>

          <div className="filter-field">
            <label htmlFor="admissionCount">Nombre d’admissions</label>

            <input
              id="admissionCount"
              type="number"
              min="0"
              max="2147483647"
              step="1"
              value={admissionCount}
              onChange={(event) => setAdmissionCount(event.target.value)}
              disabled={saving}
              required
            />
          </div>

          <button
            type="submit"
            disabled={
              saving ||
              (editingId === null &&
                (loadingDepartments ||
                  Boolean(departmentError) ||
                  departments.length === 0))
            }
          >
            {saving
              ? "Enregistrement..."
              : editingId !== null
                ? "Enregistrer les modifications"
                : "Ajouter"}
          </button>

          {editingId !== null && (
            <button
              type="button"
              className="secondary-button"
              onClick={resetForm}
              disabled={saving}
            >
              Annuler
            </button>
          )}
        </form>
      </section>

      <section className="recent-incidents">
        <h2>Liste des admissions</h2>

        <div className="filters-bar">
          <div className="filter-field">
            <label htmlFor="departmentFilter">
              Filtrer par département
            </label>

            <select
              id="departmentFilter"
              value={departmentFilter}
              onChange={(event) => {
                setDepartmentFilter(event.target.value);
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
        </div>

        {loading ? (
          <p role="status">Chargement des admissions...</p>
        ) : listError ? (
          <p className="error-message" role="alert">
            {listError}
          </p>
        ) : (
          <>
            <p className="table-description">
              {totalElements} enregistrement(s)
            </p>

            {admissions.length === 0 ? (
              <p>Aucune admission enregistrée pour cette sélection.</p>
            ) : (
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th scope="col">Département</th>
                      <th scope="col">Mois</th>
                      <th scope="col">Nombre d’admissions</th>
                      <th scope="col">Action</th>
                    </tr>
                  </thead>

                  <tbody>
                    {admissions.map((admission) => (
                      <tr key={admission.id}>
                        <td>{admission.departmentName}</td>

                        <td>
                          {String(admission.month).padStart(2, "0")}
                          /{admission.year}
                        </td>

                        <td>{admission.admissionCount}</td>

                        <td>
                          <button
                            type="button"
                            className="secondary-button"
                            onClick={() => handleEdit(admission)}
                            disabled={saving}
                          >
                            Modifier
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
                  disabled={page === 0}
                  onClick={() => setPage(page - 1)}
                >
                  Précédent
                </button>

                <span>
                  Page {page + 1} sur {totalPages}
                </span>

                <button
                  type="button"
                  disabled={page >= totalPages - 1}
                  onClick={() => setPage(page + 1)}
                >
                  Suivant
                </button>
              </div>
            )}
          </>
        )}
      </section>
    </main>
  );
}

export default MonthlyAdmissions;