import api from "../api/axiosConfig";

export async function getIncidents( page,type, gravity, status,  departmentId) {
  const response = await api.get("/incidents", {
    params: {
      page,
      size: 5,
      sort: "incidentDate",
      direction: "desc",
      type: type || undefined,
      gravity: gravity || undefined,
      status: status || undefined,
      departmentId: departmentId || undefined,
    },
  });

  return response.data;
}

export async function getRecentIncidents() {
  const response = await api.get("/incidents", {
    params: {
      page: 0,
      size: 5,
      sort: "incidentDate",
      direction: "desc",
    },
  });

  return response.data;
}

export async function getIncidentById(id) {
  const response = await api.get(`/incidents/staff/${id}`);
  return response.data;
}

export async function createIncident(data) {
  const response = await api.post("/incidents", data);
  return response.data;
}

export async function updateIncident(id, data) {
  const response = await api.put(`/incidents/${id}`, data);
  return response.data;
}

export async function updateIncidentStatus(id, status) {
  const response = await api.patch(`/incidents/${id}/status`, {
    status,
  });

  return response.data;
}

export async function deleteIncident(id) {
  await api.delete(`/incidents/${id}`);
}

export async function getAllIncidents() {
  let incidents = [];
  let page = 0;
  let lastPage = false;

  while (!lastPage) {
    const response = await api.get("/incidents", {
      params: {
        page,
        size: 100,
        sort: "id",
        direction: "desc",
      },
    });

    incidents = incidents.concat(response.data.content);
    lastPage = response.data.last;
    page++;
  }

  return incidents;
}