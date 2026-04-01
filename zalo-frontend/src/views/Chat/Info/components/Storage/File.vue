<template lang="">
    <div>
      <!-- SEARCH -->
    <div class="p-3">
      <div class="flex items-center bg-[#374151] rounded-full px-3 py-2">
        <i class="fa fa-search text-gray-400 mr-2"></i>
        <input
          placeholder="Tìm kiếm File"
          class="bg-transparent outline-none text-sm w-full placeholder-gray-400"
        />
      </div>
    </div>

    <!-- FILTER -->
    <div class="flex gap-2 px-3 pb-2">
      <select class="bg-[#374151] text-xs px-3 py-1 rounded-full outline-none">
        <option>Loại</option>
      </select>

      <select class="bg-[#374151] text-xs px-3 py-1 rounded-full outline-none">
        <option>Người gửi</option>
      </select>

      <select class="bg-[#374151] text-xs px-3 py-1 rounded-full outline-none">
        <option>Ngày gửi</option>
      </select>
    </div>

    <!-- LIST -->
    <div class="flex-1 overflow-y-auto px-3 pb-4 space-y-4">

      <div v-for="group in files" :key="group.date">

        <!-- DATE -->
        <div class="text-gray-400 text-sm font-semibold mb-2">
          {{ group.date }}
        </div>

        <!-- ITEMS -->
        <div class="space-y-3">
          <div
            v-for="item in group.items"
            :key="item.id"
            class="flex items-center gap-3"
          >
            <!-- ICON -->
            <div
              class="w-12 h-12 rounded-lg flex items-center justify-center text-xs font-bold"
              :class="getFileColor(item.type)"
            >
              {{ item.type.toUpperCase() }}
            </div>

            <!-- INFO -->
            <div class="flex-1">
              <div class="font-medium text-sm">
                {{ item.name }}
              </div>

              <div class="text-xs text-gray-400 flex items-center gap-2 mt-1">
                <span>{{ item.size }}</span>

                <template v-if="item.downloaded">
                  <span class="flex items-center gap-1 text-green-400">
                    <i class="fa fa-check-circle"></i>
                    Đã có trên máy
                  </span>
                </template>

                <template v-else>
                  <span class="flex items-center gap-1 text-blue-400 cursor-pointer">
                    <i class="fa fa-download"></i>
                    Tải về để xem lâu dài
                  </span>
                </template>
              </div>
            </div>
          </div>
        </div>

      </div>

    </div>  
    </div>
</template>
<script setup lang="ts">
const files = [
  {
    date: 'Ngày 31 Tháng 3',
    items: [
      {
        id: 1,
        name: 'FarmHU.apk',
        size: '30.01 MB',
        type: 'apk',
        downloaded: false
      },
      {
        id: 2,
        name: 'NROx10.jar',
        size: '10.99 MB',
        type: 'jar',
        downloaded: true
      }
    ]
  }
]

const getFileColor = (type: string) => {
  switch (type) {
    case 'apk':
      return 'bg-cyan-500'
    case 'jar':
      return 'bg-sky-500'
    case 'rar':
      return 'bg-purple-500'
    default:
      return 'bg-gray-500'
  }
}
</script>