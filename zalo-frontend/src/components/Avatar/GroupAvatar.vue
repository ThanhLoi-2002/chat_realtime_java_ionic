<template>
    <div class="cursor-pointer" :onClick="() => !isDisabled && groupProfileModal?.present()">
        <!-- 1 USER -->
        <div v-if="count === 1" :class="size ?? 'w-10 h-10'">
            <img :src="users[0]?.avatar?.url ?? RANDOM_AVATAR" class="w-full h-full object-cover rounded-full" />
        </div>

        <!-- 2 USERS -->
        <div v-else-if="count === 2" :class="[size ?? 'w-10 h-10', 'flex']">
            <img :src="users[0]?.avatar?.url ?? RANDOM_AVATAR" class="w-1/2 h-full object-cover rounded-l-full" />
            <img :src="users[1]?.avatar?.url ?? RANDOM_AVATAR" class="w-1/2 h-full object-cover rounded-r-full" />
        </div>

        <!-- >= 3 USERS -->
        <div v-else :class="[size ?? 'w-10 h-10', 'grid grid-cols-2 grid-rows-2 gap-0.5']">

            <!-- User 1 -->
            <img :src="users[0]?.avatar?.url ?? RANDOM_AVATAR" class="w-full h-full object-cover rounded-tl-full" />

            <!-- User 2 -->
            <img :src="users[1]?.avatar?.url ?? RANDOM_AVATAR" class="w-full h-full object-cover rounded-tr-full" />

            <!-- Case = 3 -->
            <div v-if="count === 3" class="col-span-2 flex justify-center">
                <img :src="users[2]?.avatar?.url ?? RANDOM_AVATAR" class="w-full h-full object-cover rounded-b-full" />
            </div>

            <!-- Case >= 4 -->
            <template v-else>
                <!-- User 3 -->
                <img :src="users[2]?.avatar?.url ?? RANDOM_AVATAR" class="w-full h-full object-cover" />

                <!-- User 4 hoặc +N -->
                <div class="w-full h-full flex items-center justify-center
               bg-gray-500 text-white text-xs">
                    <template v-if="count === 4">
                        <img :src="users[3]?.avatar?.url ?? RANDOM_AVATAR" class="w-full h-full object-cover" />
                    </template>
                    <template v-else>
                        {{ count }}
                    </template>
                </div>
            </template>

        </div>
    </div>

    <Modal ref="groupProfileModal" :title="t('groupProfile')">
        <transition name="slide">
            <GroupProfile v-if="view == 'groupInfo'" :conversation="conversation" @update:view="view = $event" />

            <Member v-else-if="view == 'member'" @back="view = 'groupInfo'" :is-show-back-button="true" />
        </transition>
    </Modal>
</template>

<script setup lang="ts">
import { computed, defineAsyncComponent, ref } from "vue";
import { ConversationType } from "@/types/entities";
import { RANDOM_AVATAR } from "@/utils/constant";
import { useTranslate } from "@/composables/useTranslate";
import Modal from "../Modal/Modal.vue";

const GroupProfile = defineAsyncComponent(() => import('../../views/Chat/component/GroupProfile.vue'));
const Member = defineAsyncComponent(() => import('../../views/Chat/Info/components/Member.vue'));

const props = defineProps<{
    conversation: ConversationType;
    size?: string
    isDisabled?: boolean
}>();

const { t } = useTranslate()
const groupProfileModal = ref()
const view = ref<'groupInfo' | 'member'>('groupInfo')

// ✅ luôn dùng computed để giữ reactivity
const users = computed(() => props.conversation.members || []);
const count = computed(() => users.value.length);
</script>