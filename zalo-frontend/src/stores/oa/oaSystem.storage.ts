import { defineStore } from 'pinia'

interface State {
    showSidebar: boolean
}

export const useOASystemStore = defineStore('oaSystem', {
    state: (): State => ({
        showSidebar: false,
    }),
    actions: {
        toggleSidebar() {
            this.showSidebar = !this.showSidebar
        },
    }
})
