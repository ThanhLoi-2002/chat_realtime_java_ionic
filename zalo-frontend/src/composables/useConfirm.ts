// stores/confirm.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useConfirmStore = defineStore('confirm', () => {
    const show = ref(false);
    const title = ref('');
    const message = ref('');
    const onOkCallback = ref<(() => void) | null>(null);

    // Hàm để gọi Modal từ bất cứ đâu
    const open = (options: { title: string; message: string; onOk: () => void }) => {
        title.value = options.title;
        message.value = options.message;
        onOkCallback.value = options.onOk;
        show.value = true;
    };

    const confirm = () => {
        if (onOkCallback.value) onOkCallback.value();
        show.value = false;
    };

    return { show, title, message, open, confirm };
});