import { defineStore } from 'pinia'

interface ConversationState {
    isShowBottomMenu: boolean
}

export const useSystemStore = defineStore('system', {
    state: (): ConversationState => ({
        isShowBottomMenu: false,
    }),
    actions: {
        async setShowBottomMenu(value: boolean) {
            this.isShowBottomMenu = value
        },
    }
})
