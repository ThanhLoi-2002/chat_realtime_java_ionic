import { classificationCardApi } from '@/api/classificationCard.api'
import { ClassificationCardFormType } from '@/schema/classificationCard.schema'
import { UpdateClassificationPositionType } from '@/types/common'
import { ClassificationCardType } from '@/types/entities'
import { toast } from '@/utils/toast'
import { defineStore } from 'pinia'

interface State {
    cards: ClassificationCardType[]
    card: ClassificationCardType | undefined
    isLoading: boolean
}

export const useClassificationCardStore = defineStore('classificationCard', {
    state: (): State => ({
        cards: [],
        card: undefined,
        isLoading: false,
    }),
    actions: {
        async create(data: ClassificationCardFormType) {
            try {
                const result: any = await classificationCardApi.create(data);
                this.cards.push(result.result)

                toast({
                    color: "success",
                    message: result.message
                })

                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },
        async update(id: number, data: ClassificationCardFormType) {
            try {
                const result: any = await classificationCardApi.update(id, data);

                const idx = this.cards.findIndex(i => i.id == id)

                if (idx != -1) this.cards[idx] = result.result

                toast({
                    color: "success",
                    message: result.message
                })

                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },
        async delete(id: number) {
            try {
                const result: any = await classificationCardApi.remove(id);
                this.cards = this.cards.filter(i => i.id != id)
                return true
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return false
            }
        },
        async getList() {
            try {
                const result: any = await classificationCardApi.getAll();
                this.cards = result.result
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return undefined
            }
        },

        selectCard(item?: ClassificationCardType) {
            this.card = item
        },

        async updatePositions() {
            try {
                const positions: UpdateClassificationPositionType[] = this.cards.map(c => ({ id: c.id, position: c.position }))
                const result: any = await classificationCardApi.updatePositions(positions);
            } catch (e: any) {
                toast({
                    color: "danger",
                    message: e.message
                })
                return undefined
            }
        }
    }
})
