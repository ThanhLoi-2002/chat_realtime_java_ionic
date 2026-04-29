<template>

    <div class="bg-white dark:bg-gray-900 text-gray-900 dark:text-gray-100 h-full flex flex-col rounded-md">

        <!-- BODY -->
        <div class="flex-1 overflow-y-auto overflow-x-hidden">

            <!-- GROUP INFO -->
            <div class="flex flex-col items-center py-4 gap-1">
                <div class="flex w-full gap-4 ml-8">
                    <div class="relative group/avatar cursor-pointer" @click="triggerFileInput">
                        <div
                            class="w-20 h-20 rounded-full bg-gray-200 dark:bg-gray-700 flex items-center justify-center overflow-hidden border-2 border-transparent hover:border-blue-500 transition-all">
                            <LoadingSpinner v-if="isUploading" />
                            <div v-else>
                                <img v-if="props.conversation.avatar" :src="props.conversation.avatar.secureUrl"
                                    class="w-full h-full object-cover" />
                                <i v-else class="fas fa-users text-3xl text-gray-500 dark:text-gray-300"></i>

                                <div
                                    class="absolute inset-0 bg-black/20 flex items-center justify-center opacity-0 group-hover/avatar:opacity-100 transition-opacity">
                                    <i class="fas fa-camera text-white"></i>
                                </div>
                            </div>
                        </div>

                        <input type="file" ref="fileInput" class="hidden" accept="image/*"
                            @change="handleAvatarChange" />
                    </div>

                    <div class="flex items-center gap-2 mt-3 flex-1 mr-8">
                        <template v-if="!isEditingName">
                            <span class="font-medium truncate max-w-37.5">{{ props.conversation.name }}</span>
                            <i class="fas fa-pen text-sm text-gray-500 dark:text-gray-400 cursor-pointer hover:text-blue-500"
                                @click="startEditingName"></i>
                        </template>

                        <div v-else class="flex items-center gap-2 w-full">
                            <input ref="nameInput" v-model="tempName"
                                class="bg-gray-100 dark:bg-gray-800 border-b-2 border-blue-500 outline-none px-1 py-0.5 w-full text-sm"
                                @keyup.enter="saveName" @blur="saveName" />
                        </div>
                    </div>
                </div>

                <button v-if="isMember"
                    class="mt-3 bg-gray-200 dark:bg-gray-700 hover:bg-gray-300 dark:hover:bg-gray-600 px-6 py-2 rounded-sm text-sm cursor-pointer w-[90%] transition"
                    @click="goToMessage">
                    {{ t("message") }}
                </button>

                <button v-else
                    class="mt-3 bg-gray-200 dark:bg-gray-700 hover:bg-gray-300 dark:hover:bg-gray-600 px-6 py-2 rounded-sm text-sm cursor-pointer w-[90%] transition"
                    @click="requestToJoinTheGroup">
                    {{ t("requestToJoinTheGroup") }}
                </button>
            </div>

            <!-- MEMBERS -->
            <div class="border-t-2 border-gray-200 dark:border-gray-800 px-4 py-3">
                <div class="text-sm mb-2 text-gray-600 dark:text-gray-300">{{ t("member") }} ({{
                    conversation.members?.length }})</div>

                <div class="flex items-center -space-x-4">
                    <div class="flex -space-x-4">
                        <div v-for="user in conversation.members" :key="user.id" class="relative group">
                            <!-- AVATAR -->
                            <CircleAvatar :user="user" size="size-10" />

                            <!-- TOOLTIP -->
                            <div class="absolute bottom-full left-1/2 -translate-x-1/2 mb-2
                                px-2 py-1 text-xs rounded-md shadow-lg
                                bg-gray-900 text-white 
                                dark:bg-white dark:text-black
                                opacity-0 scale-90
                                group-hover:opacity-100 group-hover:scale-100
                                transition-all duration-200
                                whitespace-nowrap z-50 pointer-events-none">
                                {{ user.username }}
                            </div>
                        </div>
                    </div>

                    <!-- more -->
                    <div class="z-2 w-10 h-10 flex items-center justify-center rounded-full bg-gray-300 dark:bg-gray-700 border-2 border-white dark:border-gray-900 text-gray-700 dark:text-white cursor-pointer"
                        @click="() => emit('update:view', 'member')">
                        <i class="fas fa-ellipsis-h"></i>
                    </div>
                </div>
            </div>

            <!-- MEDIA -->
            <div v-if="isMember" class="flex flex-col gap-2 px-4 py-3 border-t-2 border-gray-200 dark:border-gray-800">
                <span>{{ t("image/video") }}</span>
                <div v-if="messStorage.images.length > 0">
                    <div class="grid grid-cols-4 gap-2">
                        <div v-for="(i, index) in messStorage.images.slice(0, 4)" :key="index"
                            class="aspect-square rounded bg-gray-200 dark:bg-slate-700 hover:scale-105 transition cursor-pointer"
                            @click="messStorage.setPreviewImage(i)">
                            <image-or-video :media="i" />
                        </div>
                    </div>
                </div>
                <div v-else class="px-4 py-6 text-center text-gray-500 text-wrap dark:text-gray-400 text-sm">
                    {{ t("noPhotoshavebeenshared") }}
                </div>
            </div>

            <!-- LINK -->
            <div class="border-t-2 border-gray-200 dark:border-gray-800 px-4 py-3">
                <div class="text-sm mb-2 text-gray-600 dark:text-gray-300">Link tham gia nhóm</div>

                <div class="flex items-center justify-between bg-gray-100 dark:bg-gray-800 p-2 rounded-md">
                    <span class="text-blue-500 dark:text-blue-400 text-sm truncate">
                        {{ inviteUrl }}
                    </span>

                    <div class="flex gap-3 text-gray-600 dark:text-gray-300">
                        <i class="fas fa-copy cursor-pointer hover:text-blue-500 transition-colors" @click="handleCopy"
                            title="Copy link"></i>

                        <i class="fas fa-external-link-alt cursor-pointer hover:text-blue-500 transition-colors"
                            @click="handleOpenTab" title="Mở trong tab mới"></i>

                        <i class="fas fa-share cursor-pointer hover:text-blue-500 transition-colors"
                            @click="handleShare" title="Chia sẻ"></i>
                    </div>
                </div>
                <collapse v-model:isOpen="openQrCode" :title="`${t('qrCode')}`" class="border-b-0! pt-4! pb-0!"
                    :class="[style.text.primary, 'text-sm px-0']">
                    <QrCode :code="conversation.inviteCode" />
                </collapse>
            </div>

            <!-- ACTIONS -->
            <div v-if="isMember" class="border-t-2 border-gray-200 dark:border-gray-800">
                <div class="px-4 py-3 flex items-center gap-3 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800">
                    <i class="fas fa-cog"></i>
                    <span>Quản lý nhóm</span>
                </div>

                <div
                    class="px-4 py-3 flex items-center gap-3 cursor-pointer text-red-500 hover:bg-gray-100 dark:hover:bg-gray-800">
                    <i class="fas fa-sign-out-alt"></i>
                    <span>Rời nhóm</span>
                </div>
            </div>

        </div>
    </div>

