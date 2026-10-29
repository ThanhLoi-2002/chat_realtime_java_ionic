<script setup lang="ts">
import { style } from '@/assets/tailwindcss'
import LoadingSpinner from '@/components/Shared/Loading/LoadingSpinner.vue'
import { useTranslate } from '@/composables/useTranslate'
import router from '@/router'
import { usePaymentStore } from '@/stores/App/payment.storage'
import { useSubscriptionPackageStore } from '@/stores/App/subscriptionPackage.storage'
import { SubscriptionPackageType } from '@/types/entities'
import { SubscriptionCategoryEnum, TargetTypeEnum } from '@/types/enum'
import { ROUTE } from '@/utils/constant'
import { formatCurrencyVND } from '@/utils/helper'
import { computed, onMounted, ref } from 'vue'

const subPackageStor = useSubscriptionPackageStore()
const paymentStor = usePaymentStore()
const subPackagesByBillingCycle = ref<Record<string, SubscriptionPackageType[]>>({})
const isLoading = ref(false)
const { t } = useTranslate()

// State quản lý tab đang chọn (Ví dụ: 'YEARLY', 'MONTHLY' hoặc 'LIFETIME' tùy thuộc vào giá trị trả về từ backend của bạn)
const activeTab = ref<string>('YEARLY')

// Danh sách các tab chu kỳ thanh toán có sẵn từ dữ liệu API
const billingCycles = computed(() => Object.keys(subPackagesByBillingCycle.value))

// Lấy danh sách gói cước tương ứng với tab đang chọn
const currentPlans = computed(() => {
    if (!subPackagesByBillingCycle.value[activeTab.value]) return []
    return subPackagesByBillingCycle.value[activeTab.value]
})

const handleSelectPlan = async (planId: number) => {
    isLoading.value = true
    const data = await paymentStor.createSubscriptionOrderPayment(planId)
    router.push(`${ROUTE.APP.QR_CODE_CHECKOUT}?planId=${planId}&code=${data.code}`)
    isLoading.value = false
}

onMounted(async () => {
    subPackagesByBillingCycle.value = await subPackageStor.getAvailablePackages({
        category: SubscriptionCategoryEnum.BUSINESS,
        targetType: TargetTypeEnum.USER
    })

    // Tự động chọn tab đầu tiên nếu có dữ liệu
    const cycles = Object.keys(subPackagesByBillingCycle.value)
    if (cycles.length > 0) {
        activeTab.value = cycles[0]
    }
})
</script>

