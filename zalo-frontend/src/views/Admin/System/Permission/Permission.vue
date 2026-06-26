<template>
    <div class="p-3">
        <div class="flex justify-between gap-4">
            <!-- Apps -->
            <div class="w-1/2">
                <div :class="[oaStyle.text.primary, 'font-semibold mb-3']">
                    {{ t('application') }}
                </div>

                <select v-model="selectedApp"
                    :class="[oaStyle.bg.primary, oaStyle.border.secondary, oaStyle.text.primary, 'w-full border rounded px-3 py-2 focus:outline-none']">
                    <option v-for="app in apps" :key="app" :value="app">
                        {{ app }}
                    </option>
                </select>
            </div>

            <!-- Modules -->
            <div class="w-1/2">
                <label :class="[oaStyle.text.primary, 'block mb-3 font-medium']">
                    {{ t('module') }}
                </label>

                <select v-model="selectedModule"
                    :class="[oaStyle.bg.primary, oaStyle.border.secondary, oaStyle.text.primary, 'w-full border rounded px-3 py-2 focus:outline-none']">
                    <option v-for="item in modules" :key="item.id" :value="item.id">
                        {{ item.code }}
                    </option>
                </select>
            </div>
        </div>

        <!-- Controllers -->
        <div class="flex flex-wrap gap-3 mt-3">
            <button v-for="item in controllers" :key="item.id" @click="goTo(item.code)"
                :class="[oaStyle.bg.primary, oaStyle.border.primary, oaStyle.bg.hover, oaStyle.text.primary, 'px-4 py-2 rounded-lg border transition cursor-pointer']">
                {{ item.code }}
            </button>
        </div>
    </div>
</template>
<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import { useTranslate } from '@/composables/useTranslate';
import { useAdminRoleStore } from '@/stores/Admin/role.storage';
import { useAdminStructureStore } from '@/stores/Admin/structure.storage';
import { StructureType } from '@/types/entities';
import { AppTypeEnum } from '@/types/enum';
import { onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter()
const roleStor = useAdminRoleStore()
const structureStor = useAdminStructureStore()
const { t } = useTranslate()

const selectedApp = ref<AppTypeEnum>(AppTypeEnum.APP)
const selectedModule = ref<number>()

const apps = Object.values(AppTypeEnum)
const controllers = ref<StructureType[]>([])
const modules = ref<StructureType[]>([])

const goTo = (code: string) => {
    router.push(`permission/set-access?controllerCode=${code}&moduleId=${selectedModule.value}&appType=${selectedApp.value}`)
}

watch(
    () => selectedModule.value,
    async () => {
        if (!selectedModule.value) {
            controllers.value = []
            return
        }

        controllers.value = await structureStor.getControllersByModule(selectedModule.value)
    },
    { immediate: true }
)

watch(
    () => selectedApp.value,
    async () => {
        if (!selectedApp.value) {
            modules.value = []
            controllers.value = []
            return
        }

        modules.value = await structureStor.getModuleByAppType(selectedApp.value)
        if (modules.value.length > 0) {
            selectedModule.value = modules.value[0].id
        } else {
            selectedModule.value = undefined
        }
    },
    { immediate: true }
)

onMounted(async () => {
    roleStor.roles.length == 0 && await roleStor.getList()
})
</script>