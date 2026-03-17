<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>Chat Room: {{ roomId }}</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <div class="messages" ref="messagesContainer">
        <div v-for="msg in messages" :key="msg.id" class="message">
          <div class="meta">
            <strong>{{ msg.sender }}</strong>
            <small>{{ formatTime(msg.timestamp) }}</small>
          </div>
          <div class="content">{{ msg.content }}</div>
        </div>
      </div>
    </ion-content>

    <ion-footer>
      <ion-toolbar>
        <ion-item lines="none">
          <ion-input v-model="input" placeholder="Type a message..." @keydown.enter="send"
            @ionInput="typing"></ion-input>
          <ion-button slot="end" @click="send">Send</ion-button>
        </ion-item>
      </ion-toolbar>
    </ion-footer>
  </ion-page>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue';
import { StompSubscription } from '@stomp/stompjs';
import { socketSubscribe, sockJSSendMessage } from '@/utils/websocket';

const roomId = 1;

const input = ref('');
const messages = ref<Array<any>>([]);

let subMessage: StompSubscription | undefined
let subTyping: StompSubscription | undefined

onMounted(() => {
  subMessage = socketSubscribe(`chat.newMessage`, (msg: any) => {
    console.log("new message:", JSON.parse(msg.body))
  })

  subTyping = socketSubscribe(`chat.typing.1`, (msg: any) => {
    console.log("typing:", JSON.parse(msg.body))
  })

})

onUnmounted(() => {
  subMessage?.unsubscribe()
  subTyping?.unsubscribe()
})

const typing = () => {
  sockJSSendMessage({
    conversationId: roomId,
    content: 'Lợi đang nhập'
  }, "chat.typing")
}

function formatTime(ts: string | null) {
  if (!ts) return '';
  const d = new Date(ts);
  return d.toLocaleTimeString();
}

function send() {
  if (!input.value) return;
  const payload = {
    roomId,
    content: input.value,
    timestamp: new Date().toISOString()
  };

  sockJSSendMessage(payload, 'chat.sendMessage')
}
</script>

<style scoped>
.messages {
  display: flex;
  flex-direction: column;
  gap: 8px;
  height: calc(100vh - 160px);
  overflow: auto;
  padding-bottom: 8px;
}

.message {
  background: #f1f1f1;
  padding: 8px 12px;
  border-radius: 8px;
  max-width: 80%;
}

.meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
  font-size: 0.85rem;
  color: #333;
}

.content {
  word-wrap: break-word;
}
</style>