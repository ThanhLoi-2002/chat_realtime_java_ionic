<template>
    <Transition name="slide">
        <div v-if="visible" :class="[oaStyle.bg.primary, 'fixed inset-0 flex flex-col']">
            <!-- Header -->
            <div :class="[oaStyle.border.secondary, oaStyle.text.primary, 'h-14 border-b flex items-center px-4']">
                <div :class="['flex-1 text-md']">
                    {{ title }}
                </div>

                <button @click="dismiss" class="w-10">
                    <i class="fas fa-times"></i>
                </button>
            </div>

            <!-- Body -->
            <div class="flex-1 overflow-auto">
                <slot />
            </div>
        </div>
    </Transition>
</template>

<script setup lang="ts">
import { oaStyle } from "@/assets/tailwindcss";
import { ref } from "vue";

defineProps<{
    title?: string
    showBack?: boolean
}>();

const emit = defineEmits<{
    (e: "close"): void
    (e: "back"): void
    (e: "present"): void
}>();

const visible = ref(false);

const present = () => {
    visible.value = true;
    emit("present");
};

const dismiss = () => {
    visible.value = false;
    emit("close");
};

defineExpose({
    present,
    dismiss
});
</script>