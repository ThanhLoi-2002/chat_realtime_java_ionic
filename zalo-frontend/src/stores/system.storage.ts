import { defineStore } from 'pinia'
import { Network } from '@capacitor/network';

interface ConversationState {
    isShowBottomMenu: boolean
    isDarkMode: boolean
    networkStatus: boolean
}

export const useSystemStore = defineStore('system', {
    state: (): ConversationState => ({
        isShowBottomMenu: false,
        isDarkMode: false,
        networkStatus: false
    }),
    actions: {
        setShowBottomMenu(value: boolean) {
            this.isShowBottomMenu = value
        },
        setIsDarkMode(value: boolean) {
            this.isDarkMode = value
        },
        async checkNetworkStatus() {
            const status = await Network.getStatus();

            this.networkStatus = status.connected
        },
    }
})
