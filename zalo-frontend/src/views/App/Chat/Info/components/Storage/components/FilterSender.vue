<script setup lang="ts">
import { style } from '@/assets/tailwindcss'
import CircleAvatar from '@/components/Shared/Avatar/CircleAvatar.vue';
import Search from '@/components/Shared/Search/Search.vue';
import { useTranslate } from '@/composables/useTranslate'
import { UserType } from '@/types/entities';
import { computed, ref } from 'vue'

const props = defineProps<{
    users: UserType[]
}>()
const model = defineModel<number>()
const { t } = useTranslate()
const open = ref(false)

const keyword = ref('')

const filteredUsers = computed(() => {
    if (!keyword.value) return props.users

    return props.users.filter(user =>
        user.username
            .toLowerCase()
            .includes(keyword.value.toLowerCase())
    )
})

const getSelectedLabel = () => {
    const found = props.users.find(item => item.id == model.value)
    return found ? found.username : t('sender')
}

function select(senderId: number) {
    model.value = senderId
    open.value = false // Lệnh này giờ đã có tác dụng đóng popup!
}
</script>

<template>
    <popover-button id="sender-popover" :label="getSelectedLabel()" v-model:open="open" v-model="model"
        rounded="rounded-2xl">
        <div class="p-1 space-y-2 flex flex-col max-h-90" :class="[style.bg.secondary]">
            <search v-model="keyword" :placeholder="t('search')" rounded="rounded-2xl" height="h-6" text-size="text-sm"
                icon-left="left-2" icon-right="right-1.5" pxContent="px-7" />

            <div class="flex-1 overflow-y-auto">
                <div v-for="item in filteredUsers" :key="item.id"
                    :class="[model === item.id ? 'bg-blue-500/50' : `${style.bg.hover} `, 'cursor-pointer p-1']"
                    @click="select(item.id)">
                    <div class="flex items-center gap-2 w-full py-0" :class="[style.text.primary]">
                        <circle-avatar :user="item" size="size-7" />
                        <span class="text-sm font-normal">{{ item.username }}</span>
                    </div>
                </div>
            </div>
        </div>
    </popover-button>
</template>