</template>
<script setup lang="ts">
import { style } from '@/assets/tailwindcss';
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';
import LoadingSpinner from '@/components/Loading/LoadingSpinner.vue';
import QrCode from '@/components/QrCode/QrCode.vue';
import { useTranslate } from '@/composables/useTranslate';
import { useUpload } from '@/composables/useUpload';
import { useConversationStore } from '@/stores/conversation.storage';
import { useMessageStore } from '@/stores/message.storage';
import { UploadFileType } from '@/types/common';
import { ConversationType } from '@/types/entities';
import { ModuleEnum, ResourceEnum } from '@/types/enum';
import { qrCodeUrl, ROUTE } from '@/utils/constant';
import { computed, inject, nextTick, ref } from 'vue';
import { useRouter } from 'vue-router';
import ImageOrVideo from '@/components/Media/ImageOrVideo.vue';

const props = defineProps<{
    conversation: ConversationType;
    isMember: boolean
}>();

const { t } = useTranslate()
const router = useRouter()
const convStorage = useConversationStore()
const messStorage = useMessageStore()
const openQrCode = ref(false)
const inviteUrl = `${qrCodeUrl}/${props.conversation.inviteCode}`
const isUploading = ref(false)
const { uploadFile, imageFolder } = useUpload()

const dismiss = inject<() => void>("modalDismiss")

const emit = defineEmits(['update:view'])

// --- LOGIC ĐỔI AVATAR ---
const fileInput = ref<HTMLInputElement | null>(null);

const triggerFileInput = () => {
    fileInput.value?.click();
};

const handleAvatarChange = async (event: Event) => {
    isUploading.value = true

    const target = event.target as HTMLInputElement;
    const file = target.files?.[0];
    if (file) {
        const dto: UploadFileType = {
            folder: imageFolder,
            file,
            resourceType: ResourceEnum.IMAGE
        }
        const media = await uploadFile(dto, ModuleEnum.CONVERSATION)

        if (media) {
            await convStorage.updateAvatar(media)
        }
    }

    isUploading.value = false
};

// --- LOGIC ĐỔI TÊN GROUP ---
const isEditingName = ref(false);
const tempName = ref('');
const nameInput = ref<HTMLInputElement | null>(null);

const startEditingName = () => {
    tempName.value = props.conversation.name || '';
    isEditingName.value = true;
    // Tự động focus vào input khi hiện ra
    nextTick(() => {
        nameInput.value?.focus();
    });
};

const saveName = async () => {
    if (!isEditingName.value) return;

    const oldName = props.conversation.name;
    const newName = tempName.value.trim();

    if (newName && newName !== oldName) {
        await convStorage.updateName(newName);
    }

    isEditingName.value = false;
};

const goToMessage = () => {
    convStorage.selectConversation(props.conversation)
    router.push(ROUTE.CHATS)
    dismiss?.()
}

const requestToJoinTheGroup = () => {
    convStorage.selectConversation(props.conversation)
    router.push(ROUTE.CHATS)
    dismiss?.()
}

// Hàm xử lý Copy
const handleCopy = async () => {
    try {
        await navigator.clipboard.writeText(inviteUrl);
        // alert('Đã sao chép liên kết vào bộ nhớ tạm!');
        // Thay alert bằng toast của bạn nếu có: toast.success('Copied!')
    } catch (err) {
        console.error('Không thể copy:', err);
    }
};

// Hàm mở Tab mới
const handleOpenTab = () => {
    window.open(inviteUrl, '_blank');
};

// Hàm xử lý Share (Ví dụ sử dụng Web Share API nếu trình duyệt hỗ trợ)
const handleShare = async () => {
    if (navigator.share) {
        try {
            await navigator.share({
                title: 'Tham gia nhóm Zalo',
                url: inviteUrl
            });
        } catch (err) {
            console.log('User cancelled sharing');
        }
    } else {
        // Fallback nếu trình duyệt không hỗ trợ Share API
        handleCopy();
    }
};
</script>