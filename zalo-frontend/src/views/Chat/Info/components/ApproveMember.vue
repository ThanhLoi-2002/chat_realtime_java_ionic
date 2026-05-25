<template>
    <div class="h-full flex flex-col overflow-auto selection:none">
        <!-- HEADER -->
        <div class="p-4 flex items-center gap-3 border-b dark:border-slate-700" :class="[style.text.secondary]">
            <i class="fa fa-arrow-left cursor-pointer" @click="$emit('back')" v-if="isShowBackButton"></i>
            <span class="font-semibold m-auto">{{ t("approveMember") }}</span>
        </div>

        <!-- SUB HEADER & SELECT ALL -->
        <div class="px-3 flex items-center justify-between py-2 border-b dark:border-slate-800">
            <div class="text-sm font-medium flex items-center gap-2" :class="[style.text.primary]">
                <span>{{ t("joinRequestList") }} ({{ joinRequestStorage.joinGroupRequests?.length || 0 }})</span>
                <span v-if="selectedIds.length > 0" class="text-xs text-blue-500 font-normal">
                    (Đã chọn {{ selectedIds.length }})
                </span>
            </div>

            <!-- NÚT CHỌN TẤT CẢ -->
            <button v-if="joinRequestStorage.joinGroupRequests?.length" @click="toggleSelectAll"
                class="text-xs text-blue-500 cursor-pointer font-medium">
                {{ isAllSelected ? t("unselectAll") : t("selectAll") }}
            </button>
        </div>

        <!-- LIST -->
        <div class="flex-1 overflow-y-auto px-2 space-y-1 py-2">
            <div v-for="request in joinRequestStorage.joinGroupRequests" :key="request.id"
                @click="toggleSelect(request.id)"
                class="flex items-center gap-3 p-2 rounded-lg transition cursor-pointer hover:bg-slate-100 dark:hover:bg-slate-800"
                :class="[
                    style.text.secondary,
                    selectedIds.includes(request.id) ? 'bg-blue-50/50 dark:bg-blue-950/30' : ''
                ]">
                <!-- AVATAR -->
                <CircleAvatar :user="request.createdBy" />

                <!-- NAME -->
                <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-1.5">
                        <div class="truncate text-sm font-medium">
                            {{ request.createdBy?.username }}
                        </div>
                    </div>

                    <div v-if="request.message" class="text-xs opacity-60 text-wrap" :class="[style.text.secondary]">
                        {{ request.message }}
                    </div>
                </div>

                <!-- CHECKBOX (Dùng .stop để tránh trigger click 2 lần nếu bấm trúng ô input) -->
                <input type="checkbox" :checked="selectedIds.includes(request.id)"
                    @click.stop="toggleSelect(request.id)" class="cursor-pointer accent-blue-500 w-4 h-4" />
            </div>
        </div>

        <!-- ACTION -->
        <div class="p-2 border-t dark:border-slate-700 bg-white dark:bg-slate-900 flex gap-2">
            <button class="w-full text-white text-sm font-medium py-2 rounded-md transition duration-200" :class="[
                selectedIds.length > 0
                    ? 'bg-blue-500 hover:bg-blue-600 cursor-pointer'
                    : 'bg-slate-300 dark:bg-slate-700 cursor-not-allowed opacity-50'
            ]" :disabled="selectedIds.length === 0" @click="onConfirmApprove">
                {{ t("approve") }} ({{ selectedIds.length }})
            </button>

            <button class="w-full text-white text-sm font-medium py-2 rounded-md transition duration-200" :class="[
                selectedIds.length > 0
                    ? 'bg-red-500 hover:bg-red-600 cursor-pointer'
                    : 'bg-slate-300 dark:bg-slate-700 cursor-not-allowed opacity-50'
            ]" :disabled="selectedIds.length === 0" @click="onConfirmDelete">
                {{ t("delete") }} ({{ selectedIds.length }})
            </button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { style } from "@/assets/tailwindcss";
import CircleAvatar from "@/components/Avatar/CircleAvatar.vue";
import { useTranslate } from "@/composables/useTranslate";
import { useJoinGroupStore } from "@/stores/joinGroupRequest.storage";
import { useConfirmStore } from "@/composables/useConfirm";
import { useConversationStore } from "@/stores/conversation.storage";

const props = defineProps<{
    isShowBackButton: boolean;
}>();

const emit = defineEmits(['back']);
const confirmStore = useConfirmStore()
const convStorage = useConversationStore();

const { t } = useTranslate();
const joinRequestStorage = useJoinGroupStore();

// Mảng lưu các ID của request được chọn
const selectedIds = ref<number[]>([]);

// Kiểm tra xem đã chọn tất cả chưa
const isAllSelected = computed(() => {
    const requests = joinRequestStorage.joinGroupRequests || [];
    if (requests.length === 0) return false;
    return requests.every(req => selectedIds.value.includes(req.id));
});

// Hàm chọn / bỏ chọn từng item
const toggleSelect = (id: number) => {
    const index = selectedIds.value.indexOf(id);
    if (index > -1) {
        selectedIds.value.splice(index, 1); // Đã có thì xóa ra khỏi list
    } else {
        selectedIds.value.push(id); // Chưa có thì thêm vào list
    }
};

// Hàm xử lý nút "Chọn tất cả" / "Bỏ chọn hết"
const toggleSelectAll = () => {
    if (isAllSelected.value) {
        // Nếu đã chọn hết rồi -> Bỏ chọn toàn bộ
        selectedIds.value = [];
    } else {
        // Nếu chưa chọn hết -> Thêm tất cả ID vào mảng
        const requests = joinRequestStorage.joinGroupRequests || [];
        selectedIds.value = requests.map(req => req.id);
    }
};

const onConfirmApprove = () => {
    if (selectedIds.value.length === 0) return;
    confirmStore.open({
        title: t('approve'),
        message: t('approve'),
        onOk: async () => {
            const isSuccess = await joinRequestStorage.approveRequests(selectedIds.value, convStorage.conversation!.id)

            if (isSuccess) {
                joinRequestStorage.removeRequest(selectedIds.value)
                selectedIds.value = []
            }
        }
    });
}

const onConfirmDelete = () => {
    if (selectedIds.value.length === 0) return;
    confirmStore.open({
        title: t('delete'),
        message: t('delete'),
        onOk: async () => {
            const isSuccess = await joinRequestStorage.removeRequests(selectedIds.value, convStorage.conversation!.id)

            if (isSuccess) {
                joinRequestStorage.removeRequest(selectedIds.value)
                selectedIds.value = []
            }
        }
    });
}
</script>