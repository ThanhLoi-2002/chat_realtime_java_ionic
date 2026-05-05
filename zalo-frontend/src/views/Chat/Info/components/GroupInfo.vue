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

                <!-- GROUP INFO -->
                <div :class="['py-4 overflow-y-auto', style.border.primary]">

                    <div class="flex flex-col items-center justify-center">
                        <group-avatar :conversation="conversationStorage.conversation!" size="w-20 h-20" />

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
                            :class="['p-4 border-b hover:bg-gray-50 dark:hover:bg-slate-800 cursor-pointer flex items-center', style.border.primary, style.text.secondary]"
                            @click="currentView = 'member'">
                            👥 {{ conversationStorage.conversation?.members?.length }} {{ t('member') }}
                        </section>

                        <StorageComponent @update:currentView="currentView = $event" />
                        <Security />

                        <div class="p-2">
                            <div v-if="!isGoldenKey()"
                                class="text-red-500 hover:text-red-600 cursor-pointer transition p-2 rounded-xs"
                                :class="[style.bg.hover]" @click="showConfirmLeaveGroup = !showConfirmLeaveGroup">
                                {{ t("leaveTheGroup") }}
                            </div>
                            <div v-else class="text-red-500 hover:text-red-600 cursor-pointer transition p-2 rounded-xs"
                                :class="[style.bg.hover]" @click="showConfirmDisbandGroup = !showConfirmDisbandGroup">
                                {{ t("disbandTheGroup") }}
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ================= MEMBERS VIEW ================= -->
            <template v-else-if="currentView == 'member'">
                <Member @back="currentView = 'info'" :type="currentView" :isShowBackButton="true" />
            </template>

            <!-- ================= IMAGE VIEW ================= -->
            <template v-else-if="currentView.startsWith('storage/')">
                <StoragePanel @back="currentView = 'info'" :type="currentView" />
            </template>
        </transition>

        <ConfirmModal v-model:showConfirm="showConfirmLeaveGroup" :onOk="onLeaveGroup" :message="t('leaveTheGroup')"
            :header="t('leaveTheGroup')" />

        <ConfirmModal v-model:showConfirm="showConfirmDisbandGroup" :onOk="onDisbandGroup"
            :message="t('disbandTheGroup')" :header="t('disbandTheGroup')" />
    </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useConversationStore } from '@/stores/conversation.storage'
import { useConversation } from '@/composables/useConversation'
import { useTranslate } from '@/composables/useTranslate'
import { style } from '@/assets/tailwindcss'
import GroupAvatar from '@/components/Avatar/GroupAvatar.vue'
import StoragePanel from './Storage/StoragePanel.vue'
import StorageComponent from './Storage/StorageComponent.vue'
import Member from './Member.vue'
import Security from './Security.vue'
import ConfirmModal from '@/components/Modal/ConfirmModal.vue'

const emit = defineEmits(['close'])

const conversationStorage = useConversationStore()
const { t } = useTranslate()
const { conversationName, isGoldenKey } = useConversation()
const currentView = ref<'info' | 'member' | 'storage/image' | 'storage/file' | 'storage/link' | any>('info')
const showConfirmLeaveGroup = ref(false)
const showConfirmDisbandGroup = ref(false)

/* ACTION */
const toggleMute = () => console.log('mute')
const togglePin = async () => {
    if (conversationStorage.conversation) {
        await conversationStorage.pin(conversationStorage.conversation.id)
    }
}
const createGroup = () => console.log('create group')
const onEditName = () => console.log('edit name')

const actions = computed(() => [
    {
        icon: 'fa fa-bell',
        title: 'turnOffNotifications',
        onClick: toggleMute
    },
    {
        icon: conversationStorage.conversation?.pinAt ? 'fas fa-unlink' : 'fas fa-thumbtack',
        title: 'conversationPins',
        onClick: togglePin
    },
    {
        icon: 'fa fa-users',
        title: 'createGroup',
        onClick: createGroup
    }
])

const onLeaveGroup = async () => {
    const success = await conversationStorage.leaveGroup();

    showConfirmLeaveGroup.value = false
}

const onDisbandGroup = async () => {
    const success = await conversationStorage.leaveGroup();

    showConfirmLeaveGroup.value = false
}
</script>