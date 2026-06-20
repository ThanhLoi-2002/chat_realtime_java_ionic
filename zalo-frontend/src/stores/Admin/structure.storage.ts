import { structureApi } from '@/api/Admin/structure.api'
import { StructureSortType } from '@/types/common'
import { StructureType } from '@/types/entities'
import { toast } from '@/utils/toast'
import { defineStore } from 'pinia'

interface State {
    tree: Record<string, StructureType[]>
    trashes: StructureType[]
    isLoading: boolean
}

export const useAdminStructureStore = defineStore('adminStructure', {
    state: (): State => ({
        tree: {},
        trashes: [],
        isLoading: false,
    }),
    actions: {
        async getTree() {
            try {
                const result: any = await structureApi.getTree();
                this.tree = result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            }
        },
        async getTrash() {
            try {
                const result: any = await structureApi.getTrash();
                this.trashes = result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            }
        },

        async sort(payload: StructureSortType[]) {
            try {
                const result: any = await structureApi.sort(payload);
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            }
        },

        async createOrUpdate(payload: Omit<StructureType, 'id'> & { id?: number; }) {
            try {
                const result: any = await structureApi.createOrUpdate(payload);
                await this.getTree()
                await this.getTrash()
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
            }
        },

        // async deleteOne(id: number) {
        //     try {
        //         const result: any = await structureApi.deleteOne(id);
        //         this.getTree()
        //         this.getTrash()
        //     } catch (e: any) {
        //         toast({
        //             color: "danger",
        //             message: e.message
        //         })
        //     }
        // },

        // async restore(id: number) {
        //     try {
        //         const result: any = await structureApi.restore(id);
        //         this.getTree()
        //         this.getTrash()
        //     } catch (e: any) {
        //         toast({
        //             color: "danger",
        //             message: e.message
        //         })
        //     }
        // },
    }
})