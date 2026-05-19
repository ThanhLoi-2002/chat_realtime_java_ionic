export const normalizeText = (str: string) => {
    return str
        .normalize("NFD") // tách dấu
        .replace(/[\u0300-\u036f]/g, "") // xoá dấu
        .replace(/đ/g, "d")
        .replace(/Đ/g, "D")
        .toLowerCase()
}

export const capitalize = (text: string): string => {
  if (!text) return '';
  return text.charAt(0).toUpperCase() + text.slice(1);
};