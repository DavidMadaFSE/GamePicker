import axios, { AxiosError } from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080/api"
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (axios.isAxiosError(error)) {
            const status = error.response?.status;
            const serverMessage = error.response?.data?.message || error.response?.statusText;
            
            // If 403 (Forbidden/Unauthorized), token is likely expired
            if (status === 403) {
                localStorage.removeItem("token");
            }
            
            if (status) {
                error.message = `Error ${status}: ${serverMessage ?? error.message}`;
                (error as AxiosError & { status?: number }).status = status;
            }
        }
        return Promise.reject(error);
    }
);
export default api;