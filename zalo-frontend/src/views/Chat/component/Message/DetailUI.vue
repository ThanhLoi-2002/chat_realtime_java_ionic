<template>
    <!-- Main Wrapper: Tự động thích ứng Dark/Light Mode -->
    <div
        class="bg-white dark:bg-[#1e2329] text-gray-900 dark:text-white h-full flex flex-col font-sans transition-colors duration-300">

        <div class="overflow-y-auto flex-1 custom-scrollbar">
            <!-- Section 1: Xem lại nội dung tin nhắn -->
            <div class="p-4 flex justify-end">
                <div
                    class="bg-blue-50 dark:bg-[#2d3a4b] py-2 px-4 rounded-xl max-w-[85%] shadow-sm border border-blue-100 dark:border-gray-600">
                    <p class="text-blue-600 dark:text-gray-400 text-xs font-semibold mb-2">
                        {{ message.sender?.username || t('you') }}
                    </p>
                    <template v-if="message.contentType == MessageEnum.TEXT">
                        <ReplyingMessage v-if="message.replyToMessage" :replying-message="message.replyToMessage" />
                        <p class="text-sm leading-relaxed" v-html="formattedContent"></p>
                    </template>

                    <image-or-video :media="message.attachments[0]" v-if="message.contentType == MessageEnum.IMAGE"/>
                    <FileContainer :media="message.attachments[0]" :is-show-action="false" v-if="message.contentType == MessageEnum.FILE"/>
                    <div class="flex justify-end mt-2">
                        <span class="text-[12px] text-gray-500 dark:text-gray-400">
                            {{ formatDate(message.ct) }} • {{ formatTime(message.ct) }}
                        </span>
                    </div>
                </div>
            </div>

            <hr class="border-gray-200 dark:border-gray-800 mx-4" />

            <!-- Section 2: Trạng thái chi tiết -->
            <div class="p-4 space-y-8">
                <!-- Render từng nhóm trạng thái nếu có user -->
                <div v-for="group in statusGroups" :key="group.status">
                    <div v-if="group.users.length > 0">
                        <span class="text-sm font-bold mb-4 flex items-center gap-2">
                            {{ group.label }} ({{ group.users.length }})
                            <span :class="group.dotClass" class="w-2 h-2 rounded-full"></span>
                        </span>

                        <div class="grid grid-cols-2 gap-y-5">
                            <div v-for="stt in group.users" :key="stt.id"
                                class="flex items-center gap-3 cursor-pointer">
                                <CircleAvatar :user="stt.user"
                                    class="w-10 h-10 border border-gray-100 dark:border-gray-700" />
                                <span class="text-sm font-mediu" :class="[style.text.secondary]">
                                    {{ stt.user.username }}
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Footer Ghi chú -->
        <div class="p-4 mt-auto border-t border-gray-100 dark:border-transparent">
            <p class="text-[11px] text-gray-400 dark:text-gray-500 italic text-center leading-5">
                (*) Thông tin chi tiết "Đã xem", "Đã nhận" chỉ hiển thị với nhóm không quá 100 người
            </p>
        </div>
    </div>
</template>

<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';
import { useDateTime } from '@/composables/useDateTime';
import { useMessage } from '@/composables/useMessage';
import { useTranslate } from '@/composables/useTranslate';
import { useMessageStore } from '@/stores/message.storage';
import { MessageStatusType, MessageType } from '@/types/entities';
import { MessageEnum } from '@/types/enum';
import { onMounted, ref, computed } from 'vue';
import ReplyingMessage from './ReplyingMessage.vue';
import FileContainer from '../../Info/components/Storage/FileContainer.vue';

const props = defineProps<{
    message: MessageType,
}>();

const allStatuses = ref<MessageStatusType[]>([]);
const messStorage = useMessageStore();
const { formatTime, formatDate } = useDateTime();
const { t } = useTranslate()
const { formattedContentWithTag } = useMessage()

const formattedContent = computed(() => {
    if (!props.message.content) return "";
    return formattedContentWithTag(props.message.content, false);
});

// Phân loại dữ liệu theo trạng thái từ Enum
const statusGroups = computed(() => {
    return [
        {
            status: 'READ',
            label: t('seen'),
            dotClass: 'bg-blue-500',
            users: allStatuses.value.filter(s => s.status === 'READ')
        },
        // {
        //     status: 'DELIVERED',
        //     label: t('delivered'),
        //     dotClass: 'bg-green-500',
        //     users: allStatuses.value.filter(s => s.status === 'DELIVERED')
        // },
        {
            status: 'SENT',
            label: t('sent'),
            dotClass: 'bg-gray-400',
            users: allStatuses.value.filter(s => s.status === 'SENT')
        }
    ];
});

onMounted(async () => {
    // Lấy dữ liệu từ storage/api
    allStatuses.value = await messStorage.getDetails(props.message.id, props.message.conversationId);
});
</script>