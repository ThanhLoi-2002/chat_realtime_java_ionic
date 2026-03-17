<template>
    <ion-modal ref="modal" :trigger="triggerId" :enter-animation="enterAnimation" :leave-animation="leaveAnimation">
        <ion-content class="">
            <ion-toolbar class="px-4">
                <!-- BACK BUTTON -->
                <ion-buttons slot="start" v-if="isShowBackButton">
                    <ion-button @click="goBack">
                        <i class="fa fa-arrow-left"></i>
                    </ion-button>
                </ion-buttons>

                <ion-title>{{ title }}</ion-title>
                <ion-buttons slot="end">
                    <ion-button @click="dismiss()"><i class="fa fa-close" /></ion-button>
                </ion-buttons>
            </ion-toolbar>

            <!-- body slot: nội dung truyền từ bên ngoài -->
            <div class="p-4">
                <slot />
            </div>

            <!-- optional footer -->
            <div class="p-4" v-if="$slots.footer">
                <slot name="footer" />
            </div>

        </ion-content>
    </ion-modal>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import {
    createAnimation,
    IonButtons,
    IonButton,
    IonModal,
    IonContent,
    IonToolbar,
    IonTitle,
} from '@ionic/vue';
import { provide } from "vue"
import { useTranslate } from '@/composables/useTranslate';

const props = defineProps<{
    triggerId: string
    title: string,
    goBack?: () => void
}>()

const { t } = useTranslate()
const modal = ref<InstanceType<typeof IonModal> | null>(null)
const isShowBackButton = ref(false)
const dismiss = () => modal.value?.$el.dismiss()
provide("modalDismiss", dismiss)

const titlesNotDisplayBackButton = computed(() => [
    t("setting")
])

const enterAnimation = (baseEl: any) => {
    const root = baseEl.shadowRoot;

    const backdropAnimation = createAnimation()
        .addElement(root.querySelector('ion-backdrop'))
        .fromTo('opacity', '0.01', 'var(--backdrop-opacity)');

    const wrapperAnimation = createAnimation()
        .addElement(root.querySelector('.modal-wrapper'))
        .keyframes([
            { offset: 0, opacity: '0', transform: 'scale(0)' },
            { offset: 1, opacity: '0.99', transform: 'scale(1)' },
        ]);

    return createAnimation()
        .addElement(baseEl)
        .easing('ease-out')
        .duration(500)
        .addAnimation([backdropAnimation, wrapperAnimation]);
};

const leaveAnimation = (baseEl: any) => {
    return enterAnimation(baseEl).direction('reverse');
};

watch(() => props.title, () => {
    if (!titlesNotDisplayBackButton.value.includes(props.title)) {
        isShowBackButton.value = true
    } else {
        isShowBackButton.value = false
    }
})
</script>