<template>
    <div :class="[oaStyle.bg.primary, oaStyle.text.primary, 'flex flex-col p-4']">

        <div
            :class="[oaStyle.bg.primary, oaStyle.border.primary, 'flex justify-between items-center border px-4 py-2 rounded-t mb-3 shadow-sm']">
            <div
                :class="[oaStyle.text.secondary, 'text-lg font-medium uppercase tracking-wide flex items-center gap-2']">
                <span>Hệ Thống Menu Đa Phân Hệ</span>
            </div>
            <div class="text-xs text-gray-400">
                🏠 / <span class="text-blue-500">{{ t('system') }}</span> / <span class="text-blue-500">Quản lý
                    Menu</span>
            </div>
        </div>

        <div class="grid grid-cols-12 gap-4 flex-1 overflow-hidden">

            <div
                :class="[oaStyle.bg.primary, oaStyle.border.primary, 'col-span-6 border rounded shadow-sm flex flex-col overflow-hidden']">
                <div :class="[oaStyle.border.primary, 'border-b px-3 py-2 flex justify-between items-center']">
                    <span :class="[oaStyle.text.secondary, 'font-bold uppercase text-xs']">💡 Kéo menu thả đè vào
                        nhau để phân nhóm
                        con</span>
                    <button @click="toggleSortMode" :class="[
                        'px-2.5 py-1 rounded text-sm font-medium transition shadow-sm cursor-pointer text-white',
                        isSortMode
                            ? 'bg-green-600 hover:bg-green-700'
                            : 'bg-blue-600 hover:bg-blue-700'
                    ]">
                        <div class="flex items-center gap-1" v-if="isSortMode">
                            <i class="fas fa-save"></i>
                            <span>{{ t('save') }}</span>
                        </div>
                        <div class="flex items-center gap-1" v-else>
                            <i class="fas fa-sync-alt"></i>
                            <div>{{ t('sort') }}</div>
                        </div>
                    </button>
                </div>

                <div :class="[oaStyle.bg.primary, 'p-4 flex-1 overflow-y-auto space-y-6']">
                    <div v-for="(rootNode, appKey) in structureStor.tree" :key="appKey"
                        :class="[oaStyle.border.primary, 'border rounded p-3']">

                        <div :class="[oaStyle.bg.primary, oaStyle.border.primary, oaStyle.text.active]"
                            class="flex items-center gap-1.5 p-1 mb-3 border rounded w-max px-2.5 font-bold uppercase tracking-wider text-xs">
                            App: {{ appKey }}
                        </div>

                        <div class="pl-2">
                            <TreeNode :key="rootNode.id" :node="rootNode"
                                :selected-id="selectedNode?.id" @select="selectNode" @add-child="addChildNode"
                                :sortable="isSortMode"
                                @drag-node="(dragId, dropId) => swapNode(appKey, dragId, dropId)" />
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-span-6 flex flex-col gap-4 overflow-hidden">

                <div
                    :class="[oaStyle.bg.primary, oaStyle.border.primary, 'border rounded shadow-sm p-4 flex flex-col']">
                    <div
                        :class="[oaStyle.text.secondary, oaStyle.border.primary, 'font-bold border-b pb-2 uppercase text-xs mb-4 flex justify-between items-center']">
                        <span>{{ isEditMode ? t('update') : t('create') }}</span>
                        <button v-if="isEditMode" @click="resetForm"
                            class="text-blue-500 cursor-pointer normal-case font-normal text-xs">
                            {{ t('createRoot')
                            }}
                        </button>
                    </div>

                    <form @submit.prevent="submitForm" class="space-y-3.5">
                        <div>
                            <label :class="[oaStyle.text.secondary, 'block text-xs font-bold mb-1']">{{ t('PID')
                                }}</label>
                            <select v-model="form.pid" @change="onPidChange"
                                :class="[oaStyle.border.primary, oaStyle.bg.primary, oaStyle.text.secondary, 'w-full p-2 border rounded outline-none']">
                                <option v-for="opt in allNodesFlat" :key="opt.id" :value="opt.id">
                                    <p v-for="i in opt.type" :key="i">|--</p>{{ opt.code }} <p v-if="opt.type == 1">{{
                                        t('root') }} (./)</p>
                                </option>
                            </select>
                        </div>

                        <div class="grid grid-cols-2 gap-3">
                            <div>
                                <label :class="[oaStyle.text.secondary, 'block text-xs font-bold mb-1']">{{
                                    t('code') }}</label>
                                <input v-model="form.code" type="text" required
                                    :class="[oaStyle.border.primary, 'w-full p-2 border rounded outline-none focus:border-blue-600/50']" />
                            </div>
                            <div>
                                <label :class="[oaStyle.text.secondary, 'block text-xs font-bold mb-1']">{{
                                    t('appType') }}</label>
                                <input v-model="form.appType" type="text" disabled
                                    :class="[oaStyle.border.primary, 'w-full p-2 border rounded outline-none focus:border-blue-600/50']" />
                            </div>
                        </div>

                        <div>
                            <label :class="[oaStyle.text.secondary, 'block text-xs font-bold mb-1']">{{
                                t('path') }}</label>
                            <input v-model="form.path" type="text" required
                                :class="[oaStyle.border.primary, 'w-full p-2 border rounded outline-none focus:border-blue-600/50']" />
                        </div>
                        <div>
                            <label :class="[oaStyle.text.secondary, 'block text-xs font-bold mb-1']">{{
                                t('component') }}</label>
                            <input v-model="form.component" type="text"
                                :class="[oaStyle.border.primary, 'w-full p-2 border rounded outline-none focus:border-blue-600/50']" />
                        </div>

                        <div>
                            <label :class="[oaStyle.text.secondary, 'block text-xs font-bold mb-1']">{{ t('icon')
                                }}</label>
                            <input v-model="form.icon" type="text"
                                :class="[oaStyle.border.primary, 'w-full p-2 border rounded outline-none focus:border-blue-600/50']" />
                        </div>

                        <div>
                            <label :class="[oaStyle.text.secondary, 'block text-xs font-bold mb-1']">{{
                                t('description') }}</label>
                            <textarea v-model="form.description" rows="2"
                                :class="[oaStyle.border.primary, 'w-full p-2 border rounded outline-none focus:border-blue-600/50 resize-none']"></textarea>
                        </div>

                        <div>
                            <label :class="[oaStyle.text.secondary, 'block text-xs font-bold mb-1']">{{
                                t('permission') }}</label>
                            <input v-model="form.permissions" type="text"
                                :class="[oaStyle.border.primary, 'w-full p-2 border rounded outline-none focus:border-blue-600/50']" />
                        </div>

                        <div class="flex justify-between">
                            <div>
                                <label :class="[oaStyle.text.secondary, 'block text-xs font-bold mb-1']">{{ t('status')
                                }}</label>
                                <div :class="[oaStyle.text.secondary, 'flex gap-4 mt-1 font-medium']">
                                    <label class="flex items-center gap-1 cursor-pointer"><input type="radio"
                                            v-model="form.stt" :value="1"> {{ t('active') }}</label>
                                    <label class="flex items-center gap-1 cursor-pointer"><input type="radio"
                                            v-model="form.stt" :value="-1"> {{ t('delete') }} </label>
                                </div>
                            </div>
                            <div>
                                <label :class="[oaStyle.text.secondary, 'block text-xs font-bold mb-1']">{{ t('type')
                                }}</label>
                                <div :class="[oaStyle.text.secondary, 'flex gap-4 mt-1 font-medium']">
                                    <label v-for="type in menuType" :key="type"
                                        class="flex items-center gap-1 cursor-pointer"><input type="radio" required
                                            v-model="form.menuType" :value="type"> {{ t(type) }}</label>
                                </div>
                            </div>
                        </div>

                        <div class="pt-1">
                            <button type="submit"
                                class="px-4 py-1.5 bg-blue-600 hover:bg-blue-700 cursor-pointer text-white font-medium rounded shadow-sm flex items-center gap-1 transition">
                                ☁️ {{ t('save') }}
                            </button>
                        </div>
                    </form>
                </div>

                <div
                    :class="[oaStyle.bg.primary, oaStyle.border.primary, 'border borounded shadow-sm p-4 flex-1 flex flex-col overflow-hidden min-h-40']">
                    <div
                        :class="[oaStyle.border.primary, 'font-medium text-lg text-red-600 border-b pb-2 uppercase mb-2']">
                        <i class="fas fa-trash-alt"></i> {{ t('trash') }}
                    </div>
                    <div class="space-y-1.5 overflow-y-auto flex-1 pr-1">
                        <div v-if="structureStor.trashes.length === 0" class="text-gray-400 text-center py-4 italic">
                            {{ t('noData') }}
                        </div>
                        <div v-for="item in structureStor.trashes" :key="item.id" @click="selectNode(item)"
                            class="flex items-center justify-between p-2 bg-red-50/50 border border-red-100 rounded text-xs cursor-pointer">
                            <div class="flex items-center gap-2">
                                <span class="px-1 bg-gray-200 text-gray-600 rounded text-[9px] font-bold">{{
                                    item.appType }}</span>
                                <span v-html="item.icon"></span>
                                <span :class="[oaStyle.text.primary, 'font-mono']">{{ item.code }}</span>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import TreeNode from './components/TreeNode.vue';
