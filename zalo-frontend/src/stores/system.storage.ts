import { defineStore } from 'pinia'
import { Network } from '@capacitor/network';

interface State {
    isShowBottomMenu: boolean
    isDarkMode: boolean
    networkStatus: boolean
    isOpenPins: boolean
    showSidebar: boolean
}

export const useSystemStore = defineStore('system', {
    state: (): State => ({
        isShowBottomMenu: false,
        isDarkMode: false,
        networkStatus: false,
        isOpenPins: false,
        showSidebar: false
    }),
    actions: {
        setShowBottomMenu(value: boolean) {
            this.isShowBottomMenu = value
        },
        setIsDarkMode(value: boolean) {
            this.isDarkMode = value
        },
        setIsOpenPins(value: boolean) {
            this.isOpenPins = value
        },
        async checkNetworkStatus() {
            const status = await Network.getStatus();

            this.networkStatus = status.connected
        },
        toggleSidebar() {
            this.showSidebar = !this.showSidebar
        },
    }
})
