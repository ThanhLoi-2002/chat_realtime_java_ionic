<template>
    <div class="flex flex-col h-screen bg-[#f4f7f6] p-4 font-sans text-xs text-gray-700 select-none">

        <div
            class="flex justify-between items-center bg-white border border-gray-200 px-4 py-2 rounded-t mb-3 shadow-sm">
            <h1 class="text-sm font-bold text-gray-700 uppercase tracking-wide flex items-center gap-2">
                <span>⚙️ Hệ Thống Menu Đa Phân Hệ</span>
                <span class="text-[10px] bg-blue-100 text-blue-700 font-normal px-2 py-0.5 rounded-full normal-case">Kéo
                    thả Đệ Quy Native</span>
            </h1>
            <div class="text-[11px] text-gray-400">
                🏠 / <span class="text-blue-500">Hệ thống</span> / <span class="text-blue-500">Quản lý Menu</span>
            </div>
        </div>

        <div class="grid grid-cols-12 gap-4 flex-1 overflow-hidden">

            <div class="col-span-6 bg-white border border-gray-200 rounded shadow-sm flex flex-col overflow-hidden">
                <div class="border-b border-gray-200 px-3 py-2 flex justify-between items-center bg-gray-50/70">
                    <span class="font-bold text-gray-600 uppercase text-[11px]">💡 Kéo menu thả đè vào nhau để phân nhóm
                        con</span>
                    <button @click="saveTreeOrder"
                        class="px-2.5 py-1 bg-blue-500 hover:bg-blue-600 text-white rounded text-[11px] font-medium transition shadow-sm">
                        🔄 Sắp xếp / Lưu Vị Trí
                    </button>
                </div>

                <div class="p-4 flex-1 overflow-y-auto bg-white space-y-6">
                    <div v-for="(rootNodes, appKey) in treeDataGroup" :key="appKey"
                        class="border border-gray-100 rounded p-3 bg-gray-50/30">

                        <div
                            class="flex items-center gap-1.5 p-1 mb-3 bg-blue-50 text-blue-700 border border-blue-200 rounded w-max px-2.5 font-bold uppercase tracking-wider text-[11px]">
                            💻 Phân hệ: {{ appKey }}
                        </div>

                        <div @dragover.prevent @drop="onDropToRoot($event, appKey)"
                            class="flex items-center justify-center gap-1.5 p-2 mb-2 bg-emerald-50 text-emerald-700 border border-emerald-200 border-dashed rounded w-full font-semibold transition-colors text-[11px] cursor-default">
                            📥 Thả vào đây để đưa menu về cấp Gốc của [{{ appKey }}]
                        </div>

                        <div class="pl-2">
                            <TreeNode v-for="rootNode in rootNodes" :key="rootNode.id" :node="rootNode"
                                :selected-id="selectedNode?.id" @select="selectNode" @add-child="addChildNode"
                                @delete="handleDelete"
                                @drag-node="(dragId, dropId, pos) => handleDragAndDrop(appKey, dragId, dropId, pos)" />
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-span-6 flex flex-col gap-4 overflow-hidden">

                <div class="bg-white border border-gray-200 rounded shadow-sm p-4 flex flex-col">
                    <div
                        class="font-bold text-gray-700 border-b pb-2 uppercase text-[11px] mb-4 flex justify-between items-center">
                        <span>MỚI / CẬP NHẬT THÔNG TIN</span>
                        <button v-if="isEditMode" @click="resetForm"
                            class="text-blue-500 hover:underline normal-case font-normal text-[11px]">
                            Tạo mới cấp gốc
                        </button>
                    </div>

                    <form @submit.prevent="submitForm" class="space-y-3.5">
                        <div>
                            <label class="block text-[11px] font-bold text-gray-600 mb-1">Cha (pid)</label>
                            <select v-model="form.pid"
                                class="w-full p-2 border border-gray-300 rounded bg-gray-50 outline-none">
                                <option :value="0">Gốc (/)</option>
                                <option v-for="opt in allNodesFlat" :key="opt.id" :value="opt.id">
                                    [{{ opt.appType }}] {{ opt.description }} (ID: {{ opt.id }})
                                </option>
                            </select>
                        </div>

                        <div class="grid grid-cols-2 gap-3">
                            <div>
                                <label class="block text-[11px] font-bold text-gray-600 mb-1">Mã (Code)</label>
                                <input v-model="form.code" type="text" required
                                    class="w-full p-2 border border-gray-300 rounded outline-none focus:border-blue-400" />
                            </div>
                            <div>
                                <label class="block text-[11px] font-bold text-gray-600 mb-1">Phân hệ (appType)</label>
                                <input v-model="form.appType" type="text" required
                                    class="w-full p-2 border border-gray-300 rounded outline-none focus:border-blue-400" />
                            </div>
                        </div>

                        <div>
                            <label class="block text-[11px] font-bold text-gray-600 mb-1">Biểu Tượng (Icon Class /
                                HTML)</label>
                            <input v-model="form.icon" type="text"
                                class="w-full p-2 border border-gray-300 rounded outline-none focus:border-blue-400" />
                        </div>

                        <div>
                            <label class="block text-[11px] font-bold text-gray-600 mb-1">Mô Tả / Tên Hiển Thị</label>
                            <textarea v-model="form.description" required rows="2"
                                class="w-full p-2 border border-gray-300 rounded outline-none focus:border-blue-400 resize-none"></textarea>
                        </div>

                        <div>
                            <label class="block text-[11px] font-bold text-gray-600 mb-1">Trạng Thái</label>
                            <div class="flex gap-4 mt-1 font-medium">
                                <label class="flex items-center gap-1 cursor-pointer"><input type="radio"
                                        v-model="form.stt" :value="1"> Bật</label>
                                <label class="flex items-center gap-1 cursor-pointer"><input type="radio"
                                        v-model="form.stt" :value="0"> Tắt</label>
                                <label class="flex items-center gap-1 cursor-pointer"><input type="radio"
                                        v-model="form.stt" :value="-1"> Xóa (Thùng rác)</label>
                            </div>
                        </div>

                        <div class="pt-1">
                            <button type="submit"
                                class="px-4 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white font-medium rounded shadow-sm flex items-center gap-1 transition">
                                ☁️ Lưu thay đổi
                            </button>
                        </div>
                    </form>
                </div>

                <div
                    class="bg-white border border-gray-200 rounded shadow-sm p-4 flex-1 flex flex-col overflow-hidden min-h-[160px]">
                    <h2 class="font-bold text-red-600 border-b border-gray-200 pb-2 uppercase text-[11px] mb-2">
                        🗑 Thùng Rác Hệ Thống
                    </h2>
                    <div class="space-y-1.5 overflow-y-auto flex-1 pr-1">
                        <div v-if="trashData.length === 0" class="text-gray-400 text-center py-4 italic">
                            Thùng rác trống
                        </div>
                        <div v-for="item in trashData" :key="item.id"
                            class="flex items-center justify-between p-2 bg-red-50/50 border border-red-100 rounded text-[11px]">
                            <div class="flex items-center gap-2">
                                <span class="px-1 bg-gray-200 text-gray-600 rounded text-[9px] font-bold">{{
                                    item.appType }}</span>
                                <span v-html="item.icon"></span>
                                <span class="text-gray-700 font-medium">{{ item.description }}</span>
                                <span class="text-gray-400 font-mono">({{ item.code }})</span>
                            </div>
                            <button @click="restoreFromTrash(item)" class="text-blue-600 hover:underline font-bold">
                                [Khôi phục]
                            </button>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import TreeNode from './components/TreeNode.vue';

