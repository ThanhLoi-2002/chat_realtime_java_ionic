<template>
    <div class="h-full">
        <!-- Header App mô phỏng Zalo -->
        <div
            :class="[oaStyle.text.primary, oaStyle.bg.secondary, 'px-3 py-2 flex items-center justify-between text-xs font-medium']">
            <i class="fas fa-chevron-left"></i>
            <span class="truncate max-w-45">{{ oa?.name }}</span>
            <div class="flex space-x-3">
                <i class="fas fa-ellipsis-h"></i>
            </div>
        </div>

        <!-- Màn hình nội dung bên trong máy ảo -->
        <div :class="[oaStyle.bg.primary, 'flex-1 flex flex-col text-xs h-full']">
            <!-- Ảnh bìa hiển thị real-time -->
            <div class="relative h-28 w-full shrink-0">
                <img v-if="oa?.cover" :src="oa.cover" alt="Cover Preview" class="w-full h-full object-center" />
            </div>

            <!-- Thông tin profile OA -->
            <div :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'px-4 pb-4 border-b relative']">
                <!-- Ảnh đại diện chồng lên ảnh bìa -->
                <div class="flex items-center gap-3 mt-3">
                    <div :class="[oaStyle.border.secondary, 'w-12 h-12 rounded-full border']">
                        <img v-if="oa?.avatar" :src="oa?.avatar" alt="Avatar Preview"
                            :class="[oaStyle.border.secondary, 'w-12 h-12 rounded-full border object-center shadow-sm']" />
                    </div>

                    <div>
                        <div class="flex items-center gap-2">
                            <span :class="[oaStyle.text.primary, 'font-medium text-sm truncate text-wrap max-w-45']">
                                {{ oa?.name || 'name' }}
                            </span>
                            <i
                                :class="[oa?.status == OaStatusEnum.ACTIVE ? 'text-green-600' : 'text-yellow-500', 'fas fa-check-circle text-[9px]']">
                            </i>
                        </div>

                        <p :class="[oaStyle.text.secondary, 'text-[10px] mt-0.5 truncate']">{{ oa?.categoryName || 'name' }}
                        </p>
                    </div>
                </div>


                <div class="mt-2">
                    <!-- Nút Quan tâm & Menu -->
                    <div class="flex items-center space-x-2 mt-3">
                        <button type="button"
                            class="flex-1 bg-blue-600 text-white py-1.5 rounded-full font-medium text-center shadow-sm">
                            Quan tâm
                        </button>
                        <button type="button"
                            :class="[oaStyle.border.secondary, 'w-8 h-8 rounded-full border flex items-center justify-center text-gray-600']">
                            <i class="fas fa-ellipsis"></i>
                        </button>
                    </div>

                    <div :class="[oaStyle.text.secondary, 'mt-2 text-[10px] flex items-center space-x-1']">
                        <i
                            :class="[oa?.status == OaStatusEnum.ACTIVE ? 'text-green-600' : 'text-yellow-500', 'fas fa-check-circle']"></i>
                        <span>OA này đã được xác thực bởi Zalo</span>
                    </div>
                </div>
            </div>

            <!-- Phần giới thiệu real-time -->
            <div :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'p-3 border-b flex-1']">
                <div v-if="oa?.description" :class="[oaStyle.text.primary, 'font-medium text-md mb-1']">Thông tin chi tiết</div>
                <p :class="[oaStyle.text.secondary, 'leading-relaxed whitespace-pre-line']">{{ oa?.description }}</p>

                <div :class="[oaStyle.text.secondary, 'mt-3 space-y-1.5']">
                    <div v-if="oa?.showAddress" class="flex items-start space-x-2">
                        <i class="fas fa-map-marker-alt mt-0.5"></i>
                        <span>{{ oa?.address }}</span>
                    </div>
                    <div v-if="oa?.showCallButton" class="flex items-center space-x-2">
                        <i class="fas fa-phone"></i>
                        <span>{{ oa?.phone }}</span>
                    </div>
                    <div v-if="oa?.workingHours" class="flex items-center space-x-2">
                        <i class="fas fa-clock"></i>
                        <span>{{ oa?.workingHours }}</span>
                    </div>
                </div>
            </div>

        </div>
    </div>
</template>
<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import { OaType } from '@/types/entities';
import { OaStatusEnum } from '@/types/enum';

const props = defineProps<{
    oa: OaType | undefined
}>()

</script>