<template>
  <div class="relative">
    <div :draggable="sortable" @click.stop="$emit('select', node)" @dragstart="onDragStart"
      @dragenter.prevent="onDragEnter" @dragover.prevent @dragleave="onDragLeave" @drop.prevent.stop="onDrop"
      @dragend="onDragEnd" :class="[
        'group flex items-center justify-between p-2 my-1 rounded border transition-all text-xs font-sans select-none cursor-move',
        isDragOver
          ? 'border-blue-500 bg-blue-50 shadow'
          : selectedId !== node.id && [oaStyle.bg.hover, 'border-slate-400 dark:border-slate-700'],
        selectedId === node.id
          ? [oaStyle.bg.primary, 'font-semibold']
          : ''
      ]">
      <div class="flex items-center gap-1.5 flex-1">
        <span v-if="sortable" class="text-gray-400"><i class="fas fa-bars"></i></span>

        <button v-if="node.children?.length" @click.stop="isOpen = !isOpen"
          class="w-4 h-4 flex items-center justify-center">
          <i v-if="isOpen" class="fas fa-chevron-down"></i>
          <i v-else class="fas fa-chevron-right"></i>
        </button>

        <span v-else class="w-4"></span>

        <span v-html="node.icon"></span>

        <span :class="[oaStyle.text.secondary]">
          {{ node.code }}
        </span>
      </div>

      <div class="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
        <button @click.stop="$emit('add-child', node)" class="w-5 h-5 text-green-500">
          <i class="fas fa-plus-circle text-md"></i>
        </button>
      </div>
    </div>

    <div v-if="node.children?.length && isOpen" :class="[oaStyle.border.primary, 'pl-5 ml-2 border-l border-dashed']">
      <TreeNode v-for="child in node.children" :key="child.id" :node="child" :selected-id="selectedId"
        @select="$emit('select', $event)" @add-child="$emit('add-child', $event)"
        @drag-node="handleChildDrag" :sortable="sortable" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss'
import { StructureType } from '@/types/entities';
import { ref } from 'vue'

const props = defineProps<{
  node: StructureType
  selectedId?: number
  sortable?: boolean
}>()

const emit = defineEmits([
  'select',
  'add-child',
  'drag-node'
])

const isOpen = ref(true)
const isDragOver = ref(false)

const onDragStart = (event: DragEvent) => {
  if (!event.dataTransfer) return

  event.dataTransfer.effectAllowed = 'move'
  event.dataTransfer.setData(
    'application/menu-node',
    String(props.node.id)
  )
}

const onDragEnter = () => {
  isDragOver.value = true
}

const onDragLeave = () => {
  isDragOver.value = false
}

const onDragEnd = () => {
  isDragOver.value = false
}

const onDrop = (event: DragEvent) => {
  isDragOver.value = false

  if (!event.dataTransfer) return

  const dragId = Number(
    event.dataTransfer.getData(
      'application/menu-node'
    )
  )

  if (
    !dragId ||
    dragId === props.node.id
  ) {
    return
  }

  emit(
    'drag-node',
    dragId,
    props.node.id
  )
}

const handleChildDrag = (
  dragId: number,
  targetId: number
) => {
  emit(
    'drag-node',
    dragId,
    targetId
  )
}
</script>