interface MenuNode {
    id: number;
    pid: number;
    code: string;
    icon: string;
    description: string;
    type: number | null;
    sort: number;
    stt: number;
    appType: string;
    children?: MenuNode[];
}

const treeDataGroup = ref<Record<string, MenuNode[]>>({});
const trashData = ref<MenuNode[]>([]);
const allNodesFlat = ref<MenuNode[]>([]);
const selectedNode = ref<MenuNode | null>(null);
const isEditMode = ref(false);

const form = ref<MenuNode>({
    id: 0, pid: 0, code: '', icon: '', description: '', type: 99, sort: 0, stt: 1, appType: 'OA'
});

const loadMockData = () => {
    const mockResponse = {
        "result": {
            "OA": [
                {
                    "id": 1, "pid": 0, "appType": "OA", "code": "oa_chatbot", "icon": "🤖",
                    "description": "Quản lý Chatbot", "sort": 0, "stt": 1, "type": 1,
                    "children": [
                        { "id": 2, "pid": 1, "appType": "OA", "code": "bot_content", "icon": "📝", "description": "Nội dung phản hồi", "sort": 0, "stt": 1, "type": 99, "children": [] },
                        { "id": 3, "pid": 1, "appType": "OA", "code": "bot_auto", "icon": "⚡", "description": "Tịch hợp tự động", "sort": 1, "stt": 1, "type": 99, "children": [] }
                    ]
                },
                {
                    "id": 4, "pid": 0, "appType": "OA", "code": "oa_setting", "icon": "⚙️",
                    "description": "Menu Setting", "sort": 1, "stt": 1, "type": 1,
                    "children": [
                        { "id": 5, "pid": 4, "appType": "OA", "code": "set_account", "icon": "👤", "description": "Tài khoản hệ thống", "sort": 0, "stt": 1, "type": 99, "children": [] },
                        { "id": 6, "pid": 4, "appType": "OA", "code": "set_general", "icon": "🌐", "description": "Cấu hình chung", "sort": 1, "stt": 1, "type": 99, "children": [] }
                    ]
                }
            ],
            "ADMIN": [
                {
                    "id": 7, "pid": 0, "appType": "ADMIN", "code": "ad_users", "icon": "👥",
                    "description": "Quản lý người dùng (User)", "sort": 0, "stt": 1, "type": 1,
                    "children": [
                        { "id": 8, "pid": 7, "appType": "ADMIN", "code": "user_new", "icon": "🆕", "description": "Danh sách User mới đăng ký", "sort": 0, "stt": 1, "type": 99, "children": [] },
                        { "id": 9, "pid": 7, "appType": "ADMIN", "code": "user_old", "icon": "⏳", "description": "Lịch sử User cũ", "sort": 1, "stt": 1, "type": 99, "children": [] }
                    ]
                },
                {
                    "id": 10, "pid": 0, "appType": "ADMIN", "code": "ad_messages", "icon": "💬",
                    "description": "Quản lý tin nhắn hệ thống", "sort": 1, "stt": 1, "type": 1,
                    "children": [
                        { "id": 11, "pid": 10, "appType": "ADMIN", "code": "msg_list", "icon": "📋", "description": "Danh sách tin nhắn", "sort": 0, "stt": 1, "type": 99, "children": [] },
                        { "id": 12, "pid": 10, "appType": "ADMIN", "code": "msg_images", "icon": "🖼️", "description": "Kho lưu trữ hình ảnh", "sort": 1, "stt": 1, "type": 99, "children": [] }
                    ]
                }
            ]
        }
    };

    const groups = mockResponse.result;
    const newTreeGroup: Record<string, MenuNode[]> = {};
    const deletedItems: MenuNode[] = [];
    const flatList: MenuNode[] = [];

    const processNodes = (nodes: MenuNode[], targetArray: MenuNode[]) => {
        nodes.sort((a, b) => a.sort - b.sort);
        nodes.forEach(node => {
            flatList.push(node);
            if (node.stt === -1) {
                deletedItems.push(node);
            } else {
                const processedNode = { ...node, children: [] as MenuNode[] };
                targetArray.push(processedNode);
                if (node.children && node.children.length > 0) {
                    processNodes(node.children, processedNode.children);
                }
            }
        });
    };

    Object.keys(groups).forEach(appKey => {
        const activeTree: MenuNode[] = [];
        processNodes(groups[appKey as keyof typeof groups], activeTree);
        newTreeGroup[appKey] = activeTree;
    });

    treeDataGroup.value = newTreeGroup;
    trashData.value = deletedItems;
    allNodesFlat.value = flatList.filter(n => n.stt !== -1);
};

