import axios from "axios";
import { clearSession, getToken } from "../utils/session";

const api = axios.create({
  baseURL: "/api",
});

api.interceptors.request.use((config) => {
  const token = getToken();

  const isAuthRequest =
    config.url === "/auth/login" || config.url === "/auth/register";

  if (token && !isAuthRequest) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const isLoginRequest = error.config?.url === "/auth/login";

    if (status === 401 && !isLoginRequest) {
      clearSession();

      if (window.location.pathname !== "/login") {
        window.location.href = "/login";
      }
    }

    return Promise.reject(error);
  }
);

export default api;