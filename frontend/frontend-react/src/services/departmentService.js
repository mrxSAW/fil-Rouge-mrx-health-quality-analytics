import api from "../api/axiosConfig";

export async function getAllDepartments() {
  let departments = [];
  let page = 0;
  let lastPage = false;

  while (!lastPage) {
    const response = await api.get("/departments", {
      params: {
        page,
        size: 100,
      },
    });

    departments = departments.concat(response.data.content);
    lastPage = response.data.last;
    page++;
  }

  return departments;
}


export async function getDepartments(page) {
  const response = await api.get("/departments", {
    params: {
      page,
      size: 5,
    },
  });

  return response.data;
}

export async function createDepartment(data) {
  const response = await api.post("/departments", data);
  return response.data;
}


export async function updateDepartment(id, data) {
  const response = await api.put(`/departments/${id}`, data);
  return response.data;
}

export async function deleteDepartment(id) {
  await api.delete(`/departments/${id}`);
}