<template>
    <div :class="[style.bg.primary, style.text.primary, 'h-full overflow-auto py-12 px-4']">
        <!-- Header -->
        <div class="text-center mb-8">
            <h1 class="text-3xl md:text-4xl font-bold mb-2">Chọn gói phù hợp với bạn</h1>
            <p :class="[style.text.muted, 'text-sm']">Sau khi đăng ký, bạn có thể hủy gia hạn bất cứ lúc nào</p>
        </div>

        <!-- Tab Chuyển đổi Gói (Tháng / Năm / ...) -->
        <div class="flex justify-center mb-10">
            <div :class="[style.bg.secondary, 'p-1.5 rounded-full flex gap-2 border shadow-sm']">
                <button v-for="cycle in billingCycles" :key="cycle" @click="activeTab = cycle" :class="[
                    'px-6 py-2 rounded-full text-sm font-semibold transition-all duration-200',
                    activeTab === cycle
                        ? 'bg-blue-600 text-white shadow-md'
                        : `${style.text.muted} hover:${style.text.primary}`
                ]">
                    <!-- Hiển thị tên hiển thị tùy chỉnh cho tab (Ví dụ: Gói năm, Gói tháng) -->
                    {{ cycle === 'YEARLY' ? 'Gói năm' : cycle === 'MONTHLY' ? 'Gói tháng' : cycle }}
                    <span v-if="cycle === 'YEARLY'"
                        class="ml-1 text-[10px] bg-orange-100 text-orange-600 px-1.5 py-0.5 rounded-full font-bold">Tiết
                        kiệm</span>
                </button>
            </div>
        </div>

        <!-- Grid Các gói -->
        <div class="max-w-5xl mx-auto grid grid-cols-1 md:grid-cols-2 gap-8 items-stretch">
            <div v-for="(plan, index) in currentPlans" :key="plan.id || index" :class="[
                style.bg.secondary,
                'rounded-2xl p-8 flex flex-col justify-between transition-all duration-200 relative shadow-md',
                // Đánh dấu gói thứ 2 (hoặc tuỳ logic highlight của bạn) là nổi bật
                index === 1
                    ? 'border-2 border-blue-600 shadow-xl'
                    : `border ${style.border.secondary} hover:shadow-lg`
            ]">
                <div>
                    <!-- Tên & Mô tả -->
                    <h2 class="text-3xl font-bold mb-1">{{ plan.level }}</h2>
                    <p :class="[style.text.secondary, 'text-sm mb-6 min-h-8']">{{ plan.description }}</p>

                    <!-- Giá -->
                    <div class="mb-6">
                        <div class="flex items-baseline gap-1">
                            <span class="text-3xl font-ext500 font-bold">
                                {{ formatCurrencyVND(plan.monthlyPrice) }}đ
                            </span>
                            <span :class="[style.text.secondary, 'text-sm font-semibold']">/tháng</span>
                        </div>
                        <p v-if="plan.price" class="text-xs text-slate-400 mt-1">
                            {{ formatCurrencyVND(plan.price) }}đ cho {{ plan.billingCycle ===
                                'YEARLY' ? 'gói 12 tháng' : plan.billingCycle.toLowerCase() }}
                        </p>
                    </div>

                    <!-- Nút hành động -->
                    <button @click="() => !isLoading && handleSelectPlan(plan.id)" :class="[
                        'w-full py-3 px-4 rounded-xl font-semibold text-sm transition-colors mb-6 shadow-sm',
                        index === 1
                            ? 'bg-blue-600 hover:bg-blue-700 text-white'
                            : `${style.bg.tertiary} ${style.bg.hover1} ${style.text.primary} border ${style.border.secondary}`
                    ]">
                        <LoadingSpinner v-if="isLoading" />
                        <p v-else>{{ t('upgradeNow') }}</p>
                    </button>

                    <!-- Danh sách tính năng (Nếu backend có trả về mảng features) -->
                    <div class="space-y-3 text-xs">
                        <ul class="space-y-3 pl-1">
                            <li v-for="(feature, fIdx) in (plan.features || [])" :key="fIdx"
                                :class="[style.text.secondary, 'flex items-start gap-2.5']">

                                <!-- Icon check -->
                                <i class="fas fa-check text-blue-600 mt-0.5 shrink-0"></i>

                                <div class="flex-1">
                                    <!-- Dòng chính: Tên tính năng + Tag (nếu có) -->
                                    <div class="flex items-center gap-2 flex-wrap">
                                        <span class="font-medium text-slate-800 dark:text-slate-100 text-sm"
                                            v-html="feature.text"></span>

                                        <!-- Tag / Badge (Ví dụ: Business, Mới) -->
                                        <span v-if="feature.tag" :class="[
                                            'text-[11px] font-bold px-1.5 py-0.5 rounded border',
                                            feature.tag.toLowerCase() === 'business'
                                                ? 'bg-blue-50 text-blue-600 border-blue-200'
                                                : 'bg-orange-100 text-orange-600 border-orange-200'
                                        ]">
                                            {{ feature.tag }}
                                        </span>
                                    </div>

                                    <!-- Mô tả phụ (subtext) nghiêng bên dưới (nếu có) -->
                                    <p v-if="feature.subtext" class="text-[11px] text-slate-400 mt-0.5 font-normal">
                                        {{ feature.subtext }}
                                    </p>
                                </div>
                            </li>
                        </ul>
                    </div>

                    <!-- Quà tặng kèm (Nếu có) -->
                    <div v-if="plan.gifts && plan.gifts.length > 0" class="mt-6 space-y-2">
                        <p class="text-sm font-bold text-slate-700 dark:text-slate-300">Tặng kèm dịch vụ</p>

                        <div v-for="(gift, gIdx) in plan.gifts" :key="gIdx"
                            class="bg-blue-50/60 dark:bg-slate-800/80 rounded-xl p-3 flex items-start justify-between border border-blue-100 dark:border-slate-700">
                            <div class="flex items-start gap-2.5">
                                <!-- Hiển thị Icon động tùy chỉnh -->
                                <div
                                    class="w-7 h-7 rounded-full bg-blue-100 dark:bg-blue-900/50 flex items-center justify-center text-blue-600 dark:text-blue-400 shrink-0 mt-0.5">
                                    <p v-html="gift.icon" class="text-xs"></p>
                                </div>
                                <div>
                                    <p class="text-xs font-semibold text-slate-800 dark:text-slate-200 leading-tight">
                                        {{ gift.title }}
                                    </p>
                                    <p v-if="gift.note" class="text-[10px] text-slate-500 dark:text-slate-400 mt-0.5">
                                        ({{ gift.note }})
                                    </p>
                                </div>
                            </div>
                            <span
                                :class="[style.text.muted, 'text-xs cursor-pointer hover:text-slate-600 shrink-0']">ⓘ</span>
                        </div>
                    </div>

                    <!-- Quà tặng kèm (Nếu có savingsAmount hoặc savingsPercentage hoặc gift) -->
                    <div v-if="plan.savingsPercentage || plan.savingsAmount" class="mt-6">
                        <div
                            class="bg-blue-50/60 rounded-xl p-3 flex items-center justify-between border border-blue-100">
                            <div class="flex items-center gap-2.5">
                                <div
                                    class="w-7 h-7 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 shrink-0">
                                    <i class="fas fa-gift text-xs"></i>
                                </div>
                                <div>
                                    <p class="text-[11px] font-semibold text-slate-800 leading-tight">
                                        Tiết kiệm đến {{ plan.savingsPercentage }}% khi nâng cấp gói
                                    </p>
                                </div>
                            </div>
                            <span :class="[style.text.muted, 'text-xs cursor-pointer']">ⓘ</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>