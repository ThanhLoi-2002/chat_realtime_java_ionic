<template>

    <div class="flex gap-2 items-start relative group"
        :class="isOwner ? 'ml-auto flex-row-reverse max-w-[50%]' : 'max-w-[50%]'" ref="rootRef">

        <!-- AVATAR -->
        <circle-avatar v-if="!isOwner && message.showAvatar" :url="message.sender?.avatar?.url" size="size-8"
            :onClick="() => friendProfileModal?.present()" />
        <div v-else-if="!isOwner" class="px-4 py-2"></div>

        <!-- BUBBLE -->
        <div class="relative group"> <!-- thêm class group ở đây -->
            <div ref="bubbleRef" :class="[
                'text-sm flex flex-col relative',

                // 👉 chỉ áp dụng bubble khi KHÔNG phải image
                (message.contentType !== MessageEnum.IMAGE || message.contentType == MessageEnum.FILE) && [
                    'px-4 py-2 border rounded-lg',
                    isOwner
                        ? 'bg-blue-500 text-white border-blue-500'
                        : 'bg-white dark:bg-gray-800 dark:text-slate-100 border-slate-300 dark:border-gray-700'
                ]
            ]">

                <!-- USERNAME -->
                <span v-if="message.isFirst && !isOwner" class="text-xs text-slate-400 dark:text-slate-300">
                    {{ message.sender?.username }}
                </span>

                <!-- CONTENT -->
                <div class="py-1">
                    <!-- nếu bị thu hồi -->
                    <span v-if="message.stt === -1" class="italic text-gray-600 dark:text-gray-700">
                        {{ isOwner ? 'Bạn đã thu hồi tin nhắn' : 'Tin nhắn đã bị thu hồi' }}
                    </span>

                    <!-- nếu là image -->
                    <img v-else-if="message.contentType === MessageEnum.IMAGE" :src="message.file.url"
                        @click="handlePreviewImage(message.file.url)"
                        class="max-w-55 rounded-xl object-cover cursor-pointer hover:opacity-90 transition" />

                    <!-- text bình thường -->
                    <span v-else>
                        {{ message.content }}
                    </span>
                </div>

                <!-- TIME -->
                <span v-if="message.showTime" :class="[
                    'text-xs',
                    isOwner ? 'text-blue-100' : 'text-slate-400 dark:text-slate-400'
                ]">
                    {{ formatTime(message.ct) }}
                </span>
            </div>

            <!-- Vùng like hiển thị khi hover: đặt ngoài bubble hoặc trên mép tùy ý -->
            <div class="absolute right-2 bottom-0 translate-y-1/2 flex items-center gap-2
                opacity-0 group-hover:opacity-100 transition-opacity duration-150
                pointer-events-none group-hover:pointer-events-auto">
                <div class="relative group/like">
                    <button
                        class="w-5 h-5 flex items-center justify-center rounded-full bg-slate-100 dark:bg-gray-700 hover:bg-slate-200 dark:hover:bg-gray-600 shadow-sm p-1">
                        <i class="far fa-thumbs-up dark:text-white text-xs"></i>
                    </button>

                    <!-- REACTIONS: ẩn mặc định, hiện khi hover vào nút like -->
                    <div class="absolute bottom-full -mb-1 -translate-x-1/2 hidden group-hover/like:flex gap-1
                            bg-white dark:bg-gray-800 shadow-lg rounded-full px-2 py-1"
                        :class="[isOwner ? '-right-20' : 'left-1/2']">
                        <span class="cursor-pointer hover:scale-125 transition">👍</span>
                        <span class="cursor-pointer hover:scale-125 transition">❤️</span>
                        <span class="cursor-pointer hover:scale-125 transition">😂</span>
                        <span class="cursor-pointer hover:scale-125 transition">😮</span>
                        <span class="cursor-pointer hover:scale-125 transition">😢</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- ACTIONS -->
        <div class="flex items-center gap-1
         opacity-0 group-hover:opacity-100 transition my-auto">
            <button ref="moreBtnRef" @click.stop="toggleMenu"
                class="px-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 dark:bg-gray-800 dark:text-white text-center cursor-pointer">
                <i class="fa-solid fa-quote-right text-[10px] leading-none"></i>
            </button>

            <button ref="moreBtnRef" @click.stop="toggleMenu"
                class="px-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 dark:bg-gray-800 dark:text-white text-center cursor-pointer">
                <i class="fas fa-share text-[10px] leading-none"></i>
            </button>

            <!-- MORE -->
            <button ref="moreBtnRef" @click.stop="toggleMenu"
                class="px-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 dark:bg-gray-800 dark:text-white text-center cursor-pointer">
                <i class="fas fa-ellipsis-h text-[10px] leading-none"></i>
            </button>
        </div>

        <!-- Teleported CONTEXT MENU (rendered into body) -->
        <teleport to="body">
            <div v-if="showMenu" ref="menuRef" :style="menuInlineStyle" class="fixed z-50 w-56
           bg-white dark:bg-gray-800 rounded-lg shadow-lg border
           border-gray-200 dark:border-gray-700 overflow-hidden
           transition-opacity duration-150 text-sm">
                <div class="p-2 hover:bg-gray-100 dark:hover:bg-gray-700 cursor-pointer" @click="onCopy">
                    {{ t('copyMessage') }}
                </div>
                <div class="p-2 hover:bg-gray-100 dark:hover:bg-gray-700 cursor-pointer" @click="onMark">
                    {{ t('markMessage') }}
                </div>
                <div class="p-2 hover:bg-gray-100 dark:hover:bg-gray-700 cursor-pointer" @click="onDetails">
                    {{ t('seeDetails') }}
                </div>

                <div class="border-t border-gray-200 dark:border-gray-700"></div>

                <div class="p-2 text-red-500 hover:bg-red-50 dark:hover:bg-red-700/20 cursor-pointer" @click="onDelete">
                    {{ t('delete') }}
                </div>
            </div>
        </teleport>

        <div v-if="previewImage" class="fixed inset-0 z-50 flex items-center justify-center 
           bg-black/80 backdrop-blur-sm">

            <!-- overlay click để đóng -->
            <div class="absolute inset-0" @click="closePreview"></div>

            <!-- ảnh -->
            <img :src="previewImage" class="relative max-w-[90vw] max-h-[90vh] rounded-xl shadow-2xl z-10" />

            <!-- nút close -->
            <button @click="closePreview" class="absolute top-5 right-5 z-20 
               w-10 h-10 flex items-center justify-center 
               bg-white/10 hover:bg-white/20 
               rounded-full text-white text-xl">
                ✕
            </button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import { useDateTime } from '@/composables/useDateTime'
