import { IResponse } from "@/types/common";
import axios from "./axios";
import { LoginFormType, RegisterFormType } from "@/schema/auth.schema";


const login = async (data: LoginFormType) => {
    return await axios.post<IResponse<{ token: string }>>(`/auth/login`, data);
}

const register = async (data: RegisterFormType) => {
    return await axios.post<IResponse<{ token: string }>>(`/auth/register`, data);
}


export const authApi = {
    login, register
}