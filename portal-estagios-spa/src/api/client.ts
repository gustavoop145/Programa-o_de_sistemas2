import axios from "axios";

const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8081";

export const api = axios.create({ baseURL });

// manda o token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// se o token expirar ou não tiver permissão, manda pro login
api.interceptors.response.use(
  (res) => res,
  (err) => {
    const status = err?.response?.status;
    if (status === 401 || status === 403) {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("roles");
      // evita loop se já estiver no /login
      if (!location.pathname.startsWith("/login")) {
        location.assign("/login");
      }
    }
    return Promise.reject(err);
  }
);
