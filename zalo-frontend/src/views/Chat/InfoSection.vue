<template>
    <div class="h-full flex flex-col bg-white dark:bg-[#0f172a] text-sm">

        <!-- HEADER -->
        <div class="p-4 flex items-center gap-2 font-semibold 
                border-b border-gray-200 dark:border-slate-700">
            <div class="sm:hidden">
                <mobile-back-button :onClick="() => emit('update:isShowInfoSection', false)" />
            </div>
            <span :class="['text-center w-full', style.text.secondary]">
                {{ t("conversationInfo") }}
            </span>
        </div>

        <!-- USER INFO -->
        <div :class="['py-4 border-b overflow-y-auto', style.border.primary]">

            <div class="flex flex-col items-center justify-center">
                <circle-avatar :user="getRecipient(conversationStorage.conversation)" size="size-20" v-if="!isGroup(conversationStorage.conversation)"/>
                <group-avatar :conversation="conversationStorage.conversation!" size="w-16 h-16" v-else/>

                <div class="mt-3 font-medium dark:text-white gap-2">
                    {{ conversationName(conversationStorage.conversation) }}

                    <!-- EDIT -->
                    <i class="fa fa-pencil text-gray-400 hover:text-blue-500 cursor-pointer transition"
                        @click="onEditName"></i>
                </div>
            </div>

            <!-- ACTIONS -->
            <div class="flex justify-center gap-8 mt-4 text-xs text-center overflow-auto">
                <div v-for="(item, index) in actions" :key="index"
                    class="group cursor-pointer flex flex-col items-center gap-1" @click="item.onClick">
                    <div class="w-10 h-10 flex items-center justify-center rounded-full
                      bg-gray-200 dark:bg-slate-700
                      group-hover:bg-gray-300 dark:group-hover:bg-slate-600 transition">
                        <i :class="['text-yellow-500', item.icon]" />
                    </div>
                    <span :class="['group-hover:text-black dark:group-hover:text-white', style.text.secondary]">
                        {{ t(item.title) }}
                    </span>
                </div>
            </div>


            <!-- CONTENT -->
            <div class="flex-1 mt-2">
                <!-- REMINDER -->
                <section
                    :class="['p-4 border-b hover:bg-gray-50 dark:hover:bg-slate-800 cursor-pointer', style.border.primary, style.text.secondary]">
                    {{ t('listReminders') }}
                </section>

                <!-- COMMON GROUP -->
                <section
                    :class="['p-4 border-b hover:bg-gray-50 dark:hover:bg-slate-800 cursor-pointer', style.border.primary, style.text.secondary]">
                    👥 3 {{ t('generalGroup') }}
                </section>

                <!-- MEDIA -->
                <collapse v-model:isOpen="open.media" :title="t('Ảnh/Video')">
                    <div class="grid grid-cols-3 gap-2">
                        <div v-for="i in 6" :key="i"
                            class="aspect-square rounded bg-gray-200 dark:bg-slate-700 hover:scale-105 transition cursor-pointer" />
                    </div>

                    <ion-button class="btn mx-auto w-full normal-case">
                        {{ t("seeAll") }}
                    </ion-button>
                </collapse>

                <!-- FILE -->
                <collapse v-model:isOpen="open.file" :title="t('File')">
                    <div class="flex items-center gap-3 p-2 rounded
                   bg-gray-100 dark:bg-slate-800
                   hover:bg-gray-200 dark:hover:bg-slate-700
                   cursor-pointer transition" @click="openFile">
                        <div class="w-10 h-10 bg-purple-500 rounded flex items-center justify-center text-white">
                            ▶
                        </div>

                        <div class="flex-1">
                            <div class="truncate dark:text-white">Record_2025.mp4</div>
                            <div class="text-xs text-gray-400">3.78 MB</div>
                        </div>

                        <div class="text-xs text-gray-400">26/12/2025</div>
                    </div>

                    <ion-button class="btn mx-auto w-full normal-case">{{ t("seeAll") }}</ion-button>
                </collapse>

                <!-- LINK -->
                <section class="p-4 border-b border-gray-200 dark:border-slate-700">
                    <div class="font-medium mb-2 dark:text-white">{{ t("link") }}</div>
                    <div class="text-gray-400 text-sm">
                        {{ t("noLinkShared") }}
                    </div>
                </section>

                <!-- SECURITY -->
                <collapse v-model:isOpen="open.security" :title="t('setUpSecurity')">
                    <div>
                        <div class="dark:text-white">{{ t('messagesDeleteThemselves') }}</div>
                        <div class="text-xs text-gray-400">{{ t('never') }}</div>
                    </div>

                    <!-- SWITCH -->
                    <div class="flex justify-between items-center">
                        <span class="dark:text-white">{{ t('hideChat') }}</span>

                        <button @click="isHidden = !isHidden"
                            class="w-10 h-6 flex items-center rounded-full p-1 transition"
                            :class="isHidden ? 'bg-blue-500' : 'bg-gray-300 dark:bg-slate-600'">
                            <div class="w-4 h-4 bg-white rounded-full shadow transition"
                                :class="isHidden ? 'translate-x-4' : ''" />
                        </button>
                    </div>

                    <div class="text-red-500 hover:text-red-600 cursor-pointer transition" @click="deleteConversation">
                        {{ t('deleteChatHistory') }}
                    </div>
                </collapse>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useConversationStore } from '@/stores/conversation.storage'
import MobileBackButton from '@/components/Button/MobileBackButton.vue'
import { useConversation } from '@/composables/useConversation'
import { useTranslate } from '@/composables/useTranslate'
import { style } from '@/assets/tailwindcss'
import Collapse from '@/components/collapse/Collapse.vue'
import GroupAvatar from '@/components/Avatar/GroupAvatar.vue'

const emit = defineEmits(['update:isShowInfoSection'])

const conversationStorage = useConversationStore()
const { t } = useTranslate()
const { conversationAvatar, conversationName, isGroup, getRecipient } = useConversation()

/* STATE */
const isHidden = ref(false)

const open = reactive({
    media: true,
    file: true,
    security: true
})

/* ACTION */
const toggleMute = () => console.log('mute')
const togglePin = () => console.log('pin')
const createGroup = () => console.log('create group')
const onEditName = () => console.log('edit name')
const openFile = () => console.log('open file')
const deleteConversation = () => console.log('delete')

const actions = [
    {
        icon: 'fa fa-bell',
        title: 'turnOffNotifications',
        onClick: toggleMute
    },
    {
        icon: 'fas fa-thumbtack',
        title: 'conversationPins',
        onClick: togglePin
    },
    {
        icon: 'fa fa-users',
        title: 'createGroup',
        onClick: createGroup
    }
]
</script>