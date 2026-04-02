import { defineStore } from 'pinia'

interface ConversationState {
    isShowBottomMenu: boolean
    isDarkMode: boolean
}

export const useSystemStore = defineStore('system', {
    state: (): ConversationState => ({
        isShowBottomMenu: false,
        isDarkMode: false,
    }),
    actions: {
        setShowBottomMenu(value: boolean) {
            this.isShowBottomMenu = value
        },
        setIsDarkMode(value: boolean) {
            this.isDarkMode = value
        },
    }
})
