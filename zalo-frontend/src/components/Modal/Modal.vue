<template>
    <ion-modal ref="modal" :enter-animation="enterAnimation" :leave-animation="leaveAnimation" class="custom-modal">
        <ion-content class="ion-content-layout" :scroll-y="false">
            <ion-toolbar class="px-4">
                <!-- BACK BUTTON -->
                <ion-buttons slot="start" v-if="isDisplayBackButton && isShowBackButton">
                    <ion-button @click="goBack">
                        <i class="fas fa-arrow-circle-left"></i>
                    </ion-button>
                </ion-buttons>

                <ion-title>{{ title }}</ion-title>
                <ion-buttons slot="end">
                    <ion-button @click="dismiss()"><i class="fa fa-close" /></ion-button>
                </ion-buttons>
            </ion-toolbar>

            <!-- body slot: nội dung truyền từ bên ngoài -->
            <div class="px-4 pb-4 body-wrapper">
                <slot />
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
    title: string,
    goBack?: () => void
    isDisplayBackButton?: boolean
}>()

const { t } = useTranslate()
const modal = ref<InstanceType<typeof IonModal> | null>(null)
const isShowBackButton = ref(false)

// 👇 expose control
const present = () => modal.value?.$el.present()
const dismiss = () => modal.value?.$el.dismiss()

defineExpose({
    present,
    dismiss
})

provide("modalDismiss", dismiss)

const titlesNotDisplayBackButton = computed(() => [
    t("setting"), t("addFriend"), t("createGroup"), t("classificationTagManagement")
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

    console.log(props.title)
})
</script>

<style>
.custom-modal {
    --height: 85vh;
    /* 👈 chỉnh ở đây */
    --width: 500px;
    --border-radius: 12px;
}

/* Hoặc đơn giản hơn là ép div bọc slot */
ion-content::part(container) {
    height: 100%;
}

/* Ép inner content của Ionic sử dụng Flexbox */
.ion-content-layout::part(scroll) {
    display: flex;
    flex-direction: column;
    height: 100%;
    overflow: hidden;
    /* Ngăn scroll ở cấp độ này */
}

.body-wrapper {
    flex: 1;
    /* Chiếm toàn bộ phần còn lại sau khi trừ toolbar */
    min-height: 0;
    /* Quan trọng: để flex child có thể scroll */
    display: flex;
    flex-direction: column;
}
</style>