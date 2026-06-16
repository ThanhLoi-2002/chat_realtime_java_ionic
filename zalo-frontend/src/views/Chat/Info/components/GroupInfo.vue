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
                        <Collapse v-model:isOpen="openMember" :title="t('member')"> 
                            <div :class="['hover:bg-gray-50 dark:hover:bg-slate-800 py-2 px-4 cursor-pointer flex gap-2 items-center', style.border.primary, style.text.secondary]"
                                @click="currentView = 'member'">
                                <i class="fas fa-users"></i>
                                <span>{{ conversationStorage.conversation?.members?.length }} {{ t('member') }}</span>
                            </div>
                            <div v-if="isAdmin()" :class="['hover:bg-gray-50 dark:hover:bg-slate-800 py-2 px-4 cursor-pointer flex gap-2 items-center', style.border.primary, style.text.secondary]"
                                @click="currentView = 'approveMember'">
                                <i class="fas fa-pray"></i>
                                <span>{{ joinRequestStorage.joinGroupRequests?.length }} {{ t('joinRequestList') }}</span>
                            </div>
                        </Collapse>

                        <Collapse v-model:isOpen="openCommunityBulletinBoard" :title="t('communityBulletinBoard')">
                            <div :class="['hover:bg-gray-50 dark:hover:bg-slate-800 py-2 px-4 cursor-pointer flex gap-2 items-center', style.border.primary, style.text.secondary]"
                                @click="currentView = 'pin'">
                                <i class="fas fa-map-pin"></i>
                                <span>{{ t('pin') }}</span>
                            </div>
                        </Collapse>

                        <StorageComponent @update:currentView="currentView = $event" />
                        <Security />

                        <div class="p-2">
                            <div v-if="!isGoldenKey()"
                                class="text-red-500 hover:text-red-600 cursor-pointer transition p-2 rounded-xs"
                                :class="[style.bg.hover]" @click="showConfirmLeaveGroup">
                                {{ t("leaveTheGroup") }}
                            </div>
                            <div v-else class="text-red-500 hover:text-red-600 cursor-pointer transition p-2 rounded-xs"
                                :class="[style.bg.hover]" @click="showConfirmDisbandGroup">
                                {{ t("disbandTheGroup") }}
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ================= MEMBERS VIEW ================= -->
            <template v-else-if="currentView == 'member'">
                <Member @back="currentView = 'info'" :isShowBackButton="true" />
            </template>

            <!-- ================= IMAGE VIEW ================= -->
            <template v-else-if="currentView.startsWith('storage/')">
                <StoragePanel @back="currentView = 'info'" :type="currentView" />
            </template>

            <!-- ================= Pin VIEW ================= -->
            <template v-else-if="currentView.startsWith('pin')">
                <PinPanel @back="closePins" />
            </template>

            <!-- ================= GroupManagement VIEW ================= -->
            <template v-else-if="currentView.startsWith('groupManagement')">
                <GroupManagementPanel @back="currentView = 'info'" />
            </template>

            <!-- ================= Approva member VIEW ================= -->
            <template v-else-if="currentView.startsWith('approveMember')">
                <ApproveMember @back="currentView = 'info'" :isShowBackButton="true"/>
            </template>
        </transition>
    </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useConversationStore } from '@/stores/App/conversation.storage.ts'
import { useConversation } from '@/composables/useConversation'
import { useTranslate } from '@/composables/useTranslate'
import { style } from '@/assets/tailwindcss'
import GroupAvatar from '@/components/Shared/Avatar/GroupAvatar.vue'
import StoragePanel from './Storage/StoragePanel.vue'
import StorageComponent from './Storage/StorageComponent.vue'
import Member from './Member.vue'
import Security from './Security.vue'
import Collapse from '@/components/Shared/collapse/Collapse.vue'
import PinPanel from '../../component/Pin/PinPanel.vue'
import { useSystemStore } from '@/stores/App/system.storage.ts'
import GroupManagementPanel from './GroupManagement/GroupManagementPanel.vue'
import { useConfirmStore } from '@/composables/useConfirm'
import ApproveMember from './ApproveMember.vue'
import { useJoinGroupStore } from '@/stores/App/joinGroupRequest.storage.ts'

const emit = defineEmits(['close'])

const conversationStorage = useConversationStore()
const { t } = useTranslate()
const { conversationName, isGoldenKey, isAdmin } = useConversation()
const currentView = ref<'info' | 'member' | 'storage/image' | 'storage/file' | 'storage/link' | 'pin' | 'groupManagement' | 'approveMember' | any>('info')
const openMember = ref(true)
const openCommunityBulletinBoard = ref(true)
const systemStorage = useSystemStore()
const confirmStore = useConfirmStore();
const joinRequestStorage = useJoinGroupStore()

/* ACTION */
const toggleMute = () => console.log('mute')
const togglePin = async () => {
    if (conversationStorage.conversation) {
        await conversationStorage.pin(conversationStorage.conversation.id)
    }
}

const showConfirmLeaveGroup = () => {
    confirmStore.open({
        title: t('leaveTheGroup'),
        message: t('leaveTheGroup'),
        onOk: async () => {
            await conversationStorage.leaveGroup()
        }
    });
}

const showConfirmDisbandGroup = () => {
    confirmStore.open({
        title: t('disbandTheGroup'),
        message: t('disbandTheGroup'),
        onOk: async () => {
            await conversationStorage.leaveGroup()
        }
    });
}

const groupManagement = () => {
    currentView.value = 'groupManagement'
}

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
        icon: 'fa fa-cogs',
        title: 'groupManagement',
        onClick: groupManagement
    }
])

const closePins = () => {
    currentView.value = 'info'
    systemStorage.setIsOpenPins(false)
}

watch(() => systemStorage.isOpenPins, () => {
    if (systemStorage.isOpenPins) currentView.value = 'pin'
}, { immediate: true })
</script>