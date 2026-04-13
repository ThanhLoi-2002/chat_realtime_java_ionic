<template>
    <div
        class="min-h-screen bg-slate-100 dark:bg-slate-900 flex flex-col items-center justify-between font-sans text-slate-800 dark:text-slate-200 transition-colors duration-300">

        <header class="w-full max-w-6xl flex justify-between items-center p-6">
            <div class="text-blue-600 dark:text-blue-400 font-bold text-2xl tracking-tight">Zalo</div>

            <div class="flex items-center gap-4">
                <theme-toggle />
                <lang-dropdown />
            </div>
        </header>

        <main v-if="group"
            class="bg-white dark:bg-slate-800 shadow-xl dark:shadow-2xl rounded-xl p-8 md:p-12 w-[90%] max-w-4xl flex flex-col md:flex-row items-center md:items-start gap-12 border border-slate-200 dark:border-slate-700 transition-colors">

            <div class="flex-1 flex flex-col items-center md:items-start text-center md:text-left">
                <div class="flex flex-col md:flex-row items-start gap-4">
                    <group-avatar :conversation="group" :is-disabled="true" size="w-20 h-20" />
                    <div class="flex flex-col gap-3">
                        <span class="text-2xl font-bold text-slate-900 dark:text-white">{{ group.name }}</span>
                        <p v-if="group.type == ConversationEnum.COMMUNITY" class="text-slate-500 dark:text-slate-400">{{
                            t('community') }}</p>
                            
                        <button @click="join"
                            class="cursor-pointer w-fit bg-[#0068ff] hover:bg-blue-700 text-white font-semibold py-2.5 px-8 rounded-lg flex items-center gap-2 transition-all shadow-lg shadow-blue-200 dark:shadow-none mb-8">
                            <i class="fas fa-comment"></i>
                            {{ t('join') }}
                        </button>
                    </div>
                </div>

                <div class="space-y-4 text-sm md:text-base leading-relaxed">
                    <h3 class="font-bold text-lg border-b dark:border-slate-700 pb-2">{{ t('description') }}
                    </h3>
                    {{ group.description }}
                </div>
            </div>

            <div
                class="flex flex-col items-center gap-4 border-l-0 md:border-l border-slate-100 dark:border-slate-700 md:pl-12">
                <div
                    class="p-3 bg-white dark:bg-slate-200 border-2 border-slate-100 dark:border-transparent rounded-2xl shadow-sm">
                    <div class="w-48 h-48 bg-slate-50 flex items-center justify-center relative">
                        <qr-code :code="group.inviteCode"/>
                        <!-- <div class="absolute inset-0 flex items-center justify-center">
                            <div class="bg-white p-1 rounded-lg shadow-md border border-slate-100">
                                <span class="text-blue-600 font-bold text-[10px]">Zalo</span>
                            </div>
                        </div> -->
                    </div>
                </div>
            </div>
        </main>

        <footer class="w-full p-8 text-center text-xs text-slate-400 dark:text-slate-600">
            <!-- © Copyright 2021 Zalo Group. All right Reserved. -->
        </footer>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useTranslate } from '@/composables/useTranslate'
import { useRoute, useRouter } from 'vue-router'
import LangDropdown from '@/components/Dropdown/LangDropdown.vue'
import ThemeToggle from '@/components/Toggle/ThemeToggle.vue'
import { useConversationStore } from '@/stores/conversation.storage'
import { ConversationType } from '@/types/entities'
import GroupAvatar from '@/components/Avatar/GroupAvatar.vue'
import { ROUTE } from '@/utils/constant'
import { ConversationEnum } from '@/types/enum'
import QrCode from '@/components/QrCode/QrCode.vue'

const { t } = useTranslate()
const convStorage = useConversationStore()
const route = useRoute()
const router = useRouter()
const group = ref<any>()

const fetchGroup = async () => {
    const conv = await convStorage.fetchGroupByCode(route.params.code as string)
    console.log(conv)
    if (!conv) router.push(ROUTE.CHATS)
    else group.value = conv
}

// Theo dõi hệ thống để tự bật Dark Mode nếu người dùng đang để chế độ tối ở OS
onMounted(async () => {
    await fetchGroup()
})

const join = async () => {
    console.log("Tham gia với mã:", route.params.code)
    // Thực hiện logic redirect hoặc API call ở đây
}
</script>