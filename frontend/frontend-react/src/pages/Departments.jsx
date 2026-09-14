import { useEffect, useState } from "react";

import {
  getDepartments,
  createDepartment,
  updateDepartment,
  deleteDepartment,
} from "../services/departmentService";

import { getRole } from "../utils/session";

function Departments() {
  const [departments, setDepartments] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [refresh, setRefresh] = useState(0);

  const [showForm, setShowForm] = useState(false);
  const [editingDepartment, setEditingDepartment] = useState(null);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState("");

  const [departmentToDelete, setDepartmentToDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState("");

  const [success, setSuccess] = useState("");

  const canManage = getRole() === "ADMIN";

  useEffect(() => {
    let active = true;

    async function loadDepartments() {
      setLoading(true);
      setError("");

      try {
        const data = await getDepartments(page);

        if (active) {
          if (page > 0 && page >= data.totalPages) {
            setPage(Math.max(0, data.totalPages - 1));
            return;
          }

          setDepartments(data.content);
          setTotalPages(data.totalPages);
          setTotalElements(data.totalElements);
        }
      } catch (error) {
        if (active) {
          setError(
            error.response?.data?.message ||
              "Impossible de charger les départements."
          );
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadDepartments();

    return () => {
      active = false;
    };
  }, [page, refresh]);

  function openCreateForm() {
    setEditingDepartment(null);
    setName("");
    setDescription("");
    setFormError("");
    setSuccess("");
    setDepartmentToDelete(null);
    setShowForm(true);
  }

  function openEditForm(department) {
    setEditingDepartment(department);
    setName(department.name);
    setDescription(department.description || "");
    setFormError("");
    setSuccess("");
    setDepartmentToDelete(null);
    setShowForm(true);
  }

  function openDeleteConfirmation(department) {
    setDepartmentToDelete(department);
    setDeleteError("");
    setSuccess("");
    setShowForm(false);
  }

  function getErrorMessage(error, fallback) {
    const response = error.response?.data;

    if (response?.message) {
      return response.message;
    }

    if (response && typeof response === "object") {
      const messages = Object.values(response).filter(
        (value) => typeof value === "string"
      );

      if (messages.length > 0) {
        return messages.join(" ");
      }
    }

    return fallback;
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (saving || deleting || !canManage) {
      return;
    }

    setFormError("");

    if (!name.trim()) {
      setFormError("Le nom du département est obligatoire.");
      return;
    }

    setSaving(true);

    try {
      const data = {
        name: name.trim(),
        description: description.trim(),
      };

      if (editingDepartment) {
        await updateDepartment(editingDepartment.id, data);
        setSuccess("Département modifié avec succès.");
      } else {
        await createDepartment(data);
        setSuccess("Département créé avec succès.");
      }

      setShowForm(false);
      setEditingDepartment(null);
      setRefresh((value) => value + 1);
    } catch (error) {
      setFormError(
        getErrorMessage(
          error,
          "Impossible d’enregistrer le département."
        )
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete() {
    if (!departmentToDelete || deleting || saving || !canManage) {
      return;
    }

    setDeleting(true);
    setDeleteError("");

    try {
      await deleteDepartment(departmentToDelete.id);

      setDepartmentToDelete(null);
      setSuccess("Département supprimé avec succès.");
      setRefresh((value) => value + 1);
    } catch (error) {
      setDeleteError(
        getErrorMessage(
          error,
          "Impossible de supprimer ce département. Il peut être lié à d’autres données."
        )
      );
    } finally {
      setDeleting(false);
    }
  }

  return (
    <main className="page">
      <div className="page-header">
        <div>
          <h1>Départements</h1>
          <p>Consultez les départements de l’établissement.</p>
        </div>

        {canManage && !showForm && !departmentToDelete && (
          <button type="button" onClick={openCreateForm}>
            Ajouter un département
          </button>
        )}
      </div>

      {success && (
        <p className="success-message" role="status">
          {success}
        </p>
      )}

      {canManage && showForm && (
        <section className="form-panel">
          <h2>
            {editingDepartment
              ? "Modifier le département"
              : "Nouveau département"}
          </h2>

          {formError && (
            <p className="error-message" role="alert">
              {formError}
            </p>
          )}

          <form onSubmit={handleSubmit}>
            <label htmlFor="department-name">Nom</label>
            <input
              id="department-name"
              value={name}
              onChange={(event) => setName(event.target.value)}
              maxLength={255}
              disabled={saving}
              required
            />

            <label htmlFor="department-description">Description</label>
            <textarea
              id="department-description"
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              rows={4}
              maxLength={255}
              disabled={saving}
            />

            <div className="confirmation-actions">
              <button
                type="button"
                className="secondary-button"
                disabled={saving}
                onClick={() => setShowForm(false)}
              >
                Annuler
              </button>

              <button type="submit" disabled={saving}>
                {saving ? "Enregistrement..." : "Enregistrer"}
              </button>
            </div>
          </form>
        </section>
      )}

      {canManage && departmentToDelete && (
        <section className="delete-confirmation delete-section">
          <h2>Confirmer la suppression</h2>

          <p>
            Supprimer le département « {departmentToDelete.name} » ?
            Cette action est définitive.
          </p>

          {deleteError && (
            <p className="error-message" role="alert">
              {deleteError}
            </p>
          )}

          <div className="confirmation-actions">
            <button
              type="button"
              className="secondary-button"
              disabled={deleting}
              onClick={() => setDepartmentToDelete(null)}
            >
              Annuler
            </button>

            <button
              type="button"
              className="danger-button"
              disabled={deleting}
              onClick={handleDelete}
            >
              {deleting ? "Suppression..." : "Confirmer la suppression"}
            </button>
          </div>
        </section>
      )}

      <section className="recent-incidents">
        <h2>Liste des départements</h2>

        {loading ? (
          <p role="status">Chargement...</p>
        ) : error ? (
          <p className="error-message" role="alert">
            {error}
          </p>
        ) : (
          <>
            <p className="table-description">
              {totalElements} département(s)
            </p>

            {departments.length === 0 ? (
              <p>Aucun département enregistré.</p>
            ) : (
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th scope="col">ID</th>
                      <th scope="col">Nom</th>
                      <th scope="col">Description</th>
                      {canManage && <th scope="col">Actions</th>}
                    </tr>
                  </thead>

                  <tbody>
                    {departments.map((department) => (
                      <tr key={department.id}>
                        <td>#{department.id}</td>
                        <td>{department.name}</td>
                        <td>
                          {department.description || "Non renseignée"}
                        </td>

                        {canManage && (
                          <td>
                            <div className="table-actions">
                              <button
                                type="button"
                                className="secondary-button"
                                disabled={
                                  showForm ||
                                  Boolean(departmentToDelete)
                                }
                                onClick={() => openEditForm(department)}
                              >
                                Modifier
                              </button>

                              <button
                                type="button"
                                className="danger-button"
                                disabled={
                                  showForm ||
                                  Boolean(departmentToDelete)
                                }
                                onClick={() =>
                                  openDeleteConfirmation(department)
                                }
                              >
                                Supprimer
                              </button>
                            </div>
                          </td>
                        )}
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
                  disabled={page === 0 || saving || deleting}
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
                    page >= totalPages - 1 || saving || deleting
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
    </main>
  );
}

export default Departments;