<script setup lang="ts">
import { style } from '@/assets/tailwindcss'
import LoadingSpinner from '@/components/Shared/Loading/LoadingSpinner.vue'
import router from '@/router'
import { usePaymentStore } from '@/stores/App/payment.storage'
import { useSubscriptionPackageStore } from '@/stores/App/subscriptionPackage.storage'
import { useUserStore } from '@/stores/App/user.storage'
import { SubscriptionPackageType } from '@/types/entities'
import { BillingCycleEnum } from '@/types/enum'
import { ROUTE } from '@/utils/constant'
import { formatCurrencyVND } from '@/utils/helper'
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'

const userStor = useUserStore()
const paymentMethod = ref<'qr' | 'card'>('qr')
const countdown = ref(881) // 14 phút 41 giây = 881s
let timer: number | null = null
const route = useRoute()
const { planId, code } = route.query
const subPackagesStor = useSubscriptionPackageStore()
const paymentStor = usePaymentStore()
const plan = ref<SubscriptionPackageType | undefined>(undefined)
const isLoading = ref<boolean>(false)
const paymentInfo = ref<{ qrCodeUrl: string }>()

// Định dạng mm:ss
const formatTime = (seconds: number) => {
    const m = Math.floor(seconds / 60)
    const s = seconds % 60
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

// utils/dateHelper.ts hoặc viết trực tiếp trong component
const calculateExpirationDate = (billingCycle?: BillingCycleEnum): string => {
    const expDate = new Date()

    if (!billingCycle) return expDate.toLocaleDateString('vi-VN', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    })

    // Kiểm tra chu kỳ thanh toán
    switch (billingCycle.toUpperCase()) {
        case BillingCycleEnum.MONTHLY:
            expDate.setMonth(expDate.getMonth() + 1) // Cộng thêm 1 tháng
            break
        case BillingCycleEnum.YEARLY:
            expDate.setFullYear(expDate.getFullYear() + 1) // Cộng thêm 1 năm
            break
        default:
            expDate.setMonth(expDate.getMonth() + 1) // Mặc định 1 tháng
            break
    }

    return expDate.toLocaleDateString('vi-VN', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    })
}

const cancel = () => {
    router.push(ROUTE.APP.UPGRADE_BUSINESS)
}

onMounted(async () => {
    timer = window.setInterval(() => {
        if (countdown.value > 0) {
            countdown.value--
        }
    }, 1000)

    isLoading.value = true

    try {
        plan.value = await subPackagesStor.getById(Number(planId))
        paymentInfo.value = await paymentStor.getSubscriptionOrderPaymentInfo(code ? code?.toString() : '')
    } finally {
        isLoading.value = false
    }
})

onUnmounted(() => {
    if (timer) clearInterval(timer)
})
</script>

