<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss'
import router from '@/router'
import { useOaStore } from '@/stores/Oa/oa.storage'
import { OaType } from '@/types/entities'
import { OaStatusEnum, OaVerifiedEnum } from '@/types/enum'
import { MINIO_URL, OA_ID, OA_ROUTE } from '@/utils/constant'
import { setKey } from '@/utils/local'
import { onMounted, ref } from 'vue'

const oaStor = useOaStore()
// Trạng thái tab đang chọn
const activeTab = ref<string>('active')

// Danh sách các tab
const tabs = [
    { key: 'all', label: 'Tất cả' },
    { key: 'active', label: 'Đang hoạt động' },
    { key: 'pending', label: 'Đang chờ duyệt' },
    { key: 'locked', label: 'Đang bị khoá' },
]

// Dữ liệu mẫu dựa trên hình ảnh
const accounts = ref<OaType[]>([])

// Hàm xử lý khi bấm các nút
const handleCreateOA = () => {
    console.log('Tạo Official Account mới')
}

const handleRevokeAdmin = (id: number) => {
    console.log('Thôi làm Admin cho OA ID:', id)
}

const selectOa = (id: number) => {
    setKey(OA_ID, id)
    router.push(OA_ROUTE.home)
}

onMounted(async () => {
    accounts.value = await oaStor.getAllOas()
})
</script>

<template>
    <div :class="[oaStyle.text.primary, oaStyle.bg.primary, 'p-6 h-full']">
        <!-- Tiêu đề trang -->
        <div :class="[oaStyle.text.primary, 'text-xl font-medium mb-6']">Quản lý Official Account</div>

        <!-- Thanh điều hướng tab và nút tạo mới -->
        <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-6">
            <!-- Tabs -->
            <div class="flex items-center space-x-6 text-sm">
                <button v-for="tab in tabs" :key="tab.key" @click="activeTab = tab.key" :class="[
                    'pb-1 transition-colors duration-200 font-medium',
                    activeTab === tab.key
                        ? 'text-blue-400 border-b-2 border-blue-400'
                        : `${oaStyle.text.secondary} hover:text-gray-500`
                ]">
                    {{ tab.label }}
                </button>
            </div>

            <!-- Nút Tạo Official Account mới -->
            <button @click="handleCreateOA"
                class="bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium px-4 py-2 rounded shadow-sm transition-colors flex items-center justify-center gap-1">
                <span>Tạo Official Account mới</span>
            </button>
        </div>

        <!-- Bảng dữ liệu -->
        <div :class="[oaStyle.bg.primary, oaStyle.border.secondary, 'shadow-sm rounded-lg overflow-hidden border']">
            <div class="overflow-x-auto">
                <table class="w-full text-left border-collapse">
                    <!-- Tiêu đề bảng -->
                    <thead>
                        <tr
                            :class="[oaStyle.bg.secondary, oaStyle.border.secondary, oaStyle.text.secondary, 'border-b text-xs font-medium uppercase tracking-wider']">
                            <th class="py-3 px-4 w-12 text-center">#</th>
                            <th class="py-3 px-4">Avatar</th>
                            <th class="py-3 px-4">Tên Official Account</th>
                            <th class="py-3 px-4">Danh mục chính</th>
                            <th class="py-3 px-4">Trạng thái</th>
                            <th class="py-3 px-4">Người tạo</th>
                            <th class="py-3 px-4 text-right">Thao tác</th>
                        </tr>
                    </thead>

                    <!-- Nội dung bảng -->
                    <tbody class="divide-y divide-gray-100 text-sm">
                        <tr v-for="item in accounts" :key="item.id" :class="[oaStyle.bg.hover, 'transition-colors']">
                            <!-- Cột ID STT -->
                            <td :class="[oaStyle.text.secondary, 'py-4 px-4 text-center font-medium']">{{ item.id }}
                            </td>

                            <!-- Cột OA ID -->
                            <td :class="[oaStyle.text.secondary, 'py-4 px-4 font-mono text-xs max-w-45 break-all']">
                                <img :src="`${MINIO_URL}/${item.avatar}`" alt="Avatar" class="w-10 h-10 rounded-full object-cover" />
                            </td>

                            <!-- Cột Tên Official Account & Badge -->
                            <td class="py-4 px-4">
                                <div @click="() => item.status == OaStatusEnum.ACTIVE && selectOa(item.id)"
                                    :class="[item.status == OaStatusEnum.ACTIVE && 'cursor-pointer hover:underline', 'font-medium text-blue-600 mb-1.5']">
                                    {{ item.name }}
                                </div>
                                <div class="flex items-center gap-2 flex-wrap">
                                    <!-- Badge Tài khoản xác thực -->
                                    <span v-if="item.verified == OaVerifiedEnum.VERIFIED"
                                        class="bg-blue-600 text-white text-[11px] font-medium px-2 py-0.5 rounded shadow-sm">
                                        Tài khoản xác thực
                                    </span>
                                    <span v-if="item.status == OaStatusEnum.PENDING"
                                        class="bg-gray-600 text-white text-[11px] font-medium px-2 py-0.5 rounded shadow-sm">
                                        Tài khoản chờ duyệt
                                    </span>
                                    <!-- Badge Gói OA Nâng cao -->
                                    <!-- <span v-if="item.isAdvanced"
                                        class="bg-white border border-blue-400 text-blue-600 text-[11px] font-medium px-2 py-0.5 rounded">
                                        Gói OA Nâng cao
                                    </span> -->
                                </div>
                            </td>

                            <!-- Cột Danh mục chính -->
                            <td :class="[oaStyle.text.secondary, 'py-4 px-4']">{{ item.category }}</td>

                            <!-- Cột Trạng thái -->
                            <td :class="[oaStyle.text.secondary, 'py-4 px-4']">{{ item.status }}</td>

                            <!-- Cột Người tạo -->
                            <td :class="[oaStyle.text.secondary, 'py-4 px-4']">{{ item.cu }}</td>

                            <!-- Cột Thao tác -->
                            <td class="py-4 px-4 text-right">
                                <button @click="handleRevokeAdmin(item.id)"
                                    :class="[oaStyle.text.secondary, oaStyle.border.secondary, oaStyle.bg.hover1, 'border text-xs font-medium px-3 py-1.5 rounded transition-colors']">
                                    Thôi làm Admin
                                </button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>