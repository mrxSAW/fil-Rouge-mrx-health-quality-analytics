export function saveSession(data) {
  localStorage.setItem("token", data.token);
  localStorage.setItem("userId", String(data.userId));
  localStorage.setItem("role", data.role);
}

export function getToken() {
  return localStorage.getItem("token");
}

export function getRole() {
  return localStorage.getItem("role");
}

export function clearSession() {
  localStorage.removeItem("token");
  localStorage.removeItem("userId");
  localStorage.removeItem("role");
}