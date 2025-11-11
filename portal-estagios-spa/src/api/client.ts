import axios from "axios";

// 👉 pegue a URL da sua API do Spring Boot (Codespaces/Render/etc.)
// Dica: use variável de ambiente Vite para facilitar:
const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export const api = axios.create({ baseURL });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
