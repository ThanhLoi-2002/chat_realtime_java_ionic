import router from "@/router";

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

export const goBack = () => {
  // Kiểm tra nếu có lịch sử trình duyệt thì back, không thì đẩy về trang chủ tránh bị kẹt
  if (window.history.length > 1) {
    router.back(); 
    // Hoặc router.go(-1);
  } else {
    router.push('/');
  }
};