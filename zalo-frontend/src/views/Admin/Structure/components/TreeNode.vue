<template>
  <div class="relative">
    <div v-if="isDragOver && dragPosition === 'top'" class="h-0.5 w-full bg-blue-500 my-0.5 rounded transition-all animate-pulse"></div>

    <div 
      draggable="true"
      @dragstart.stop="onDragStart"
      @dragover.prevent="onDragOver"
      @dragleave="onDragLeave"
      @drop.stop="onDrop"
      @click.stop="$emit('select', node)"
      :class="[
        'flex items-center justify-between p-2 my-0.5 rounded border transition-all text-xs font-sans relative select-none',
        isDragOver && dragPosition === 'center' ? 'border-blue-500 bg-blue-50/90 scale-[1.01] shadow-sm' : 'bg-white border-gray-200 hover:bg-gray-50',
        selectedId === node.id ? 'bg-blue-50 border-blue-400 font-semibold shadow-sm' : ''
      ]"
    >
      <div class="flex items-center gap-1.5 flex-1 cursor-grab active:cursor-grabbing">
        <span class="text-gray-400 font-bold text-[11px] px-0.5 hover:text-blue-500">☰</span>
        <button 
          v-if="node.children && node.children.length > 0" 
          @click.stop="isOpen = !isOpen"
          class="w-4 h-4 flex items-center justify-center text-[10px] text-gray-500 hover:bg-gray-200 rounded"
        >
          {{ isOpen ? '▼' : '▶' }}
        </button>
        <span v-else class="w-4"></span>

        <span class="text-gray-600 flex items-center justify-center" v-html="node.icon || '📁'"></span>
        <span class="text-gray-800">{{ node.description }}</span>
        <span class="text-gray-400 font-mono text-[10px]">({{ node.code }})</span>
        <span v-if="node.stt === 0" class="px-1 py-px text-[9px] bg-amber-50 text-amber-600 rounded border border-amber-200 scale-90">Tắt</span>
      </div>

      <div class="flex items-center gap-1 opacity-0 group-hover:opacity-100 hover:opacity-100 transition-opacity pl-2 bg-gradient-to-l from-white via-white to-transparent">
        <button @click.stop="$emit('add-child', node)" class="w-5 h-5 flex items-center justify-center text-emerald-600 hover:bg-emerald-50 rounded border border-transparent hover:border-emerald-200" title="Thêm menu con">➕</button>
        <button @click.stop="$emit('delete', node)" class="w-5 h-5 flex items-center justify-center text-red-500 hover:bg-red-50 rounded border border-transparent hover:border-red-200" title="Xóa tạm">🗑️</button>
      </div>
    </div>

    <div v-if="isDragOver && dragPosition === 'bottom'" class="h-0.5 w-full bg-blue-500 my-0.5 rounded transition-all animate-pulse"></div>

    <div v-if="node.children && node.children.length > 0 && isOpen" class="pl-5 ml-2 border-l border-dashed border-gray-300">
      <TreeNode 
        v-for="child in node.children" 
        :key="child.id" 
        :node="child"
        :selected-id="selectedId"
        class="group"
        @select="$emit('select', $event)"
        @add-child="$emit('add-child', $event)"
        @delete="$emit('delete', $event)"
        @drag-node="(dragId, dropId, pos) => $emit('drag-node', dragId, dropId, pos)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

interface MenuNode {
  id: number; pid: number; code: string; icon: string; description: string; type: number | null; sort: number; stt: number; appType: string; children?: MenuNode[];
}

const props = defineProps<{ node: MenuNode; selectedId?: number }>();
const emit = defineEmits(['select', 'add-child', 'delete', 'drag-node']);

const isOpen = ref(true);
const isDragOver = ref(false);
const dragPosition = ref<'top' | 'center' | 'bottom'>('center');

const onDragStart = (event: DragEvent) => {
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move';
    event.dataTransfer.setData('text/plain', props.node.id.toString());
  }
};

// 🔥 Tính toán vị trí chuột động để xác định ý định người dùng
const onDragOver = (event: DragEvent) => {
  isDragOver.value = true;
  const targetElement = event.currentTarget as HTMLElement;
  const rect = targetElement.getBoundingClientRect();
  const relativeY = event.clientY - rect.top; // Vị trí pixel chuột tính từ mép trên của Node
  
  // Phân chia không gian: 25% đầu = Trên, 25% cuối = Dưới, 50% giữa = Làm con
  if (relativeY < rect.height * 0.25) {
    dragPosition.value = 'top';
  } else if (relativeY > rect.height * 0.75) {
    dragPosition.value = 'bottom';
  } else {
    dragPosition.value = 'center';
  }
};

const onDragLeave = () => {
  isDragOver.value = false;
};

const onDrop = (event: DragEvent) => {
  isDragOver.value = false;
  if (event.dataTransfer) {
    const draggedNodeId = parseInt(event.dataTransfer.getData('text/plain'), 10);
    const targetNodeId = props.node.id;
    
    if (draggedNodeId && targetNodeId && draggedNodeId !== targetNodeId) {
      emit('drag-node', draggedNodeId, targetNodeId, dragPosition.value);
    }
  }
};
</script>