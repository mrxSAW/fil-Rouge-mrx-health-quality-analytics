import api from "../api/axiosConfig";

export async function getIncidentRate(departmentId, year, month) {
  const response = await api.get("/statistics/incidents/rate", {
    params: {
      departmentId,
      year,
      month,
    },
  });

  return response.data;
}