<template>
    <div v-if="users.size > 0"
        class="absolute bottom-[95%] left-2 text-sm text-gray-600 dark:text-gray-400 px-4 pb-2 flex items-center gap-1">
        <span>
            <span v-for="([id, user], index) in Array.from(users)" :key="id">
                <span v-if="index > 0">, </span>{{ user.username }}
            </span>
            {{ t("typing") }}
        </span>
        <span class="flex gap-1 ml-1">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
        </span>
    </div>
</template>

<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate';
const { t } = useTranslate();
defineProps<{ users: Map<number, any> }>();
</script>

<style scoped>
.dot {
    width: 4px;
    height: 4px;
    background-color: currentColor;
    border-radius: 50%;
    animation: bounce 1.4s infinite;
}

.dot:nth-child(2) {
    animation-delay: 0.2s;
}

.dot:nth-child(3) {
    animation-delay: 0.4s;
}

@keyframes bounce {

    0%,
    80%,
    100% {
        transform: scale(0);
        opacity: 0.3;
    }

    40% {
        transform: scale(1);
        opacity: 1;
    }
}
</style>