import { oaStyle } from '@/assets/tailwindcss.ts';
import { useTranslate } from '@/composables/useTranslate.ts';
import { StructureType } from '@/types/entities.ts';
import { useAdminStructureStore } from '@/stores/Admin/structure.storage.ts';
import { AppTypeEnum, MenuTypeEnum } from '@/types/enum.ts';
import { useStructure } from '@/composables/useStructure.ts';

const allNodesFlat = ref<StructureType[]>([]);
const selectedNode = ref<StructureType | null>(null);
const isEditMode = ref(false);
const isSortMode = ref(false);
const { t } = useTranslate()
const structureStor = useAdminStructureStore()
const { findParent } = useStructure()

const structureDefault: Omit<StructureType, 'id'> & { id?: number; } = {
    id: undefined, pid: 1, code: '', icon: '', description: '', type: 0, sort: 0, stt: 1, appType: AppTypeEnum.OA, component: '', path: "", permissions: '', menuType: MenuTypeEnum.MENU, children: []
}
const form = ref<Omit<StructureType, 'id'> & { id?: number; }>(structureDefault);
const menuType = Object.values(MenuTypeEnum)

onMounted(async () => {
    await structureStor.getTree()
    await structureStor.getTrash()
});

watch(() => structureStor.tree, () => {
    syncFlatList()
}, { deep: true })