import { useUserStore } from '@/stores/user.storage'
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue'
import { useTranslate } from '@/composables/useTranslate';
import { MessageEnum } from '@/types/enum';

const props = defineProps<{
    message: any
    friendProfileModal: any
}>()

const { t } = useTranslate()
const { formatTime } = useDateTime()

const userStorage = useUserStore()

const isOwner = props.message.sender.id === userStorage.user?.id

// refs
const bubbleRef = ref<HTMLElement | null>(null)
const moreBtnRef = ref<HTMLElement | null>(null)
const menuRef = ref<HTMLElement | null>(null)

// menu state
const showMenu = ref(false)
const menuInlineStyle = ref<Record<string, string>>({ opacity: '0' })
const toggleMenu = async () => {
    showMenu.value = !showMenu.value
    if (showMenu.value) {
        await nextTick()
        positionMenu()
        // small fade in
        requestAnimationFrame(() => {
            menuInlineStyle.value.opacity = '1'
        })
    } else {
        menuInlineStyle.value.opacity = '0'
    }
}

// calculate position and set menuInlineStyle
const positionMenu = () => {
    if (!bubbleRef.value || !menuRef.value) return

    const bubbleRect = bubbleRef.value.getBoundingClientRect()
    const menuRect = menuRef.value.getBoundingClientRect()
    const viewportWidth = window.innerWidth
    const viewportHeight = window.innerHeight
    const margin = 8

    // default position: place menu below the bubble (align start or end depending on isOwner)
    let top = bubbleRect.bottom + margin
    let left: number
    if (isOwner) {
        // align menu's right edge to bubble's right edge
        left = bubbleRect.right - menuRect.width
    } else {
        // align menu's left edge to bubble's left edge
        left = bubbleRect.left
    }

    // If would overflow right, clamp
    if (left + menuRect.width > viewportWidth - 8) {
        left = viewportWidth - menuRect.width - 8
    }

    // If would overflow left, clamp
    if (left < 8) {
        left = 8
    }

    // If not enough space below, try place above
    if (top + menuRect.height > viewportHeight - 8) {
        const aboveTop = bubbleRect.top - margin - menuRect.height
        if (aboveTop >= 8) {
            top = aboveTop
        } else {
            // cannot fit above or below well, clamp top
            top = Math.max(8, viewportHeight - menuRect.height - 8)
        }
    }

    menuInlineStyle.value = {
        position: 'fixed',
        top: `${Math.round(top)}px`,
        left: `${Math.round(left)}px`,
        opacity: menuInlineStyle.value.opacity ?? '1'
    }
}

const previewImage = ref<string | null>(null)

const handlePreviewImage = (url: string) => {
    previewImage.value = url
}

const closePreview = () => {
    previewImage.value = null
}

// Actions (placeholders — replace with your actual logic)
const onCopy = () => {
    navigator.clipboard?.writeText(props.message.content || '')
    showMenu.value = false
}

const onMark = () => {
    // implement marking logic
    showMenu.value = false
}

const onDetails = () => {
    // open details modal
    showMenu.value = false

}

const onDelete = () => {
    // confirm & delete
    showMenu.value = false
}

// click outside: if clicked outside menu and outside more button, close
const handleClickOutside = (e: MouseEvent) => {
    const target = e.target as Node

    if (menuRef.value && menuRef.value.contains(target)) return
    if (moreBtnRef.value && moreBtnRef.value.contains(target)) return

    // clicking inside bubble but not on more button should close menu
    if (bubbleRef.value && bubbleRef.value.contains(target)) {
        showMenu.value = false
        return
    }

    showMenu.value = false
}

const handleScrollOrResize = () => {
    if (showMenu.value) nextTick(positionMenu)
}

onMounted(() => {
    document.addEventListener('click', handleClickOutside)
    window.addEventListener('resize', handleScrollOrResize)
    window.addEventListener('scroll', handleScrollOrResize, true) // capture scroll on ancestors
})

onBeforeUnmount(() => {
    document.removeEventListener('click', handleClickOutside)
    window.removeEventListener('resize', handleScrollOrResize)
    window.removeEventListener('scroll', handleScrollOrResize, true)
})

// reposition when content or ownership changes
watch(
    () => [props.message.content, isOwner],
    () => {
        if (showMenu.value) nextTick(positionMenu)
    }
)
</script>