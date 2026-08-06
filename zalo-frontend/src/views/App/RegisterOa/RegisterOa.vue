<template>
    <div :class="[oaStyle.text.primary, oaStyle.bg.primary, 'mx-auto p-4 h-full']">
        <div :class="[oaStyle.border.secondary, oaStyle.text.primary, 'h-14 border-b flex items-center px-4']">
            <div :class="['flex-1 text-md']">
                {{ t('createBusinessOa') }}
            </div>

            <button @click="goBack" class="w-10 cursor-pointer">
                <i class="fas fa-times"></i>
            </button>
        </div>

        <form @submit="save" class="grid grid-cols-1 lg:grid-cols-12 items-start h-full p-4 overflow-auto">
            <!-- CỘT TRÁI: FORM NHẬP LIỆU (CHIẾM 8 CỘT) -->
            <div class="lg:col-span-8 space-y-6">

                <!-- 1. DANH MỤC HOẠT ĐỘNG -->
                <ErrorSelect label="category" name="category" :defineField="defineField" :errors="errors"
                    :schema="oaSchema" placeholder="Chọn danh mục phù hợp" :options="categories"
                    direction="horizontal" />

                <!-- 2. TÊN OFFICIAL ACCOUNT -->
                <ErrorInput :errors="errors" name="name" label="oaName" :define-field="defineField" :schema="oaSchema"
                    placeholder="oaName" direction="horizontal" />

                <!-- 3. THÔNG TIN GIỚI THIỆU -->
                <ErrorInput :errors="errors" name="description" label="description" :define-field="defineField"
                    :schema="oaSchema" :is-textarea="true" placeholder="description" direction="horizontal" />

                <AddressSelect label="oaAddress" :errors="errors" :names="['province', 'district', 'address']"
                    :define-field="defineField" :schema="oaSchema" direction="horizontal" />

                <!-- 4. CHỌN ẢNH ĐẠI DIỆN & ẢNH BÌA TỪ MÁY -->
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <!-- Avatar Upload -->
                    <div class="space-y-1.5">
                        <label class="block text-sm font-semibold">Ảnh đại diện</label>
                        <div class="flex items-center gap-3">
                            <!-- Click trực tiếp vào khung tròn ảnh đại diện để mở input file -->
                            <div @click="avatarInputRef?.click()"
                                class="w-12 h-12 rounded-full bg-gray-200 dark:bg-gray-700 overflow-hidden shrink-0 border border-gray-300 dark:border-gray-600 flex items-center justify-center cursor-pointer hover:opacity-90 transition group relative">
                                <img v-if="values.avatar" :src="values.avatar" class="w-full h-full object-center" />
                                <i v-else class="fas fa-camera text-gray-400 text-xs"></i>

                                <!-- Hiệu ứng lớp phủ icon máy ảnh khi hover vào ảnh -->
                                <div
                                    class="absolute inset-0 bg-black/30 opacity-0 group-hover:opacity-100 transition flex items-center justify-center rounded-full">
                                    <i class="fas fa-camera text-white text-[10px]"></i>
                                </div>
                            </div>

                            <!-- Input file giữ nguyên -->
                            <input ref="avatarInputRef" type="file" accept="image/*" class="hidden"
                                @change="onChangeAvatar" />
                        </div>
                        <p class="text-[11px] text-gray-400">Tối thiểu 240x240 (JPG, PNG). Tối đa 15MB.</p>
                        <span v-if="errors['avatar'] && submitCount > 0" class="text-red-500 text-sm mt-1 block">
                            {{ t(errors['avatar']) }}
                        </span>
                    </div>

                    <!-- Cover Upload -->
                    <div class="space-y-1.5">
                        <label class="block text-sm font-semibold">Ảnh bìa</label>
                        <div class="flex items-center gap-3">
                            <!-- Click vào khung ảnh để mở input file -->
                            <div @click="coverInputRef?.click()"
                                class="w-16 h-16 rounded bg-gray-200 dark:bg-gray-700 overflow-hidden shrink-0 border border-gray-300 dark:border-gray-600 flex items-center justify-center cursor-pointer hover:opacity-90 transition group relative">
                                <img v-if="values.cover" :src="values.cover" class="w-full h-full object-center" />
                                <i v-else class="fas fa-image text-gray-400 text-xs"></i>

                                <!-- (Tùy chọn) Hiệu ứng nhỏ khi hover vào ảnh để người dùng biết là bấm được -->
                                <div
                                    class="absolute inset-0 bg-black/20 opacity-0 group-hover:opacity-100 transition flex items-center justify-center">
                                    <i class="fas fa-camera text-white text-xs"></i>
                                </div>
                            </div>

                            <!-- Input file ẩn giữ nguyên -->
                            <input ref="coverInputRef" type="file" accept="image/*" class="hidden"
                                @change="onFileSelected" />
                        </div>
                        <p class="text-[11px] text-gray-400">Tối thiểu 320x180 (JPG, PNG). Tối đa 15MB.</p>
                        <span v-if="errors['cover'] && submitCount > 0" class="text-red-500 text-sm mt-1 block">
                            {{ t(errors['cover']) }}
                        </span>
                    </div>
                </div>

                <!-- NÚT HÀNH ĐỘNG -->
                <div class="flex items-center justify-end gap-3 pt-4 border-t border-gray-200 dark:border-gray-800">
                    <button type="button" @click="goBack"
                        class="px-5 py-2.5 rounded-lg border border-gray-300 dark:border-gray-700 hover:bg-gray-100 dark:hover:bg-gray-800 text-sm font-medium transition-colors">
                        {{ t('cancel') }}
                    </button>
                    <button type="submit" :disabled="isLoading"
                        class="px-6 py-2.5 rounded-lg bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold transition-colors flex items-center gap-2 disabled:opacity-50 shadow-sm">
                        <span v-if="isLoading" class="animate-spin text-xs">⏳</span>
                        <span>{{ t('create') }}</span>
                    </button>
                </div>
            </div>

            <!-- CỘT PHẢI: PREVIEW GIAO DIỆN ĐIỆN THOẠI -->
            <div class="mx-auto lg:col-span-4">
                <VirtualMobile>
                    <OaInforMobile :oa="oa" />
                </VirtualMobile>
            </div>
        </form>

        <!-- Gọi Modal Cắt ảnh -->
        <ImageCropperModal v-model="showCropper" :imgSrc="values.cover" :aspect-ratio="16 / 9" title="changeCover"
            @cropped="onCropperResult" />
    </div>