const onPidChange = (event: Event) => {
    const pid = Number(
        (event.target as HTMLSelectElement).value
    );

    // tìm node được chọn
    const parent = allNodesFlat.value.find(
        item => item.id === pid
    );

    resetForm()

    if (parent) {
        form.value.appType = parent.appType
        form.value.type = parent.type + 1
        form.value.path = parent.path + '/?'
        form.value.component = parent.component + '/?'
    }
    form.value.id = undefined
};

const toggleSortMode = () => {

    if (!isSortMode.value) {
        isSortMode.value = true;
        return;
    }

    saveTreeOrder();

    isSortMode.value = false;
};

const selectNode = (node: StructureType) => {
    selectedNode.value = node;
    isEditMode.value = true;

    const parent = findParent(
        Object.values(structureStor.tree).flat(), node.id
    )
    form.value = {
        ...node,
        path: node.path ? node.path : (parent?.path + '/?'),
        component: node.component ? node.component : (parent?.component + '/?'),
        children: []
    };
    // if(node.pid == 0){
    //     form.value.pid = node.id
    // }
};

const addChildNode = (parent: StructureType) => {
    isEditMode.value = false;
    selectedNode.value = null;
    form.value = {
        ...structureDefault,
        pid: parent.id,
        type: parent.type + 1,
        appType: parent.appType,
        path: parent.path + '/?',
        component: parent.component + '/?'
    };
};

const resetForm = () => {
    isEditMode.value = false;
    selectedNode.value = null;
    form.value = structureDefault;
};

const submitForm = async () => {
    await structureStor.createOrUpdate(form.value)
    syncFlatList();
    resetForm();
};

// --- NATIVE DRAG AND DROP RECURSIVE LOGIC ---
const swapNode = (
    appKey: string,
    draggedId: number,
    targetId: number
) => {

    const root = structureStor.tree[appKey];

    if (!root) return;

    const swapInLevel = (
        nodes: StructureType[]
    ): boolean => {

        const dragIndex =
            nodes.findIndex(
                n => n.id === draggedId
            );

        const targetIndex =
            nodes.findIndex(
                n => n.id === targetId
            );

        if (
            dragIndex !== -1 &&
            targetIndex !== -1
        ) {

            [
                nodes[dragIndex],
                nodes[targetIndex]
            ] = [
                    nodes[targetIndex],
                    nodes[dragIndex]
                ];

            return true;
        }

        for (const node of nodes) {
            if (
                node.children?.length &&
                swapInLevel(node.children)
            ) {
                return true;
            }
        }

        return false;
    };

    swapInLevel(root.children);
    syncFlatList();
};

const syncFlatList = () => {
    const flat: StructureType[] = [];
    const rec = (nodes: StructureType) => {
        nodes.children.forEach(n => {
            flat.push(n);
            if (n.children && n.children.length > 0) rec(n);
        });
    };
    Object.keys(structureStor.tree).forEach((k) => rec(structureStor.tree[k]));
    allNodesFlat.value = flat;
};

const saveTreeOrder = () => {
    const updates: Array<{ id: number; pid: number; sort: number }> = [];

    const flatTree = (nodes: StructureType, parentId: number) => {
        nodes.children.forEach((node, index) => {
            updates.push({ id: node.id, pid: parentId, sort: index });
            if (node.children && node.children.length > 0) {
                flatTree(node, node.id);
            }
        });
    };

    Object.keys(structureStor.tree).forEach(key => {
        flatTree(structureStor.tree[key], 0);
    });

    console.log("MẢNG PAYLOAD GỬI LÊN SERVER SẮP XẾP:", JSON.stringify(updates, null, 2));
    structureStor.sort(updates)
};
</script>