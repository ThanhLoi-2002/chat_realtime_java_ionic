<template>
    <div class="flex flex-col h-full bg-white dark:bg-gray-900 text-gray-800 dark:text-gray-100">
        <!-- BODY -->
        <div class="flex-1 overflow-hidden flex flex-col px-4 py-3 rounded-lg h-full">

            <!-- SEARCH -->
            <div class="">
                <input placeholder="Nhập tên, số điện thoại..." class="w-full px-4 py-2 rounded-md
                 bg-gray-100 dark:bg-gray-800
                 outline-none" v-model="keyword" />
            </div>

            <div class="py-1">
                <classification-chip />
            </div>

            <!-- TAB NAVIGATION -->
            <div class="flex items-center justify-between border-b border-gray-200 dark:border-gray-700 relative">
                <div class="flex gap-6 text-sm font-medium">
                    <button @click="activeTab = TabEnum.ALL"
                        :class="[activeTab === TabEnum.ALL ? 'text-blue-500 border-b-2 border-blue-500' : 'text-gray-500']"
                        class="pb-2 transition-all cursor-pointer">
                        {{ t(TabEnum.ALL) }}
                    </button>
                    <button @click="activeTab = TabEnum.GROUP"
                        :class="[activeTab === TabEnum.GROUP ? 'text-blue-500 border-b-2 border-blue-500' : 'text-gray-500']"
                        class="pb-2 transition-all cursor-pointer">
                        {{ t(TabEnum.GROUP) }}
                    </button>
                    <button @click="activeTab = TabEnum.FRIEND"
                        :class="[activeTab === TabEnum.FRIEND ? 'text-blue-500 border-b-2 border-blue-500' : 'text-gray-500']"
                        class="pb-2 transition-all cursor-pointer">
                        {{ t(TabEnum.FRIEND) }}
                    </button>
                </div>
            </div>

            <!-- LIST -->
            <div class="flex-1 mt-4 pr-1 h-1/3">
                <div class="flex gap-2 h-full">
                    <!-- Recent users list: cố định chiều cao, scroll riêng -->
                    <div class="flex-1">
                        <!-- Sử dụng displayConvs đã được lọc theo Tab và search -->
                        <div class="h-full overflow-y-auto bg-white dark:bg-gray-900 rounded">

                            <div v-if="activeTab == TabEnum.ALL">
                                <!-- Item Hội thoại -->
                                <div v-for="conv in displayConvs" :key="conv.id"
                                    class="flex items-center gap-3 py-2 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800 rounded pl-2 transition-colors"
                                    @click="selectConv(conv)">

                                    <!-- Checkbox custom -->
                                    <span
                                        class="w-5 h-5 rounded border-gray-400 border flex justify-center items-center transition-colors"
                                        :class="isSelected(conv) ? 'bg-blue-500 border-blue-500' : 'dark:bg-transparent'">
                                        <i class="fa fa-check text-white text-[10px]" v-show="isSelected(conv)" />
                                    </span>

                                    <circle-avatar v-if="!isGroup(conv)" :user="getRecipient(conv)" :is-disabled="true"
                                        size="size-10" />
                                    <GroupAvatar v-else :conversation="conv" :is-disabled="true" size="size-10" />
                                    <span class="truncate text-sm">{{ isGroup(conv) ? conv.name :
                                        getRecipient(conv)?.username }}</span>
                                </div>
                            </div>

                            <div v-else>
                                <!-- Logic Grouped: truyền đúng Enum tùy theo tab đang đứng -->
                                <div v-for="(group, letter) in grouped(displayConvs, activeTab === TabEnum.GROUP ? ConversationEnum.GROUP : ConversationEnum.PRIVATE)"
                                    :key="letter">

                                    <!-- Header Chữ cái (A, B, C...) -->
                                    <div class="text-gray-500 dark:text-gray-400 text-xs font-bold mb-2 mt-4 px-2">
                                        {{ letter }}
                                    </div>

                                    <!-- Item Hội thoại -->
                                    <div v-for="conv in group" :key="conv.id"
                                        class="flex items-center gap-3 py-2 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800 rounded pl-2 transition-colors"
                                        @click="selectConv(conv)">

                                        <!-- Checkbox custom -->
                                        <span
                                            class="w-5 h-5 rounded border-gray-400 border flex justify-center items-center transition-colors"
                                            :class="isSelected(conv) ? 'bg-blue-500 border-blue-500' : 'dark:bg-transparent'">
                                            <i class="fa fa-check text-white text-[10px]" v-show="isSelected(conv)" />
                                        </span>

                                        <circle-avatar v-if="!isGroup(conv)" :user="getRecipient(conv)"
                                            :is-disabled="true" size="size-10" />
                                        <GroupAvatar v-else :conversation="conv" :is-disabled="true" size="size-10" />
                                        <span class="truncate text-sm">{{ isGroup(conv) ? conv.name :
                                            getRecipient(conv)?.username }}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Selected users list: cũng có scroll riêng; ẩn nếu rỗng -->
                    <div v-if="selectedConvs.length > 0" class="w-40">
                        <span class="dark:text-slate-400 text-sm">{{ t('selected') }} {{ selectedConvs.length
                            }}/100</span>
                        <div class="h-[85%] overflow-y-auto bg-white dark:bg-gray-900 rounded flex flex-col gap-2 mt-2">
                            <div v-for="conv in selectedConvs" :key="conv.id"
                                class="flex items-center justify-between gap-2 py-1 cursor-pointer bg-blue-500 rounded-full px-2">
                                <div class="flex gap-2 items-center min-w-0">
                                    <circle-avatar v-if="!isGroup(conv)" :user="getRecipient(conv)" :is-disabled="true"
                                        size="size-6" />
                                    <GroupAvatar v-else :conversation="conv" :is-disabled="true" size="size-6" />
                                    <span class="truncate text-sm">{{ isGroup(conv) ? conv.name :
                                        getRecipient(conv)?.username }}</span>
                                </div>
                                <i class="fa fa-close mr-2 dark:hover:text-slate-300 text-sm"
                                    @click="removeConv(conv)"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- PREVIEW & INPUT MESSAGE -->
            <div class="mt-4 pt-2 border-t border-gray-200 dark:border-gray-700">
                <!-- Message Preview Card -->
                <div class="bg-gray-50 dark:bg-gray-800/50 px-3 py-2 rounded-lg mb-1.5 border dark:border-gray-700">
                    <div class="flex gap-3 items-center">
                        <!-- Preview ảnh nếu là tin nhắn hình ảnh -->
                        <div v-if="message.contentType === MessageEnum.IMAGE"
                            class="size-12 rounded overflow-hidden shrink-0">
                            <img :src="message.attachments[0].secureUrl" class="w-full h-full object-cover" />
                        </div>
                        <div class="min-w-0">
                            <p class="text-xs text-gray-500 dark:text-gray-400 font-medium">
                                {{ message.contentType == MessageEnum.IMAGE ? t('shareImages') : message.contentType == MessageEnum.FILE ? t('shareFiles') : t('shareMessages') }}
                            </p>

                            <FileContainer :media="message.attachments[0]" :isShowAction="false" v-if="message.contentType == MessageEnum.FILE"/>
                            <p v-if="isAttachDesc && message.contentType == MessageEnum.IMAGE"
                                class="text-sm truncate dark:text-gray-200"
                                v-html="formattedContentWithTag(message.content, message.senderId == userStorage.user?.id)" />

                            <p v-if="message.contentType != MessageEnum.IMAGE"
                                class="text-sm truncate dark:text-gray-200"
                                v-html="formattedContentWithTag(message.content, message.senderId == userStorage.user?.id)" />
                        </div>
                    </div>

                    <!-- Toggle đính kèm mô tả (Optional) -->
                    <div v-if="message.contentType == MessageEnum.IMAGE && message.content"
                        class="mt-2 flex items-center gap-2 border-t dark:border-gray-700 pt-2">
                        <Switch v-model="isAttachDesc" :label="t('attachDescription')" />
                    </div>
                </div>

                <!-- Input -->
                <textarea v-model="shareNote" rows="1" placeholder="Nhập tin nhắn..."
                    class="w-full bg-transparent border border-gray-300 dark:border-gray-700 rounded-lg p-4 text-sm outline-none focus:border-blue-500 transition-colors resize-none" />
            </div>
        </div>

        <!-- FOOTER -->
        <div class="flex justify-end gap-3 px-4 py-2 border-t border-gray-200 dark:border-gray-700">
            <button
                class="px-2 py-1 bg-gray-200 hover:bg-slate-100 dark:hover:bg-slate-600 dark:bg-gray-700 rounded cursor-pointer"
                @click="dismiss">
                {{ t("cancel") }}
            </button>
            <ion-button class="rounded cursor-pointer normal-case" :disabled="selectedConvs.length < 1 || isLoading"
                @click="onShare">
                <span v-if="!isLoading">{{ t("share") }}</span>
                <LoadingSpinner v-else />
            </ion-button>
        </div>
    </div>
