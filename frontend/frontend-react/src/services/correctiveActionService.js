import api from "../api/axiosConfig";

export async function getCorrectiveActions(page, overdueOnly) {
  let url = "/corrective-actions";

  if (overdueOnly) {
    url = "/corrective-actions/overdue";
  }

  const response = await api.get(url, {
    params: {
      page,
      size: 5,
      sort: "deadline",
      direction: "asc",
    },
  });

  return response.data;
}


export async function getCorrectiveActionById(id) {
  const response = await api.get(`/corrective-actions/${id}`);
  return response.data;
}




export async function updateCorrectiveActionStatus(id, status) {
  const response = await api.patch(`/corrective-actions/${id}/status`, {
    status,
  });

  return response.data;
}



export async function createCorrectiveAction(data) {
  const response = await api.post("/corrective-actions", data);
  return response.data;
}


export async function getResponsibleUsers() {
  let users = [];
  let page = 0;
  let lastPage = false;

  while (!lastPage) {
    const response = await api.get("/corrective-actions/responsible-users", {
      params: { page, size: 100, },
    });

    users = users.concat(response.data.content);
    lastPage = response.data.last;
    page++;
  }

  return users;
}



export async function updateCorrectiveAction(id, data) {
  const response = await api.put(`/corrective-actions/${id}`, data);
  return response.data;
}
