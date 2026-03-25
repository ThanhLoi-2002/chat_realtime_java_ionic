<template>
  <div>
    <!-- Typing indicator (giữ nguyên) -->
    <div v-if="typingUsers.size > 0" class="text-sm text-gray-600 dark:text-gray-400 px-4 pb-2 flex items-center gap-1">
      <span>
        <span v-for="([id, user], index) in typingUsers" :key="id">
          <span v-if="index > 0">, </span>{{ user.username }}
        </span>
        {{ t("typing") }}
      </span>
      <span class="flex gap-1 ml-1">
        <span class="dot"></span>
        <span class="dot"></span>
        <span class="dot"></span>
      </span>
    </div>

    <!-- INPUT BAR -->
    <div class="p-3 border-t border-gray-200 dark:border-slate-700 bg-white dark:bg-gray-900">
      <div class="flex items-center gap-2 bg-gray-100 dark:bg-gray-800 rounded-2xl px-4 py-1.5">
        
        <!-- Left icons -->
        <button @click="togglePicker" class="text-2xl text-gray-500 dark:text-gray-400 hover:text-blue-500 transition">
          🙂
        </button>
        <button class="text-2xl text-gray-500 dark:text-gray-400">
          📎
        </button>

        <!-- Message Input -->
        <input
          v-model="message"
          @keyup.enter="sendMessage"
          @input="sendTyping"
          :placeholder="t('typeMessage')"
          class="flex-1 bg-transparent outline-none text-base dark:text-slate-200 placeholder-gray-400 dark:placeholder-gray-500"
        />

        <!-- Right icons -->
        <button @click="togglePicker" class="text-2xl text-gray-500 dark:text-gray-400 hover:text-blue-500 transition">
          😊
        </button>
        <base-button 
          icon="fa-solid fa-paper-plane text-blue-500 text-xl" 
          @click="sendMessage" 
          class="ml-1" 
        />
      </div>

      <!-- Sticker / Emoji / GIF Picker -->
      <div v-if="showPicker" 
           class="mt-2 bg-white dark:bg-gray-800 rounded-2xl shadow-xl border border-gray-200 dark:border-gray-700 overflow-hidden">
        
        <!-- Tabs -->
        <div class="flex border-b border-gray-200 dark:border-gray-700">
          <button 
            v-for="tab in tabs" 
            :key="tab.key"
            @click="activeTab = tab.key"
            :class="[
              'flex-1 py-3 text-sm font-medium transition',
              activeTab === tab.key 
                ? 'text-blue-500 border-b-2 border-blue-500' 
                : 'text-gray-500 dark:text-gray-400'
            ]">
            {{ tab.label }}
          </button>
        </div>

        <!-- Search -->
        <div class="p-3">
          <div class="relative">
            <input 
              v-model="searchQuery"
              placeholder="Tìm kiếm sticker" 
              class="w-full bg-gray-100 dark:bg-gray-700 rounded-xl py-2.5 pl-10 text-sm focus:outline-none"
            />
            <span class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400">🔍</span>
          </div>
        </div>

        <!-- Content Area -->
        <div class="max-h-80 overflow-auto p-3 grid grid-cols-4 gap-3" style="scrollbar-width: thin;">
          <!-- Gần đây + Sticker grid -->
          <div v-for="(item, i) in filteredItems" :key="i" 
               class="aspect-square flex items-center justify-center bg-gray-100 dark:bg-gray-700 rounded-2xl cursor-pointer hover:scale-110 transition active:scale-95"
               @click="sendSticker(item)">
            <img :src="item.url" class="w-14 h-14 object-contain" alt="sticker" />
          </div>
        </div>

        <!-- Bottom quick icons (như Zalo) -->
        <div class="border-t border-gray-200 dark:border-gray-700 p-3 flex gap-4 overflow-x-auto">
          <button v-for="(icon, i) in quickIcons" :key="i" 
                  class="text-3xl shrink-0 hover:scale-110 transition">
            {{ icon }}
          </button>
          <button class="text-3xl shrink-0 text-gray-400">+</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useDebounce } from '@/composables/useDebounce';
