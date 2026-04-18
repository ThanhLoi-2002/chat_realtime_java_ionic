<template>
    <div class='w-full h-full'>
        <!-- SEARCH -->
        <div class="p-3">
            <div class="flex items-center bg-[#374151] rounded-full px-3 py-2">
                <i class="fa fa-search text-gray-400 mr-2"></i>
                <input placeholder="Tìm kiếm link"
                    class="bg-transparent outline-none text-sm w-full placeholder-gray-400" />
            </div>
        </div>

        <!-- FILTER -->
        <div class="flex gap-2 px-3 pb-2">
            <select v-model="filterUser" class="bg-[#374151] text-xs px-3 py-1 rounded-full outline-none">
                <option value="">Người gửi</option>
                <option value="me">Tôi</option>
                <option value="other">Người khác</option>
            </select>

            <select v-model="filterDate" class="bg-[#374151] text-xs px-3 py-1 rounded-full outline-none">
                <option value="">Ngày gửi</option>
                <option value="today">Hôm nay</option>
                <option value="week">7 ngày</option>
                <option value="month">30 ngày</option>
            </select>
        </div>

        <!-- LIST -->
        <div class="flex-1 overflow-y-auto px-3 pb-4 space-y-4 w-full h-[80%]" ref="scrollRef" @scroll="handleScroll">

            <div v-for="group in groupedMedia" :key="group.date">

                <!-- DATE -->
                <div class="text-gray-400 text-sm font-semibold mb-2">
                    {{ group.date }}
                </div>

                <!-- ITEMS -->
                <div class="space-y-1">
                    <LinkContainer v-for="item in group.items" :key="item.id" :message="item" />
                </div>

            </div>

        </div>
    </div>
</template>
<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useMedia } from '@/composables/useMedia';
import { useMessageStore } from '@/stores/message.storage';
import { useConversationStore } from '@/stores/conversation.storage';
import { MessageFilter } from '@/types/common';
import { MessageEnum } from '@/types/enum';
import LinkContainer from './LinkContainer.vue';
import { appLimit } from '@/utils/constant';

const { groupedMediaByTime } = useMedia();
const messStorage = useMessageStore();
const convStorage = useConversationStore();
const scrollRef = ref<HTMLElement | null>(null);

const filterUser = ref('')
const filterDate = ref('')

const groupedMedia = computed(() => {
    return groupedMediaByTime(filtered.value);
});

const filtered = computed(() => {
    return messStorage.links.filter((item) => {
        // filter user
        // if (filterUser.value === 'me' && !item.isMe) return false
        // if (filterUser.value === 'other' && item.isMe) return false

        // filter date
        const now = new Date();
        const itemDate = new Date(item.ct);

        if (filterDate.value === "today") {
            if (itemDate.toDateString() !== now.toDateString()) return false;
        }

        if (filterDate.value === "week") {
            const diff = (now.getTime() - itemDate.getTime()) / (1000 * 60 * 60 * 24);
            if (diff > 7) return false;
        }

        if (filterDate.value === "month") {
            const diff = (now.getTime() - itemDate.getTime()) / (1000 * 60 * 60 * 24);
            if (diff > 30) return false;
        }

        return true;
    });
});

const fetchLinkMessages = () => {
    if (!messStorage.linksHasMore) return;

    const lastId = messStorage.links.at(-1)?.id ?? undefined;
    const options: MessageFilter = {
        conversationId: convStorage.conversation!.id,
        limit: appLimit.links,
        lastId,
        contentType: MessageEnum.TEXT,
        linkMetadata: true
    };

    messStorage.getLinkMessages(options);
};

// Xử lý sự kiện cuộn
const handleScroll = () => {
    // Vì đây là danh sách Media (dạng Grid), thường người dùng cuộn XUỐNG để xem ảnh cũ hơn
    // Bạn cần kiểm tra xem scroll container đã gần tới ĐÁY chưa
    const el = scrollRef.value;
    if (!el) return;

    // Logic: Nếu khoảng cách tới đáy < 150px thì tải thêm
    const isBottom = el.scrollHeight - el.scrollTop <= el.clientHeight + 150;
    if (isBottom) {
        fetchLinkMessages();
    }
};

onMounted(() => {
    fetchLinkMessages();
});
</script>
