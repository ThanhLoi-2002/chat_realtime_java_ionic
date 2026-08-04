<template>
    <aside :class="[oaStyle.bg.primary, 'w-95 p-6 flex flex-col items-center justify-center overflow-y-auto']">
        <!-- Khung điện thoại -->
        <div
            :class="[oaStyle.bg.primary, oaStyle.border.secondary, 'w-75 h-145 rounded-[40px] border-6 shadow-xl overflow-hidden flex flex-col relative']">

            <!-- Status Bar điện thoại giả lập -->
            <div
                :class="[oaStyle.text.primary, oaStyle.bg.secondary, 'px-4 py-1 text-[10px] flex justify-between items-center']">
                <span>10:07</span>
                <div class="flex space-x-1.5 items-center">
                    <i class="fas fa-signal text-[8px]"></i>
                    <i class="fas fa-wifi text-[8px]"></i>
                    <i class="fas fa-battery-full text-[8px]"></i>
                </div>
            </div>

            <div class="h-full flex flex-col overflow-y-auto" style="scrollbar-width: none;">
                <slot />
            </div>

        </div>
    </aside>
</template>
<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';


</script>