onMounted(() => {
    loadMockData();
});

const selectNode = (node: MenuNode) => {
    selectedNode.value = node;
    isEditMode.value = true;
    form.value = { ...node, children: [] };
};

const addChildNode = (parent: MenuNode) => {
    isEditMode.value = false;
    selectedNode.value = null;
    form.value = {
        id: Math.floor(Math.random() * 1000) + 100,
        pid: parent.id,
        code: '',
        icon: '',
        description: '',
        type: 99,
        sort: 0,
        stt: 1,
        appType: parent.appType
    };
};

const resetForm = () => {
    isEditMode.value = false;
    selectedNode.value = null;
    form.value = { id: Math.floor(Math.random() * 1000) + 100, pid: 0, code: '', icon: '', description: '', type: 99, sort: 0, stt: 1, appType: 'OA' };
};

const submitForm = () => {
    if (isEditMode.value && selectedNode.value) {
        selectedNode.value.code = form.value.code;
        selectedNode.value.icon = form.value.icon;
        selectedNode.value.description = form.value.description;
        selectedNode.value.stt = form.value.stt;
        selectedNode.value.appType = form.value.appType;

        if (form.value.stt === -1) {
            handleDelete(selectedNode.value);
        } else {
            alert(`[TEST UI] Đã kích hoạt lưu phần tử: ${form.value.description}`);
        }
    } else {
        const newNode: MenuNode = { ...form.value, children: [] };
        if (newNode.pid === 0) {
            if (!treeDataGroup.value[newNode.appType]) treeDataGroup.value[newNode.appType] = [];
            treeDataGroup.value[newNode.appType].push(newNode);
        } else {
            const appendToParent = (nodes: MenuNode[]): boolean => {
                for (let i = 0; i < nodes.length; i++) {
                    if (nodes[i].id === newNode.pid) {
                        if (!nodes[i].children) nodes[i].children = [];
                        nodes[i].children.push(newNode);
                        return true;
                    }
                    if (nodes[i].children && nodes[i].children.length > 0) {
                        if (appendToParent(nodes[i].children)) return true;
                    }
                }
                return false;
            };
            Object.keys(treeDataGroup.value).forEach(k => appendToParent(treeDataGroup.value[k]));
        }
    }
    syncFlatList();
    resetForm();
};

