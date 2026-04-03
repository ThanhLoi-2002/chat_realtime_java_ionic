import { defineStore } from 'pinia'

interface ConversationState {
    isShowBottomMenu: boolean
    isDarkMode: boolean
    userIdsOnline: Record<number, boolean>
}

export const useSystemStore = defineStore('system', {
    state: (): ConversationState => ({
        isShowBottomMenu: false,
        isDarkMode: false,
        userIdsOnline: {}
    }),
    actions: {
        setShowBottomMenu(value: boolean) {
            this.isShowBottomMenu = value
        },
        setIsDarkMode(value: boolean) {
            this.isDarkMode = value
        },
        updateUserIdsOnline(id: number, value: boolean) {
            if (value) {
                this.userIdsOnline[id] = true
            } else {
                delete this.userIdsOnline[id]
            }
        }
    }
})
