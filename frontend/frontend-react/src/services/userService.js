import api from "../api/axiosConfig";

export async function getUsers(page) {
  const response = await api.get("/users", {
    params: {
      page,
      size: 5,
    },
  });

  return response.data;
}


export async function getUserById(id) {
  const response = await api.get(`/users/${id}`);
  return response.data;
}

export async function updateUser(id, data) {
  const response = await api.put(`/users/${id}`, data);
  return response.data;
}


export async function updateUserRole(id, role) {
  const response = await api.patch(`/users/${id}/role`, {
    role,
  });

  return response.data;
}


export async function deleteUser(id) {
  await api.delete(`/users/${id}`);
}


export async function getMyProfile() {
  const response = await api.get("/users/me");

  return response.data;
}