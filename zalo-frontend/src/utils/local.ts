export const getKey = (key: string) => {
    return localStorage.getItem(key);
};

export const setKey = (key: string, value: any) => {
    localStorage.setItem(key, value);
};

export const deleteKey = (key: string) => {
    localStorage.removeItem(key)
};