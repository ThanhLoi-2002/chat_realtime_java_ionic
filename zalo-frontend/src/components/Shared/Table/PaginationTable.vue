<script setup lang="ts">
import { ref, computed, watch } from "vue"
import {
    useVueTable,
    getCoreRowModel,
    getSortedRowModel,
    getFilteredRowModel,
    getPaginationRowModel,
    FlexRender
} from "@tanstack/vue-table"
import { IonButton } from "@ionic/vue"

const props = defineProps<{
    data: any[];
    columns: any[];
}>()

const sorting = ref<any>([])
const globalFilter = ref("")

// Make a reactive source from props.data
const dataRef = computed(() => props.data ?? [])

// Create table using the reactive dataRef
const table = useVueTable({
    data: dataRef,
    // columns: props.columns,

    get columns() {
        return props.columns
    },

    initialState: {
        pagination: { pageSize: 15 }
    },

    state: {
        get sorting() { return sorting.value },
        get globalFilter() { return globalFilter.value }
    },

    onSortingChange: updater => {
        sorting.value = typeof updater === "function" ? updater(sorting.value) : updater
    },

    onGlobalFilterChange: value => {
        globalFilter.value = value
    },

    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    getPaginationRowModel: getPaginationRowModel()
})

watch(
    () => props.columns,
    (newColumns) => {
        table.setOptions(prev => ({
            ...prev,
            columns: newColumns
        }))
    },
    { deep: true }
)
</script>

<template>

    <!-- SEARCH -->
    <!-- <input v-model="globalFilter" placeholder="Search..." class="border p-2 mb-3" /> -->
    <div class="w-full overflow-x-auto max-h-[75vh]">
        <table class="w-full min-w-max border border-gray-200 dark:border-gray-700
            bg-white dark:bg-gray-800 text-gray-800 dark:text-gray-200">

            <thead class="sticky top-0 z-10">
                <tr>
                    <th v-for="header in table.getHeaderGroups()[0].headers" :key="header.id" class="border border-gray-200 dark:border-gray-700 cursor-pointer
                        bg-gray-100 dark:bg-gray-700
                        hover:bg-gray-200 dark:hover:bg-gray-600 relative text-center py-2 pr-6" @click="header.column.toggleSorting()">
                        <div class="flex items-center justify-center gap-2">
                            <FlexRender :render="header.column.columnDef.header" :props="header.getContext()" />

                            <i :class="[
                                'fa-solid text-gray-500 dark:text-gray-300 absolute right-2 top-1/2 -translate-y-1/2',
                                !header.column.getIsSorted()
                                    ? 'fa-sort'
                                    : header.column.getIsSorted() === 'asc'
                                        ? 'fa-sort-up'
                                        : 'fa-sort-down'
                            ]"></i>
                        </div>
                    </th>
                </tr>
            </thead>

            <tbody>
                <tr v-for="row in table.getRowModel().rows" :key="row.id" class="group cursor-pointer">
                    <td v-for="cell in row.getVisibleCells()" :key="cell.id" class="border border-gray-200 dark:border-gray-700
                        p-2 bg-white dark:bg-gray-800
                        group-hover:bg-gray-200 dark:group-hover:bg-gray-600" :style="{ width: cell.column.columnDef.meta?.width }">
                        <FlexRender :render="cell.column.columnDef.cell ?? cell.getValue()"
                            :props="cell.getContext()" />
                    </td>
                </tr>
            </tbody>

        </table>
    </div>

    <!-- PAGINATION -->

    <div class="flex gap-2 mt-4 items-center text-gray-700 dark:text-gray-200 z-1 ">

        <ion-button @click="table.previousPage()" :disabled="!table.getCanPreviousPage()">
            Prev
        </ion-button>

        <span>
            Page {{ table.getState().pagination.pageIndex + 1 }}
        </span>

        <ion-button @click="table.nextPage()" :disabled="!table.getCanNextPage()">
            Next
        </ion-button>

    </div>
</template>