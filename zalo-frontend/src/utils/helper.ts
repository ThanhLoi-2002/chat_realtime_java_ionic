import router from "@/router";
import { SelectOptionType } from "@/types/common";

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

export const enumToOptions = (
  enumObj: Record<string, string>,
): SelectOptionType[] => {
  return Object.values(enumObj).map((value) => ({
    label: value,
    value,
  }));
}

export const formatCurrencyVND = (
    value: number | string | null | undefined,
): string => {
    if (value === null || value === undefined || value === '') {
        return '';
    }

    const amount = Number(value);

    if (!Number.isFinite(amount)) {
        return '';
    }

    return new Intl.NumberFormat('vi-VN', {
        style: 'decimal',
        maximumFractionDigits: 0,
    }).format(amount);
};