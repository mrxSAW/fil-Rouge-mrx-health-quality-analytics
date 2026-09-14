import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

import { getUserById, updateUser } from "../services/userService";
import { getAllDepartments } from "../services/departmentService";
import { clearSession } from "../utils/session";

function UserEdit() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [departments, setDepartments] = useState([]);
  const [originalEmail, setOriginalEmail] = useState("");

  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState("");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    departmentId: "",
  });

  useEffect(() => {
    let active = true;

    async function loadData() {
      setLoading(true);
      setLoadError("");

      try {
        const [user, departmentData] = await Promise.all([
          getUserById(id),
          getAllDepartments(),
        ]);

        if (active) {
          setDepartments(departmentData);
          setOriginalEmail(user.email);

          setFormData({
            firstName: user.firstName || "",
            lastName: user.lastName || "",
            email: user.email || "",
            departmentId:
              user.departmentId == null
                ? ""
                : String(user.departmentId),
          });
        }
      } catch (error) {
        if (active) {
          setLoadError(
            error.response?.data?.message ||
              "Impossible de charger cet utilisateur."
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

    const firstName = formData.firstName.trim();
    const lastName = formData.lastName.trim();
    const email = formData.email.trim();

    if (!firstName || !lastName || !email) {
      setError("Le prénom, le nom et l’email sont obligatoires.");
      return;
    }

    setSaving(true);

    try {
      await updateUser(id, {
        firstName,
        lastName,
        email,
        departmentId: formData.departmentId
          ? Number(formData.departmentId)
          : null,
      });

      const isCurrentUser = localStorage.getItem("userId") === id;

      if (isCurrentUser && email !== originalEmail) {
        clearSession();
        navigate("/login", { replace: true });
        return;
      }

      navigate("/users", {
        replace: true,
        state: {
          message: "Utilisateur modifié avec succès.",
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
          messages.join(" ") || "Impossible de modifier l’utilisateur."
        );
      } else {
        setError("Impossible de modifier l’utilisateur.");
      }
    } finally {
      setSaving(false);
    }
  }

  return (
    <main className="page">
      <div className="page-header">
        <h1>Modifier l’utilisateur #{id}</h1>
        <Link to="/users">Retour aux utilisateurs</Link>
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
            <label htmlFor="firstName">Prénom</label>
            <input
              id="firstName"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              maxLength={255}
              required
            />

            <label htmlFor="lastName">Nom</label>
            <input
              id="lastName"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              maxLength={255}
              required
            />

            <label htmlFor="email">Email</label>
            <input
              id="email"
              name="email"
              type="email"
              value={formData.email}
              onChange={handleChange}
              maxLength={255}
              required
            />

            <div className="filter-field">
              <label htmlFor="departmentId">Département</label>

              <select
                id="departmentId"
                name="departmentId"
                value={formData.departmentId}
                onChange={handleChange}
              >
                <option value="">Aucun département</option>

                {departments.map((department) => (
                  <option key={department.id} value={department.id}>
                    {department.name}
                  </option>
                ))}
              </select>
            </div>

            {localStorage.getItem("userId") === id && (
              <p className="chart-note">
                Si vous modifiez votre email, vous devrez vous
                reconnecter avec la nouvelle adresse.
              </p>
            )}

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

export default UserEdit;