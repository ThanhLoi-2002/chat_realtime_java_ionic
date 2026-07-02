<template>
    <div
        :class="[oaStyle.bg.primary, oaStyle.text.primary, 'w-full h-screen flex flex-col md:flex-row overflow-hidden']">

        <header
            :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'md:hidden flex items-center justify-between px-4 py-3 border-b']">
            <div :class="[oaStyle.text.primary, 'text-lg font-medium tracking-wide']">✨ zSticker AI Studio</div>
            <button @click="mobileActiveTab = mobileActiveTab === 'history' ? 'create' : 'history'"
                class="bg-purple-600 hover:bg-purple-700 text-xs font-semibold px-3 py-1.5 rounded-full transition">
                {{ mobileActiveTab === 'history' ?  t('openGenerator') : t('photoLibrary') }}
            </button>
        </header>

        <section :class="[oaStyle.border.primary, oaStyle.bg.secondary, 'w-full md:w-80 lg:w-96 border-r flex-col h-full transition-all duration-300',
        mobileActiveTab === 'history' ? 'flex' : 'hidden md:flex'
        ]">
            <div :class="[oaStyle.border.primary, 'p-4 border-b flex items-center justify-between']">
                <div class="flex items-center gap-2">
                    <div :class="[oaStyle.text.primary, 'font-medium text-lg']">{{ t('yourStickers') }}</div>
                </div>
                <span :class="[oaStyle.text.primary, oaStyle.bg.tertiary, 'text-xs px-2 py-0.5 rounded-md']">
                    {{ createdStickers.length }} bộ
                </span>
            </div>

            <div class="flex-1 p-4 overflow-y-auto grid grid-cols-3 gap-2 content-start custom-scrollbar">
                <div v-for="sticker in createdStickers" :key="sticker.id" :class="[
                    oaStyle.bg.secondary, oaStyle.border.secondary,
                    'group relative aspect-square border hover:border-purple-500 rounded-lg flex items-center justify-center cursor-pointer transition hover:scale-105'
                ]" @click="sendStickerToChat(sticker.url)">
                    <ZaloSticker :sticker-item="sticker" :is-hover="true" :size="80" :is-use-default-url="true" />

                    <!-- <div
                        class="absolute inset-0 bg-black/40 rounded-xl opacity-0 group-hover:opacity-100 items-center justify-center transition hidden md:flex">
                        <span class="text-xs font-semibold bg-purple-600 px-2 py-1 rounded-md">{{ t('send') }}</span>
                    </div> -->
                </div>

                <div v-if="createdStickers.length === 0"
                    :class="[oaStyle.text.primary, 'col-span-3 text-center py-20 text-sm']">
                    <span class="text-3xl block mb-2">🎨</span>
                    Chưa có sticker nào.<br>Hãy tạo sticker đầu tiên!
                </div>
            </div>
        </section>


        <section :class="[oaStyle.bg.secondary, 'flex-1 flex flex-col h-full relative',
        mobileActiveTab === 'create' ? 'flex' : 'hidden md:flex']">

            <div class="flex-1 w-full overflow-y-auto p-4 flex items-center justify-center">

                <div
                    :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'w-full h-full p-6 flex flex-col rounded-xl shadow-lg border']">

                    <div :class="[oaStyle.text.primary, 'text-2xl font-medium mb-4']">{{ t('generateSticker') }}</div>
                    <div class="flex-1">
                        <div v-if="stickerItem" class="flex flex-col gap-2">
                            <!-- Prompt -->
                            <div :class="[
                                oaStyle.bg.tertiary,
                                oaStyle.border.secondary,
                                oaStyle.text.primary,
                                'max-w-xl rounded-2xl border px-4 py-3 shadow-sm'
                            ]">
                                <div class="text-xs opacity-60 mb-1">
                                    {{ t('prompt') }}
                                </div>

                                <div class="whitespace-pre-wrap wrap-break-word text-sm leading-6">
                                    {{ oldPrompt }}
                                </div>
                            </div>

                            <!-- Sticker -->
                            <div class="flex-1 flex items-center justify-center py-8">
                                <ZaloSticker :key="stickerItem.url" :sticker-item="stickerItem" :is-hover="false"
                                    :size="130" :is-use-default-url="true" />
                            </div>

                            <div :class="[oaStyle.text.secondary, 'text-center text-xs mb-3']">
                                {{ newFileName }}
                            </div>

                            <!-- Action -->
                            <div :class="[oaStyle.border.secondary, 'border-t pt-3 flex flex-wrap gap-2 justify-end']">
                                <button @click="copyPrompt" :disabled="isGenerating"
                                    class="p-2 text-sm rounded-sm border hover:bg-slate-200 dark:hover:bg-slate-700 transition cursor-pointer">
                                    <i class="fas fa-copy"></i> {{ t('copyPromt') }}
                                </button>

                                <button @click="regenerateSticker" :disabled="isGenerating"
                                    class="p-2 text-sm rounded-sm bg-amber-500 hover:bg-amber-600 text-white transition disabled:opacity-60 cursor-pointer">
                                    <i class="fas fa-undo"></i> {{ t('changeSticker') }}
                                </button>

                                <button @click="saveSticker" :disabled="isGenerating"
                                    class="p-2 text-sm rounded-sm bg-green-600 hover:bg-green-700 text-white transition cursor-pointer">
                                    <i class="fas fa-save"></i> {{ t('save') }}
                                </button>
                            </div>
                        </div>
                    </div>

                    <div class="">
                        <label :class="[oaStyle.text.secondary, 'block text-sm font-medium mb-2']">{{
                            t('description')
                            }}</label>
                        <textarea v-model="prompt"
                            :class="[oaStyle.border.secondary, 'w-full p-3 border rounded-lg focus:ring-1 focus:ring-blue-500 outline-none transition']"
                            rows="6" placeholder="Ví dụ: Một phi hành gia cưỡi ngựa trên sao Hỏa..."></textarea>
                    </div>

                    <button @click="handleGenerate" :disabled="isGenerating"
                        class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 rounded-lg transition disabled:bg-gray-400 cursor-pointer">
                        {{ isGenerating ? t('loading') : t('create') }}
                    </button>

                </div>
            </div>
        </section>
    </div>
