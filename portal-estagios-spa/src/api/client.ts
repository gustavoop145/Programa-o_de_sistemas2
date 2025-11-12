import axios from "axios";

const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8081";

export const api = axios.create({ baseURL });

// adiciona token JWT nas requisições
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// trata erros de autenticação
api.interceptors.response.use(
  (res) => res,
  (err) => {
    const status = err?.response?.status;
    if (status === 401 || status === 403) {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("roles");
      location.assign("/login");
    }
    return Promise.reject(err);
  }
);
