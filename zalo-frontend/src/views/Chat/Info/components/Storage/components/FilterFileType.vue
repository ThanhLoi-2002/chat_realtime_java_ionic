<script setup lang="ts">
import { useTranslate } from '@/composables/useTranslate'
import { ref } from 'vue'
import PopoverButton from '@/components/Dropdown/PopoverButton.vue'
import { style } from '@/assets/tailwindcss'

const model = defineModel<string>()
const { t } = useTranslate()
const open = ref(false) // Biến này giờ sẽ điều khiển được PopoverButton

const fileTypes = [
    { value: 'pdf', label: 'PDF', iconClass: 'far fa-file-pdf text-red-500' },
    { value: 'doc', label: 'Word', iconClass: 'far fa-file-word text-blue-500' },
    { value: 'ppt', label: 'PowerPoint', iconClass: 'far fa-file-powerpoint text-orange-500' },
    { value: 'xlsx', label: 'Excel', iconClass: 'far fa-file-excel text-green-600' }
]

const getSelectedLabel = () => {
    const found = fileTypes.find(item => item.value === model.value)
    return found ? found.label : t('type')
}

function select(type: string) {
    model.value = type
    open.value = false // Lệnh này giờ đã có tác dụng đóng popup!
}

</script>

<template>
    <popover-button id="file-popover" :label="getSelectedLabel()" v-model:open="open" v-model="model" rounded="rounded-2xl">
        <div class="p-1 space-y-0.5" :class="[style.bg.secondary]">
            <div v-for="item in fileTypes" :key="item.value" class=""
                :class="[model === item.value && 'zalo-item-active', 'cursor-pointer p-1', style.bg.hover]"
                @click="select(item.value)">
                <div class="flex items-center gap-3 w-full py-1" :class="[style.text.primary]">
                    <i :class="item.iconClass" class="text-lg w-5 text-center"></i>
                    <span class="text-sm font-normal">{{ item.label }}</span>
                </div>
            </div>
        </div>
    </popover-button>
</template>