</template>
<script setup lang="ts">
import GroupAvatar from '@/components/Avatar/GroupAvatar.vue';
import LoadingSpinner from '@/components/Loading/LoadingSpinner.vue';
import Switch from '@/components/Switch/switch.vue';
import { useConversation } from '@/composables/useConversation';
import { useMessage } from '@/composables/useMessage';
import { useTranslate } from '@/composables/useTranslate';
import { useClassificationCardStore } from '@/stores/classificationCard.storage';
import { useConversationStore } from '@/stores/conversation.storage';
import { useFriendshipStore } from '@/stores/friendship.storage';
import { useMessageStore } from '@/stores/message.storage';
import { useUserStore } from '@/stores/user.storage';
import { ShareMessageType } from '@/types/common';
import { ConversationType, MessageType } from '@/types/entities';
import { ConversationEnum, MessageEnum } from '@/types/enum';
import { normalizeText } from '@/utils/helper';
import { computed, inject, onMounted, ref } from 'vue';
import FileContainer from '../../Info/components/Storage/FileContainer.vue';

const props = defineProps<{
    message: MessageType
}>()

// Khai báo các loại tab
enum TabEnum {
    ALL = 'all',
    GROUP = 'group',
    FRIEND = 'friend'
}

const activeTab = ref(TabEnum.ALL);

