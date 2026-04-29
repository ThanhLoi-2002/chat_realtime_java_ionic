<template>
    <div class="flex flex-col h-full bg-white dark:bg-gray-900 text-gray-800 dark:text-gray-100">
        <!-- BODY -->
        <div class="flex-1 overflow-hidden flex flex-col px-4 py-3 rounded-lg h-full">

            <!-- GROUP NAME -->
            <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-full bg-gray-200 dark:bg-gray-700 flex items-center justify-center">
                    <i class="fa fa-camera"></i>
                </div>

                <input :placeholder="t('enterGroupName') + '...'" class="flex-1 border-b bg-transparent outline-none
                 border-gray-300 dark:border-gray-600
                 pb-1" v-model="groupName" />
            </div>

            <!-- SEARCH -->
            <div class="mt-3">
                <input placeholder="Nhập tên, số điện thoại..." class="w-full px-4 py-2 rounded-full
                 bg-gray-100 dark:bg-gray-800
                 outline-none" v-model="keyword" />
            </div>

            <div class="py-2">
                <classification-chip />
            </div>

            <!-- LIST -->
            <div class="flex-1 mt-4 pr-1 h-full">

                <p class="text-sm text-gray-500 mb-2">{{ t("recentChat") }}</p>

                <div class="flex gap-2 h-full">
                    <!-- Recent users list: cố định chiều cao, scroll riêng -->
                    <div class="flex-1">
                        <div class="h-[80%] overflow-y-auto bg-white dark:bg-gray-900 rounded">
                            <div v-for="(group, letter) in groupedFriends" :key="letter">
                                <!-- LETTER -->
                                <div class="text-gray-500 dark:text-gray-400 text-xs md:text-sm mb-2 mt-4">
                                    {{ letter }}
                                </div>

                                <!-- USERS -->
                                <div v-for="conv in group" :key="conv.id"
                                    class="flex items-center gap-3 py-2 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800 rounded pl-2"
                                    @click="selectConv(conv)">
                                    <span
                                        class="w-4 h-4 rounded-full flex justify-center items-center border-slate-400 border"
                                        :class="isSelected(conv) ? 'bg-blue-400' : 'dark:bg-slate-600'"><i
                                            class="fa fa-check" v-show="isSelected(conv)" /></span>
                                    <circle-avatar :user="getRecipient(conv)" :is-disabled="true" size="size-10"/>
                                    <span class="truncate text-base">{{ getRecipient(conv)?.username }}</span>
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
                                <div class="flex gap-2 items-center">
                                    <circle-avatar :user="getRecipient(conv)" :is-disabled="true" size="size-6"/>
                                    <span class="truncate text-sm">{{ getRecipient(conv)?.username }}</span>
                                </div>
                                <i class="fa fa-close mr-2 dark:hover:text-slate-300 text-sm"
                                    @click="removeConv(conv)"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- FOOTER -->
        <div class="flex justify-end gap-3 px-4 py-3 border-t border-gray-200 dark:border-gray-700">
            <button
                class="px-4 py-2 bg-gray-200 hover:bg-slate-100 dark:hover:bg-slate-600 dark:bg-gray-700 rounded cursor-pointer"
                @click="dismiss">
                {{ t("cancel") }}
            </button>
            <ion-button class="rounded cursor-pointer" :disabled="selectedConvs.length < 2 || groupName.length == 0"
                @click="createGroup">
                {{ t("createGroup") }}
            </ion-button>
        </div>

    </div>
</template>

<script setup lang="ts">
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';
import { useConversation } from '@/composables/useConversation';
import { useTranslate } from '@/composables/useTranslate';
import { useClassificationCardStore } from '@/stores/classificationCard.storage';
import { useConversationStore } from '@/stores/conversation.storage';
import { useFriendshipStore } from '@/stores/friendship.storage';
import { ConversationType, UserType } from '@/types/entities';
import { normalizeText } from '@/utils/helper';
import { computed, inject, onMounted, ref } from 'vue';

const dismiss = inject<() => void>("modalDismiss")
const { t } = useTranslate()
const keyword = ref("");
const groupName = ref("");
const friendshipStorage = useFriendshipStore()
const convStorage = useConversationStore()
const classCardStorage = useClassificationCardStore()
const { getRecipient } = useConversation()

const convs = ref<ConversationType[]>([

])

const selectedConvs = ref<ConversationType[]>([
])

// filter
const filtered = computed(() => {
    const search = normalizeText(keyword.value).toLowerCase();
    const activeId = classCardStorage.activeCardId;

    return convs.value.filter((f) => {
        const name = getRecipient(f)?.username || getRecipient(f)?.phone || "";
        const matchesSearch = normalizeText(name).toLowerCase().includes(search);

        // 2. Lọc theo Tag (Classification Card)
        let matchesTag = true;
        if (activeId !== 0) { // Nếu activeId = 0 là "Tất cả"
            // Tìm card tương ứng trong store
            const currentCard = classCardStorage.cards.find(c => c.id === activeId);
            // Kiểm tra xem convId có nằm trong danh sách conversationIds của card đó không
            matchesTag = currentCard?.conversationIds?.includes(f.id) || false;
        }

        return matchesSearch && matchesTag;
    }
    );
}
);

const groupedFriends = computed(() => {
    const groups: any = {};

    filtered.value.forEach((c) => {
        const letter = getRecipient(c)?.username?.charAt(0).toUpperCase();

        if (letter) {
            if (!groups[letter]) groups[letter] = [];
            groups[letter].push(c);
        }
    });

    return groups;
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

const createGroup = async () => {
    const isSuccess = await convStorage.createGroup({
        name: groupName.value,
        participantIds: selectedConvs.value.map(c => getRecipient(c)?.id)
    })
    isSuccess && dismiss?.()
}

onMounted(async () => {
    // await friendshipStorage.getFriends()
    const userIds = friendshipStorage.friends.map(f => f.id)

    convs.value = await convStorage.findByUserIdsAndTypePrivate(userIds)
    classCardStorage.setActiveCard(0)
})
</script>