<template>
    <div class="h-full flex flex-col bg-white dark:bg-[#0f172a] text-sm overflow-auto">
        <transition name="slide">
            <div v-if="currentView == 'info'">
                <!-- HEADER -->
                <div class="p-4 flex items-center gap-2 font-semibold 
                border-b border-gray-200 dark:border-slate-700">
                    <div class="sm:hidden">
                        <mobile-back-button :onClick="() => emit('close')" />
                    </div>
                    <span :class="['text-center w-full', style.text.secondary]">
                        {{ t("conversationInfo") }}
                    </span>
                </div>

                <!-- USER INFO -->
                <div :class="['py-4 overflow-y-auto', style.border.primary]">

                    <div class="flex flex-col items-center justify-center">
                        <circle-avatar :user="getRecipient(conversationStorage.conversation)" size="size-20"/>

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
                            :class="['p-4 border-b hover:bg-gray-50 dark:hover:bg-slate-800 cursor-pointer', style.border.primary, style.text.secondary]"
                            @click="currentView = 'commonGroup'">
                            👥 {{ conversationStorage.generalGroup?.length }} {{ t('generalGroup') }}
                        </section>

                        <StorageComponent @update:currentView="currentView = $event"/>
                        <Security/>
                    </div>
                </div>
            </div>

            <!-- ================= COMMON GROUP VIEW ================= -->
            <template v-else-if="currentView === 'commonGroup'">
                <CommonGroupPanel @back="currentView = 'info'" />
            </template>

            <!-- ================= IMAGE VIEW ================= -->
            <template v-else-if="currentView.startsWith('storage/')">
                <StoragePanel @back="currentView = 'info'" :type="currentView"/>
            </template>
        </transition>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useConversationStore } from '@/stores/conversation.storage'
import { useConversation } from '@/composables/useConversation'
import { useTranslate } from '@/composables/useTranslate'
import { style } from '@/assets/tailwindcss'
import CommonGroupPanel from './CommonGroupPanel.vue'
import StoragePanel from './Storage/StoragePanel.vue'
import StorageComponent from './Storage/StorageComponent.vue'
import Security from './Security.vue'

const emit = defineEmits(['close'])

const conversationStorage = useConversationStore()
const { t } = useTranslate()
const { conversationName, getRecipient } = useConversation()
const currentView = ref<'info' | 'commonGroup' | 'storage/image' | 'storage/file' | 'storage/link' | any>('info')

/* ACTION */
const toggleMute = () => console.log('mute')
const togglePin = () => console.log('pin')
const createGroup = () => console.log('create group')
const onEditName = () => console.log('edit name')

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