export const getKey = (key: string) => {
    return localStorage.getItem(key);
};

export const setKey = (key: string, value: any) => {
    if (typeof value !== 'string') {
        value = JSON.stringify(value)
    }

    localStorage.setItem(key, value);
};

export const deleteKey = (key: string) => {
    localStorage.removeItem(key)
};