<template>
    <LoadingSpinner v-if="isLoading" />
    <div v-else :class="[style.bg.primary, style.text.primary, 'h-full overflow-auto']">
        <!-- Navbar Header -->
        <header :class="[style.border.secondary, 'border-b py-3 px-8 flex justify-between items-center']">
            <div class="flex items-center gap-1 text-2xl text-blue-600">
                zBox
            </div>
            <div class="flex items-center gap-2 cursor-pointer">
                <img :src="userStor.user?.avatar.url" alt="Avatar"
                    :class="[style.border.secondary, 'w-8 h-8 rounded-full border']" />
                <span :class="[style.text.secondary, 'text-sm font-semibold']">{{ userStor.user?.username }}</span>
            </div>
        </header>

        <!-- Main Content -->
        <main class="max-w-6xl mx-auto py-10 px-4 grid grid-cols-1 md:grid-cols-12 gap-8">
            <!-- Cột Trái: Chọn phương thức & Quét QR (8 cols) -->
            <div class="md:col-span-7 space-y-4">
                <h2 class="text-lg font-bold mb-4">Chọn phương thức thanh toán</h2>

                <!-- Option 1: Mã QR -->
                <div :class="[
                    'border rounded-xl p-4 cursor-pointer transition-all',
                    paymentMethod === 'qr' ? 'border-blue-600 bg-blue-50/20 dark:bg-slate-800' : 'border-slate-200 bg-white'
                ]" @click="paymentMethod = 'qr'">
                    <div class="flex items-center justify-between">
                        <div class="flex items-center gap-3">
                            <input type="radio" name="payment" checked
                                class="w-4 h-4 text-blue-600 focus:ring-blue-500" />
                            <div>
                                <div class="flex items-center gap-2">
                                    <span class="font-bold text-sm">Mã QR</span>
                                    <span
                                        :class="[style.bg.secondary, style.text.secondary, 'text-[11px] px-2 py-0.5 rounded']">
                                        Không tự gia hạn
                                    </span>
                                </div>
                                <p :class="[style.text.secondary, 'text-xs mt-0.5']">Quét bằng Zalo và app ngân hàng</p>
                            </div>
                        </div>
                        <!-- Icon QR -->
                        <div class="text-teal-600">
                            <i class="fas fa-qrcode"></i>
                        </div>
                    </div>

                    <!-- Khung Mã QR (Hiển thị khi chọn QR) -->
                    <div v-if="paymentMethod === 'qr'"
                        :class="[style.bg.secondary, 'mt-4 py-4 px-4 rounded-xl flex flex-col items-center']">
                        <p :class="[style.text.secondary, 'font-bold text-sm mb-6']">Quét mã QR để thanh toán</p>

                        <!-- Mã QR Frame -->
                        <div class="relative p-4 bg-white rounded-xl shadow-sm border border-slate-100">
                            <!-- Góc viền xanh trang trí -->
                            <div class="absolute -top-1 -left-1 w-4 h-4 border-t-2 border-l-2 border-blue-600"></div>
                            <div class="absolute -top-1 -right-1 w-4 h-4 border-t-2 border-r-2 border-blue-600"></div>
                            <div class="absolute -bottom-1 -left-1 w-4 h-4 border-b-2 border-l-2 border-blue-600"></div>
                            <div class="absolute -bottom-1 -right-1 w-4 h-4 border-b-2 border-r-2 border-blue-600">
                            </div>

                            <!-- Placeholder QR Image -->
                            <img :src="paymentInfo?.qrCodeUrl" alt="QR Code" class="w-80 h-full object-contain" />
                        </div>

                        <!-- Đồng hồ đếm ngược -->
                        <div
                            class="mt-8 bg-amber-50 border border-amber-100 rounded-full px-4 py-1.5 flex items-center gap-2 text-xs text-slate-700">
                            <span class="text-amber-500 font-bold">ⓘ</span>
                            <span>Mã QR chỉ có hiệu lực cho lần thanh toán này. Hết hạn sau:</span>
                            <span class="font-bold text-slate-900">{{ formatTime(countdown) }}</span>
                        </div>
                    </div>
                </div>

                <!-- Bảo mật Footer -->
                <div class="flex items-center gap-2 text-xs text-slate-500 pt-2">
                    <svg class="w-4 h-4 text-emerald-600 shrink-0" fill="currentColor" viewBox="0 0 20 20">
                        <path fill-rule="evenodd"
                            d="M2.166 4.999A11.954 11.954 0 0010 1.944 11.954 11.954 0 0017.834 5c.11.65.166 1.32.166 2.001 0 5.225-3.34 9.67-8 11.317C5.34 16.67 2 12.225 2 7c0-.682.057-1.35.166-2.001zm11.541 3.708a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                            clip-rule="evenodd" />
                    </svg>
                    <span>Thông tin thanh toán được mã hóa và bảo mật bởi Đối Tác thanh toán được Ngân hàng Nhà nước cấp
                        phép.</span>
                </div>
            </div>

            <!-- Cột Phải: Chi tiết giao dịch (5 cols) -->
            <div class="md:col-span-5 space-y-6">
                <h2 class="text-lg font-bold">Chi tiết giao dịch</h2>

                <div
                    :class="[style.border.secondary, style.bg.secondary, 'border rounded-xl overflow-hidden shadow-sm']">
                    <!-- Tài khoản dịch vụ -->
                    <div :class="[style.border.secondary, 'p-4 border-b flex justify-between items-center text-xs']">
                        <span :class="[style.text.primary]">Tài khoản mua dịch vụ</span>
                        <div :class="[style.text.secondary, 'flex items-center gap-2 font-semibold']">
                            <span>{{ userStor.user?.username }}</span>
                            <img :src="userStor.user?.avatar.url" alt="Avatar" class="w-6 h-6 rounded-full" />
                        </div>
                    </div>

                    <!-- Thông tin Sản phẩm -->
                    <div :class="[style.border.secondary, 'p-4 border-b']">
                        <p :class="[style.text.primary, 'text-xs font-bold mb-3']">Sản phẩm</p>
                        <div class="flex justify-between items-start text-xs">
                            <div>
                                <p :class="[style.text.primary, 'font-semibold']">{{ plan?.name }}</p>
                                <p :class="[style.text.muted, 'mt-1']">Hạn dùng {{
                                    calculateExpirationDate(plan?.billingCycle) }}</p>
                            </div>
                            <span :class="[style.text.secondary, 'font-semibold']">{{ formatCurrencyVND(plan?.price)
                            }}đ</span>
                        </div>
                    </div>

                    <!-- Tổng tiền -->
                    <div class="p-4">
                        <div class="flex justify-between items-baseline">
                            <span :class="[style.text.primary, 'font-bold text-sm']">Tổng tiền</span>
                            <span :class="[style.text.secondary, 'text-xl font-extrabold']">{{
                                formatCurrencyVND(plan?.price) }}đ</span>
                        </div>
                        <p :class="[style.text.muted, 'text-[11px] text-right mt-1']">Đã bao gồm VAT và phí liên quan
                        </p>
                    </div>
                </div>

                <!-- Nút Hủy -->
                <div class="text-center pt-2">
                    <button
                        :class="[style.text.primary, style.bg.hoverActive, style.bg.active, 'text-sm font-bold p-4 rounded-xl w-full']"
                        @click="cancel">
                        Hủy giao dịch
                    </button>
                </div>
            </div>
        </main>
    </div>
</template>