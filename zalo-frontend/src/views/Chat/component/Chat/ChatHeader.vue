<template>
    <header class="h-16 flex items-center justify-between px-6
          bg-white dark:bg-gray-900
          border-b border-gray-200 dark:border-slate-500">

        <div class="flex items-center justify-between gap-3 w-full">
            <div class="flex items-cente gap-3">
                <!-- Back button (mobile only) -->
                <MobileBackButton :onClick="onBack" />

                <GroupAvatar v-if="isGroup(convStorage.conversation)"
                    :conversation="convStorage.conversation!" />
                <circle-avatar v-else :user="getRecipient(convStorage.conversation)" size="size-10" />

                <div class="flex flex-col">
                    <span class="font-medium dark:text-slate-200">
                        {{ conversationName(convStorage.conversation!) }}
                    </span>

                    <div class="flex gap-2 items-center">
                        <span class="text-xs rounded-full w-2.5 h-2.5"
                            :class="[isOnline ? 'bg-green-400' : 'bg-gray-400']"
                            v-if="!isGroup(convStorage.conversation!)">
                        </span>
                        <div v-else class="flex gap-2 items-center">
                            <i class="fa fa-user text-xs" :class="[style.text.secondary]" />
                            <span class="text-xs hover:text-blue-400 cursor-pointer" :class="[style.text.secondary]"
                                @click="() => memberModal?.present()">
                                {{ convStorage.conversation?.members?.length }} {{ t("members") }}
                            </span>
                        </div>
                        <div class="border-l border-gray-300 dark:border-slate-500 h-3"></div>

                        <ClassificationCard v-if="convStorage.conversation" :isChatHeader="true"
                            :conv="convStorage.conversation" id="tag-menu-trigger">
                            <span :class="[style.text.muted, 'text-sm']">{{
                                getClassCard(convStorage.conversation?.id)?.name }}</span>
                        </ClassificationCard>
                    </div>
                </div>
            </div>

            <div>
                <i class="fas fa-toggle-off text-2xl cursor-pointer dark:text-white" v-if="!isShowInfoSection"
                    @click="emit('update:isShowInfoSection', !isShowInfoSection)"></i>
                <i class="fas fa-toggle-on text-2xl text-blue-500 cursor-pointer" v-if="isShowInfoSection"
                    @click="emit('update:isShowInfoSection', !isShowInfoSection)"></i>
            </div>
        </div>

        <Modal ref="memberModal" title="">
            <Member :isShowBackButton="false" />
        </Modal>

        <ion-popover ref="popoverRef" trigger="tag-menu-trigger" trigger-action="click" :dismiss-on-select="false"
            class="custom-popover">
            <div class="dark:text-gray-200 w-56 py-2 shadow-xl border border-gray-500 dark:border-gray-700 rounded-lg"
                :class="[style.bg.primary]">
                <div v-for="tag in classificationCardStorage.cards" :key="tag.id" @click="assignClassCard(tag.id, convStorage.conversation?.id)"
                    class="flex items-center justify-between px-4 py-2.5 hover:bg-gray-300/30 dark:hover:bg-gray-700 cursor-pointer transition-colors">

                    <ClassificationCard :color="tag.color">
                        <span class="text-[14px]">{{ tag.name }}</span>
                    </ClassificationCard>

                    <i class="fa fa-check" v-if="getClassCard(convStorage.conversation?.id)?.color == tag.color"/>
                </div>

                <div class="h-px bg-gray-400 dark:bg-gray-700 m-2"></div>

                <div @click="openManager"
                    class="px-4 py-2 dark:text-slate-200 hover:bg-gray-300/30 dark:hover:bg-gray-700 cursor-pointer text-[14px] text-nowrap">
                    {{ t("classificationTagManagement") }}
                </div>
            </div>
        </ion-popover>

        <modal ref="classificationTagRef" :title="t(pageModal)" :go-back="() => goPage('classificationTagManagement')"
            :isDisplayBackButton="pageModal != Object.keys(pages)[0]">
            <transition name="slide" mode="out-in">
                <component :is="pages[pageModal]" :key="pageModal" :goPage="goPage" />
            </transition>
        </modal>
    </header>
</template>
<script setup lang="ts">
import MobileBackButton from '@/components/Button/MobileBackButton.vue';
import { useConversation } from '@/composables/useConversation';
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { useSystemStore } from '@/stores/system.storage';
import GroupAvatar from '@/components/Avatar/GroupAvatar.vue';
import { style } from '@/assets/tailwindcss';
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';
import Modal from '@/components/Modal/Modal.vue';
import Member from '../../Info/components/Member.vue';
import { onMounted, ref, watch } from 'vue';
import { StompSubscription } from '@stomp/stompjs';
import { socketSubscribe, sockJSSendMessage } from '@/utils/websocket';
import ClassificationCards from '@/views/ClassificationCard/ClassificationCards.vue';
import AddOrUpdateClassificationCard from '@/views/ClassificationCard/AddOrUpdateClassificationCard.vue';
import { useClassificationCardStore } from '@/stores/classificationCard.storage';
import { useClassCard } from '@/composables/useClassCard';
import ClassificationCard from '@/components/Classification/ClassificationCard.vue';

const props = defineProps<{
    isShowInfoSection: boolean
}>()

const convStorage = useConversationStore()
const systemStorage = useSystemStore()
const { isGroup, getRecipient, conversationName } = useConversation()
const { t } = useTranslate()
const memberModal = ref()
const isOnline = ref(false)
const popoverRef = ref<any>(null);
const classificationCardStorage = useClassificationCardStore()
const classificationTagRef = ref<any>(null)
const { getClassCard } = useClassCard()

const pageModal = ref<ComponentKey>("classificationTagManagement")
const pages = {
    classificationTagManagement: ClassificationCards,
    detail: AddOrUpdateClassificationCard
}

type ComponentKey = keyof typeof pages;

const openManager = () => {
    goPage('classificationTagManagement')
    popoverRef.value?.$el.dismiss();
    classificationTagRef.value?.present()
};

const goPage = (page: ComponentKey) => {
    pageModal.value = page
    console.log(pageModal.value)
}

let sub: StompSubscription | undefined

const emit = defineEmits(['update:isShowInfoSection'])

const onBack = () => {
    convStorage.selectConversation()
    systemStorage.setShowBottomMenu(true)
}

const resetSubscribe = () => {
    sub = socketSubscribe(`/topic/user.status.${getRecipient(convStorage.conversation)?.id}`, (msg: any) => {
        isOnline.value = JSON.parse(msg.body).online
        console.log(JSON.parse(msg.body))
    })
}

const emitCheckUserOnline = () => {
    sockJSSendMessage({
        recipientId: getRecipient(convStorage.conversation)?.id,
    }, "check.userOnline")
}

const assignClassCard = (classCardId: number, convId?: number) => {
    if(convId){
        const type = getClassCard(convStorage.conversation?.id)?.id == classCardId ? "remove" : "add";
        classificationCardStorage.assignConvToCard(classCardId, convId, type)
    }
}

onMounted(() => {
    resetSubscribe()
    emitCheckUserOnline()
})

watch(() => convStorage.conversation?.id, async () => {
    sub?.unsubscribe()
    resetSubscribe()
    emitCheckUserOnline()
})
</script>

<style>
/* Ghi đè biến của Ionic để dùng background tùy chỉnh từ Tailwind */
.custom-popover {
    --background: transparent;
    --box-shadow: none;
}

/* Loại bỏ padding mặc định của ion-content bên trong popover nếu có */
ion-popover::part(content) {
    border-radius: 8px;
    overflow: hidden;
}
</style>