<template>
    <profile-info v-if="!isGroup(conversationStorage.conversation)" />
    <group-info v-else />
</template>

<script setup lang="ts">
import { useConversationStore } from '@/stores/conversation.storage'
import { useConversation } from '@/composables/useConversation'
import ProfileInfo from './components/ProfileInfo.vue'
import GroupInfo from './components/GroupInfo.vue'
import { onMounted, watch } from 'vue'

const emit = defineEmits(['update:isShowInfoSection'])

const conversationStorage = useConversationStore()
const { isGroup } = useConversation()

const loadInfo = () => {
    conversationStorage.getConversationInfo()
}

onMounted(() => {
    loadInfo()
})

watch(() => conversationStorage.conversation?.id, () => {
    loadInfo()
})
</script>