import api from "../api/axiosConfig";

export async function getMonthlyAdmissions(page = 0, departmentId = "") {
  const response = await api.get("/monthly-admissions", {
    params: {
      page,
      size: 5,
      direction: "desc",
      departmentId: departmentId || undefined,
    },
  });

  return response.data;
}

export async function getMonthlyAdmissionsById(id) {
  const response = await api.get(`/monthly-admissions/${id}`);

  return response.data;
}

export async function createMonthlyAdmissions(data) {
  const response = await api.post("/monthly-admissions", data);

  return response.data;
}

export async function updateMonthlyAdmissions(id, data) {
  const response = await api.put(`/monthly-admissions/${id}`, data);

  return response.data;
}