</template>

<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss'
import ErrorInput from '@/components/Shared/Form/ErrorInput.vue'
import ErrorSelect from '@/components/Shared/Form/ErrorSelect.vue'
import ImageCropperModal from '@/components/Shared/Modal/ImageCropperModal.vue'
import AddressSelect from '@/components/Shared/Form/AddressSelect.vue'
import OaInforMobile from '@/components/Shared/VirtualMobile/OaInforMobile.vue'
import VirtualMobile from '@/components/Shared/VirtualMobile/VirtualMobile.vue'
import { useTranslate } from '@/composables/useTranslate'
import router from '@/router'
import { oaSchema } from '@/schema/Oa/oa.schema'
import { toast } from '@/utils/toast'
import { toTypedSchema } from '@vee-validate/yup'
import { useForm } from 'vee-validate'
import { computed, onMounted, ref, watch } from 'vue'
import { useOaStore } from '@/stores/Oa/oa.storage'
import { useUploadMinio } from '@/composables/useUploadMinio'
import { OA_ROUTE } from '@/utils/constant'
import { goBack } from '@/utils/helper'
import { SelectOptionType } from '@/types/common'
import { useOaCategoryStore } from '@/stores/Admin/oaCategory.storage'
import { OaCategoryType } from '@/types/entities'

const emit = defineEmits<{
    (e: 'submit', data: FormData): void
    (e: 'cancel'): void
}>()

const oaStor = useOaStore()
const { uploadFile } = useUploadMinio()
const isLoading = ref(false)
const avatarFile = ref<File | null>(null)
const coverFile = ref<File | null>(null)
const categories = ref<SelectOptionType[]>([])
const categoryStor = useOaCategoryStore()

const showCropper = ref(false)

const { t } = useTranslate()
const oa = computed<any>(() => ({
    ...values, display: {
        showDescription: true,
        showAddress: false,
        showPhone: false,
        showWebsite: false,
        showWorkingHours: false,
        showCallButton: false,
    },
}));

// Thêm các ref để điều khiển DOM cho thẻ input file ẩn
const avatarInputRef = ref<HTMLInputElement | null>(null)
const coverInputRef = ref<HTMLInputElement | null>(null)

const { handleSubmit, errors, defineField, values, setFieldValue, submitCount } = useForm({
    validationSchema: toTypedSchema(oaSchema)
});

// Xử lý chọn file Avatar
const onChangeAvatar = (event: Event) => {
    const target = event.target as HTMLInputElement
    if (target.files && target.files[0]) {
        const file = target.files[0]
        setFieldValue('avatar', URL.createObjectURL(file))
        avatarFile.value = file
    }
}

const onFileSelected = (event: Event) => {
    const target = event.target as HTMLInputElement

    if (target.files && target.files[0]) {
        const file = target.files[0]
        // Kiểm tra dung lượng (Ví dụ tối đa 15MB)
        if (file.size > 15 * 1024 * 1024) {
            toast({ message: 'Dung lượng ảnh vượt quá 15MB!', color: 'danger' })
            return
        }

        // Đọc file thành đường dẫn tạm thời để đưa vào Cropper
        setFieldValue('cover', URL.createObjectURL(file))
        coverFile.value = file
        showCropper.value = true // Mở modal cắt ảnh

        // Reset input để có thể chọn lại chính file đó lần sau nếu muốn
        target.value = ''
    }
}

// Nhận kết quả trả về từ Modal cắt ảnh
const onCropperResult = ({ blob, croppedImageUrl }: any) => {
    // resultImageUrl.value = croppedImageUrl
    setFieldValue('cover', croppedImageUrl)
    coverFile.value = blob
}

const save = handleSubmit(async (values: any) => {
    isLoading.value = true

    if (!avatarFile.value || !coverFile.value) return
    const avatarObjectName = `/media/oaAvatar_${Date.now()}`
    const coverObjectName = `/media/oaCover_${Date.now()}`

    try {
        await Promise.all([
            uploadFile({
                file: avatarFile.value,
                objectName: avatarObjectName
            }),
            uploadFile({
                file: coverFile.value,
                objectName: coverObjectName
            })
        ]);

        const data = await oaStor.create({
            ...values, avatar: avatarObjectName, cover: coverObjectName
        })

        if (data) {
            router.push(OA_ROUTE.accounts)
        }
    } catch (e: any) {
        toast({
            message: e.message,
            color: "danger"
        })
    } finally {
        isLoading.value = false
    }
},
    (errorContext) => {
        // Nếu bấm submit mà không thấy log gì, hãy xem ở đây!
        console.log('Form bị chặn do lỗi validation:', errorContext.errors)
    })

onMounted(async () => {
    const list = await categoryStor.getAll()

    categories.value = list.map((i: OaCategoryType) => ({ label: i.name, value: i.code }))
})

watch(() => values.category, () => {
    setFieldValue('categoryName', categories.value.find(i => i.value == values.category)?.label)
})
</script>