import { formatCurrencyVND } from "@/utils/helper";
import { computed, MaybeRefOrGetter, toValue } from "vue";

export function useFormatPrice() {

    const formattedPrice = (price: MaybeRefOrGetter<number | string | undefined>) =>
        computed({
            get: () => formatCurrencyVND(toValue(price)),
            set: (value: string) => {
                const raw = value.replace(/\D/g, '');
                // Chỉ format để hiển thị, không thay đổi giá trị gốc
            },
        });
    return {
        formattedPrice
    }
}