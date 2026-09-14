import api from "../api/axiosConfig";

export async function getAudits(page, departmentId) {
  const response = await api.get("/audits", {
    params: {
      page,
      size: 5,
      sort: "auditDate",
      direction: "desc",
      departmentId: departmentId || undefined,
    },
  });

  return response.data;
}


export async function createAudit(data) {
  const response = await api.post("/audits", data);
  return response.data;
}

export async function getAuditById(id) {
  const response = await api.get(`/audits/${id}`);
  return response.data;
}

export async function updateAudit(id, data) {
  const response = await api.put(`/audits/${id}`, data);
  return response.data;
}

export async function deleteAudit(id) {
  await api.delete(`/audits/${id}`);
}