import { ACCESS_TOKEN, LANG, REFRESH_TOKEN } from "@/utils/constant";
import { deleteKey, getKey, setKey } from "@/utils/local";
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
        console.log(error?.response)
        const originalRequest = error.config;
        if (error?.response?.status === 401 && error?.response?.data.message == 'expiredToken' && !originalRequest._retry) {
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

            try {
                const res = await axios.post(import.meta.env.VITE_API_URL + '/api/auth/refresh', {
                    refreshToken: getKey(REFRESH_TOKEN),
                });
                
                console.log(res)
                const newToken = res.data.result.token;
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
                
                deleteKey(ACCESS_TOKEN)
                deleteKey(REFRESH_TOKEN)

                // window.location.href = '/'
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