const handleDelete = (node: MenuNode) => {
    if (!confirm(`Xóa tạm menu "${node.description}"?`)) return;
    node.stt = -1;
    trashData.value.push(node);

    const removeNode = (nodes: MenuNode[]): boolean => {
        for (let i = 0; i < nodes.length; i++) {
            if (nodes[i].id === node.id) { nodes.splice(i, 1); return true; }
            if (nodes[i].children && nodes[i].children.length > 0) { if (removeNode(nodes[i].children)) return true; }
        }
        return false;
    };

    Object.keys(treeDataGroup.value).forEach(k => removeNode(treeDataGroup.value[k]));
    syncFlatList();
};

const restoreFromTrash = (node: MenuNode) => {
    node.stt = 1;
    node.pid = 0;
    trashData.value = trashData.value.filter(n => n.id !== node.id);
    if (!treeDataGroup.value[node.appType]) {
        treeDataGroup.value[node.appType] = [];
    }
    treeDataGroup.value[node.appType].push(node);
    syncFlatList();
};

// --- NATIVE DRAG AND DROP RECURSIVE LOGIC ---
const handleDragAndDrop = (appKey: string, draggedId: number, targetId: number, position: 'top' | 'center' | 'bottom') => {
    const list = treeDataGroup.value[appKey];
    if (!list || draggedId === targetId) return;

    let draggedNode: MenuNode | null = null;
    let targetNodeParentId: number = 0; // Lưu pid của Node đích để phục vụ việc nhảy cấp

    // STEP 1: Tìm pid thực tế của Node đích (Target) trước khi cắt mảng
    const findTargetParentId = (nodes: MenuNode[], currentParentId: number): boolean => {
        for (let i = 0; i < nodes.length; i++) {
            if (nodes[i].id === targetId) {
                targetNodeParentId = currentParentId;
                return true;
            }
            if (nodes[i].children && nodes[i].children.length > 0) {
                if (findTargetParentId(nodes[i].children, nodes[i].id)) return true;
            }
        }
        return false;
    };
    findTargetParentId(list, 0);

    // STEP 2: Tìm và BÓC TÁCH (Cắt) Node đang kéo ra khỏi cây dữ liệu
    const removeNodeFromTree = (nodes: MenuNode[]): boolean => {
        for (let i = 0; i < nodes.length; i++) {
            if (nodes[i].id === draggedId) {
                draggedNode = nodes.splice(i, 1)[0];
                return true;
            }
            if (nodes[i].children && nodes[i].children.length > 0) {
                if (removeNodeFromTree(nodes[i].children)) return true;
            }
        }
        return false;
    };
    removeNodeFromTree(list);

    // Hàm chặn lỗi kéo cha thả vào con ruột gây lặp vô hạn
    const isTargetChildOfDragged = (dragged: MenuNode, idToCheck: number): boolean => {
        if (!dragged.children || dragged.children.length === 0) return false;
        return dragged.children.some(c => c.id === idToCheck || isTargetChildOfDragged(c, idToCheck));
    };

    // STEP 3: Tiến hành CHÈN dựa theo vị trí position (Xử lý mượt kéo lên/xuống/xuyên cấp)
    const processInsertion = (nodes: MenuNode[], parentId: number): boolean => {
        for (let i = 0; i < nodes.length; i++) {
            if (nodes[i].id === targetId) {
                if (!draggedNode) return false;

                // Chống đệ quy vô hạn
                if (isTargetChildOfDragged(draggedNode, targetId)) {
                    alert("❌ Không thể kéo menu cha vào bên trong menu con của chính nó!");
                    return false;
                }

                if (position === 'center') {
                    // TH1: Thả vào CHÍNH GIỮA -> Biến thành con của Node đích (Xuống cấp hoặc chuyển nhóm)
                    if (!nodes[i].children) nodes[i].children = [];
                    draggedNode.pid = nodes[i].id;
                    nodes[i].children.push(draggedNode);
                } else {
                    // TH2: Thả lên TRÊN hoặc XUỐNG DƯỚI -> Xếp hàng cùng cấp với Node đích (Hỗ trợ kéo cấp 4 ra cấp 2)
                    draggedNode.pid = targetNodeParentId; // Ép Node đang kéo ăn theo pid của Node đích
                    const insertIndex = position === 'top' ? i : i + 1;
                    nodes.splice(insertIndex, 0, draggedNode);
                }
                return true;
            }

            if (nodes[i].children && nodes[i].children.length > 0) {
                if (processInsertion(nodes[i].children, nodes[i].id)) return true;
            }
        }
        return false;
    };

    // STEP 4: Chèn và cứu hộ dữ liệu phòng hờ lỗi
    if (draggedNode) {
        const success = processInsertion(list, 0);

        // Nếu chèn không thành công, trả node về lại cấp gốc để tránh bốc hơi dữ liệu
        if (!success) {
            draggedNode.pid = 0;
            list.push(draggedNode);
        }

        // Đánh lại số thứ tự sort chuẩn và cập nhật danh sách phẳng select form
        recalculateSortOrder(list, 0);
        syncFlatList();
    }
};