import { useScroll } from '@/composables/useScroll';
import { useTranslate } from '@/composables/useTranslate';
import { useConversationStore } from '@/stores/conversation.storage';
import { useMessageStore } from '@/stores/message.storage';
import { useUserStore } from '@/stores/user.storage';
import { SendMessageType } from '@/types/common';
import { MessageEnum } from '@/types/enum';
import { socketSubscribe, sockJSSendMessage } from '@/utils/websocket';
import { StompSubscription } from '@stomp/stompjs';
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';

const props = defineProps<{
    scrollContainer: HTMLElement | null
}>()

const { t } = useTranslate()
const conversationStorage = useConversationStore()
const userStorage = useUserStore()
const messageStorage = useMessageStore()
const { scrollToBottom } = useScroll()

const message = ref('')
const typingUsers = ref<Map<number, any>>(new Map())

const showPicker = ref(false)
const activeTab = ref<'sticker' | 'emoji' | 'gif' | string>('sticker')
const searchQuery = ref('')

const tabs = [
  { key: 'sticker', label: 'STICKER' },
  { key: 'emoji',   label: 'EMOJI' },
  { key: 'gif',     label: 'GIF' }
]

// Dữ liệu mẫu sticker (bạn thay bằng API sau)
const recentStickers = ref([
  { url: 'https://via.placeholder.com/100/FF6B6B/fff?text=Cat' },
  { url: 'https://via.placeholder.com/100/4ECDC4/fff?text=Meow' },
  { url: 'https://via.placeholder.com/100/45B7D1/fff?text=Wow' },
  // ... thêm nhiều hơn
])

const filteredItems = computed(() => {
  // sau này filter theo searchQuery + activeTab
  return recentStickers.value
})

const quickIcons = ['🐱', '😺', '😹', '🙀', '😻', '🐶']

const togglePicker = () => {
  showPicker.value = !showPicker.value
}

const sendSticker = (sticker: any) => {
  // Gửi sticker (contentType = STICKER, content = sticker.url)
  console.log('Gửi sticker:', sticker)
  showPicker.value = false
  // gọi messageStorage.sendMessage(...)
}

const { debounced: sendTyping } = useDebounce(() => {
    sockJSSendMessage({
        conversationId: conversationStorage.conversation?.id,
        username: userStorage.user?.username,
        userId: userStorage.user?.id,
    }, "chat.typing")
}, 300)

const sendMessage = async () => {
    if (!message.value.trim()) return

    const payload: SendMessageType = {
        content: message.value.trim(),
        conversationId: conversationStorage.conversation?.id,
        contentType: MessageEnum.TEXT
    }

    const success = await messageStorage.sendMessage(payload);

    if (success) {
        message.value = ''
        scrollToBottom(props.scrollContainer, true)
    }
}

let subTyping: StompSubscription | undefined

onMounted(() => {
    resetSubscribe()
})

onUnmounted(() => {
    subTyping?.unsubscribe()
})

watch(() => conversationStorage.conversation, async () => {
    subTyping?.unsubscribe()
    resetSubscribe()
})

const resetSubscribe = () => {
    subTyping = socketSubscribe(`/user/queue/chat.typing.${conversationStorage.conversation?.id}`, (msg: any) => {
        const data = JSON.parse(msg.body)

        // bỏ qua chính mình
        if (data.userId === userStorage.user?.id) return

        handleTyping(data)
    })
}

const handleTyping = (data: any) => {
    const userId = data.userId

    // nếu đã có thì clear timeout cũ
    if (typingUsers.value.has(userId)) {
        clearTimeout(typingUsers.value.get(userId).timeout)
    }

    // set timeout mới (2s)
    const timeout = setTimeout(() => {
        typingUsers.value.delete(userId)
    }, 2000)

    // update map
    typingUsers.value.set(userId, {
        ...data,
        timeout
    })
}
</script>

<style>
.dot {
    width: 4px;
    height: 4px;
    background-color: currentColor;
    border-radius: 50%;
    animation: bounce 1.4s infinite;
}

/* delay từng dot */
.dot:nth-child(1) {
    animation-delay: 0s;
}

.dot:nth-child(2) {
    animation-delay: 0.2s;
}

.dot:nth-child(3) {
    animation-delay: 0.4s;
}

@keyframes bounce {

    0%,
    80%,
    100% {
        transform: scale(0);
        opacity: 0.3;
    }

    40% {
        transform: scale(1);
        opacity: 1;
    }
}
</style>