</template>

<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import ZaloSticker from '@/components/Shared/Sticker/Zalo/ZaloSticker.vue';
import { useTranslate } from '@/composables/useTranslate';
import { useStickerStore } from '@/stores/App/sticker.storage';
import { StickerItemType } from '@/types/entities';
import { ref } from 'vue';

// State management
const prompt = ref<string>('');
const oldPrompt = ref<string>('');
const isGenerating = ref<boolean>(false);
const stickerStor = useStickerStore()
const stickerItem = ref<StickerItemType | undefined>(undefined)
const newFileName = ref<string>('')
const newBlob = ref<Blob>()

const { t } = useTranslate()
// Tab tích hợp riêng cho màn hình Mobile ('history': Xem kho ảnh | 'create': Trình tạo sticker)
const mobileActiveTab = ref<'history' | 'create'>('create');

// Mock data danh sách sticker user đã tạo trước đó (Thay bằng dữ liệu lấy từ DB Spring Boot của bạn)
const createdStickers = ref<StickerItemType[]>([]);

// Hàm xử lý click chọn sticker để đẩy thẳng vào ô chat chính qua WebSocket
const sendStickerToChat = (url: string) => {
    console.log("Đã chọn gửi sticker vào hội thoại:", url);
    // Thực hiện emit ra phòng chat lớn hoặc call hàm gửi WebSocket chat tại đây
};

// Hàm xử lý
const handleGenerate = async () => {
    if (!prompt.value) return;
    isGenerating.value = true;

    try {
        const { sticker, blob, fileName } = await stickerStor.generateSticker({ prompt: prompt.value })

        stickerItem.value = sticker
        newBlob.value = blob
        newFileName.value = fileName

        console.log(blob, fileName, stickerItem.value)

        oldPrompt.value = prompt.value

        prompt.value = ''
    } catch {
        //
    } finally {
        isGenerating.value = false;
    }
};

const copyPrompt = async () => {
    if (!oldPrompt.value) return

    await navigator.clipboard.writeText(oldPrompt.value)
}

const regenerateSticker = async () => {
    if (!oldPrompt.value) return

    isGenerating.value = true

    try {
        const { sticker, blob, fileName } =
            await stickerStor.generateSticker({
                prompt: oldPrompt.value
            })

        stickerItem.value = sticker
        newBlob.value = blob
        newFileName.value = fileName
    } finally {
        isGenerating.value = false
    }
}

const saveSticker = () => {
    if (!stickerItem.value) return

    createdStickers.value.push(stickerItem.value)
}
</script>