const recalculateSortOrder = (nodes: MenuNode[], parentId: number) => {
    nodes.forEach((node, index) => {
        node.sort = index; // Đánh lại thứ tự sort chuẩn từ 0, 1, 2... nội bộ trong nhóm
        node.pid = parentId; // Gán lại pid chuẩn theo ID của nhóm cha chứa nó

        // Nếu có nhóm con bên dưới, tiếp tục đệ quy sâu xuống để đồng bộ
        if (node.children && node.children.length > 0) {
            recalculateSortOrder(node.children, node.id);
        }
    });
};

const onDropToRoot = (event: DragEvent, appKey: string) => {
    if (event.dataTransfer) {
        const draggedId = parseInt(event.dataTransfer.getData('text/plain'), 10);
        const list = treeDataGroup.value[appKey];

        if (draggedId && list) {
            let draggedNode: MenuNode | null = null;

            const removeNode = (nodes: MenuNode[]): boolean => {
                for (let i = 0; i < nodes.length; i++) {
                    if (nodes[i].id === draggedId) { draggedNode = nodes.splice(i, 1)[0]; return true; }
                    if (nodes[i].children && nodes[i].children.length > 0) { if (removeNode(nodes[i].children)) return true; }
                }
                return false;
            };

            removeNode(list);
            if (draggedNode) {
                draggedNode.pid = 0;
                list.push(draggedNode);
                syncFlatList();
            }
        }
    }
};

const syncFlatList = () => {
    const flat: MenuNode[] = [];
    const rec = (nodes: MenuNode[]) => {
        nodes.forEach(n => {
            flat.push(n);
            if (n.children && n.children.length > 0) rec(n.children);
        });
    };
    Object.keys(treeDataGroup.value).forEach(k => rec(treeDataGroup.value[k]));
    allNodesFlat.value = flat.filter(n => n.stt !== -1);
};

const saveTreeOrder = () => {
    const updates: Array<{ id: number; pid: number; sort: number }> = [];

    const flatTree = (nodes: MenuNode[], parentId: number) => {
        nodes.forEach((node, index) => {
            updates.push({ id: node.id, pid: parentId, sort: index });
            if (node.children && node.children.length > 0) {
                flatTree(node.children, node.id);
            }
        });
    };

    Object.keys(treeDataGroup.value).forEach(key => {
        flatTree(treeDataGroup.value[key], 0);
    });

    console.log("MẢNG PAYLOAD GỬI LÊN SERVER SẮP XẾP:", JSON.stringify(updates, null, 2));
    alert('Đã tính toán xong mảng vị trí mới! Kiểm tra cửa sổ Console log F12 để xem cấu trúc JSON gửi đi.');
};
</script>