import api from "../api/axiosConfig";

export async function getDashboard() {
  const response = await api.get("/statistics/dashboard");

  return response.data;
}

export async function getMonthlyIncidents() {
  const response = await api.get("/statistics/incidents/monthly");
  return response.data;
}

export async function getIncidentsByType() {
  const response = await api.get("/statistics/incidents/by-type");
  return response.data;
}

export async function getIncidentsByGravity() {
  const response = await api.get("/statistics/incidents/by-gravity");
  return response.data;
}


export async function getConformityByDepartment() {
  const response = await api.get("/statistics/conformity/by-department");
  return response.data;
}

export async function getRiskByDepartment() {
  const response = await api.get("/statistics/risk/by-department");
  return response.data;
}