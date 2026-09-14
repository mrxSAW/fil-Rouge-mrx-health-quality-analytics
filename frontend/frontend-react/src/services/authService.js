import api from "../api/axiosConfig";
import { saveSession } from "../utils/session";

export async function login(email, password) {
  const response = await api.post("/auth/login", {
    email: email.trim(),
    password,
  });

  saveSession(response.data);

  return response.data;
}


export async function register(data) {
  const response = await api.post("/auth/register", data);
  return response.data;
}