import { ACCESS_TOKEN, LANG } from "@/utils/constant";
import { getKey, setKey } from "@/utils/local";
import axiosClient from "axios";
import axios from "axios";

const instance = axiosClient.create({
    baseURL: import.meta.env.VITE_API_URL + '/api',
    withCredentials: false,
});

// --- FLAG tránh lặp vô hạn ---
let isRefreshing = false;
let queue: any[] = []; // để đợi refresh xong retry

// Add a request interceptor
instance.interceptors.request.use(
    async function (config: any) {
        const accessToken = getKey(ACCESS_TOKEN);

        if (accessToken)
            config.headers["Authorization"] = accessToken

        config.headers["Accept-Language"] = getKey(LANG) || "vi"
        return config;
    },
    function (error) {
        // Do something with request error
        return Promise.reject(error);
    }
);

// Add a response interceptor
instance.interceptors.response.use(
    function (response: any) {
        // Any status code that lie within the range of 2xx cause this function to trigger
        // Do something with response data
        return response.data;
    },
    async function (error) {
        // Any status codes that falls outside the range of 2xx cause this function to trigger
        // Do something with response error
        const originalRequest = error.config;
        if (error?.response?.status === 1 && !originalRequest._retry) {
            originalRequest._retry = true;

            // ------- Nếu đang refresh, đợi -------
            if (isRefreshing) {
                return new Promise((resolve) => {
                    queue.push((token: string) => {
                        originalRequest.headers.Authorization = `Bearer ${token}`;
                        resolve(instance(originalRequest));
                    });
                });
            }
            isRefreshing = true;

            // let refreshToken = getRefreshToken();

            try {
                const res = await axios.post(import.meta.env.VITE_API_URL + '/api/v1/auth/refresh-token', {
                    // refreshToken: refreshToken,
                });

                const newToken = res.data.data.token;
                setKey(ACCESS_TOKEN,newToken);

                // Update Authorization cho request đầu
                originalRequest.headers.Authorization = `Bearer ${newToken}`;

                // Xử lý các request đang chờ
                queue.forEach((cb) => cb(newToken));
                queue = [];
                isRefreshing = false;

                return instance(originalRequest);

            } catch (err) {
                isRefreshing = false;
                queue = [];
                // removeToken()
                window.location.href = '/'
                return Promise.reject(err);
            }
        }

        if (error?.response?.data) {
            return Promise.reject(error.response.data);
        }

        return Promise.reject(error);
    }
);

export default instance;