const dismiss = inject<() => void>("modalDismiss")
const { t } = useTranslate()
const keyword = ref("");
const friendshipStorage = useFriendshipStore()
const convStorage = useConversationStore()
const userStorage = useUserStore()
const classCardStorage = useClassificationCardStore()
const messStorage = useMessageStore()
const { getRecipient, isGroup } = useConversation()
const { formattedContentWithTag } = useMessage()

const convs = ref<ConversationType[]>([
])

const selectedConvs = ref<ConversationType[]>([
])

const shareNote = ref("");
const isAttachDesc = ref(false)
const isLoading = ref(false)

const displayConvs = computed(() => {
    switch (activeTab.value) {
        case TabEnum.GROUP: {
            return convs.value.filter(c => c.type === ConversationEnum.GROUP);
        }

        case TabEnum.FRIEND: {
            // Khai báo biến bên trong khối { } này sẽ không bị lỗi lexical
            const friendIds = new Set(friendshipStorage.friends.map(f => f.id));
            console.log(friendIds)
            const filteredFriends = convs.value.filter(c => {
                const rid = getRecipient(c)?.id;
                return c.type === ConversationEnum.PRIVATE && rid !== undefined && friendIds.has(rid);
            });
            return filteredFriends;
        }

        case TabEnum.ALL:
        default: {
            return filtered.value(convs.value);
        }
    }
});

// filter
const filtered = computed(() => {
    return (list: ConversationType[]) => {
        const search = normalizeText(keyword.value).toLowerCase();
        const activeId = classCardStorage.activeCardId;

        return list.filter((f) => {
            const name = getRecipient(f)?.username || getRecipient(f)?.phone || "";
            const matchesSearch = normalizeText(name).toLowerCase().includes(search);

            let matchesTag = true;
            if (activeId !== 0) {
                const currentCard = classCardStorage.cards.find(c => c.id === activeId);
                matchesTag = currentCard?.conversationIds?.includes(f.id) || false;
            }

            return matchesSearch && matchesTag;
        });
    };
});

const grouped = computed(() => {
    return (rawList: ConversationType[], type: ConversationEnum.PRIVATE | ConversationEnum.GROUP) => {
        const groups: any = {};

        const filteredList = filtered.value(rawList);

        // Bước 2: Nhóm danh sách đã lọc
        filteredList.forEach((c) => {
            let letter = '';
            if (type === ConversationEnum.PRIVATE) {
                letter = getRecipient(c)?.username?.charAt(0).toUpperCase() || '#';
            } else {
                letter = c.name?.charAt(0).toUpperCase() || '#';
            }

            if (!groups[letter]) groups[letter] = [];
            groups[letter].push(c);
        });

        // Sắp xếp key A-Z nếu cần
        return Object.keys(groups).sort().reduce((acc, key) => {
            acc[key] = groups[key];
            return acc;
        }, {} as any);
    };
});

const isSelected = (user: ConversationType) => {
    return selectedConvs.value.some(u => u.id === user.id)
}

const selectConv = (user: ConversationType) => {
    const exists = isSelected(user)
    if (!exists) {
        selectedConvs.value.push(user)
    } else {
        removeConv(user)
    }
}

const removeConv = (user: ConversationType) => {
    selectedConvs.value = selectedConvs.value.filter(u => u.id !== user.id)
}

const onShare = async () => {
    isLoading.value = true
    const convIds = selectedConvs.value.map(c => c.id);
    const payload: ShareMessageType = {
        conversationIds: convIds,
        messageId: props.message.id,
        isAttachDesc: isAttachDesc.value,
        content: shareNote.value,
        conversationId: props.message.conversationId
    };

    console.log("Sharing to:", payload);
    await messStorage.shareMessage(payload)

    isLoading.value = false
    dismiss?.()
}

onMounted(async () => {
    convs.value = await convStorage.getAllConversations()
    classCardStorage.setActiveCard(0)
})
</script>