<template>
  <div :class="[oaStyle.bg.primary, oaStyle.text.primary]" class="flex flex-col h-full p-2">
    
    <!-- Main Workspace: Chat Interface -->
    <div class="flex-1 flex overflow-hidden">
      <!-- Danh sách hội thoại (Conversations List) -->
      <section class="w-80 bg-white dark:bg-slate-800 border-r border-slate-200 dark:border-slate-700 flex flex-col shrink-0">
        <!-- Tab bộ lọc tin nhắn -->
        <div class="p-3 border-b border-slate-100 dark:border-slate-700 flex items-center justify-between">
          <div class="flex items-center space-x-1 text-sm font-semibold cursor-pointer">
            <span>Tin nhắn chính</span>
            <span class="text-xs">▼</span>
          </div>
          <div class="flex items-center space-x-2 text-slate-400">
            <button class="hover:text-slate-600">🔍</button>
            <button class="hover:text-slate-600">⚙️</button>
          </div>
        </div>

        <!-- Khung nhập nhanh tư vấn trong kỳ -->
        <div class="p-3 bg-amber-50/60 dark:bg-amber-950/20 border-b border-amber-100 dark:border-amber-900/30 text-xs">
          <div class="flex justify-between text-slate-500 mb-1">
            <span>Tin tư vấn trong kỳ</span>
            <span>0/2000 ⓘ</span>
          </div>
        </div>

        <!-- Danh sách user chat -->
        <div class="flex-1 overflow-y-auto divide-y divide-slate-100 dark:divide-slate-700/50">
          <div v-for="(chat, index) in chatList" :key="index" 
               :class="['p-3 flex space-x-3 cursor-pointer transition', chat.active ? 'bg-blue-50/60 dark:bg-slate-700/50 border-l-4 border-blue-600' : 'hover:bg-slate-50 dark:hover:bg-slate-700/30']">
            <div class="relative shrink-0">
              <img :src="chat.avatar" class="w-10 h-10 rounded-full object-cover" alt="avatar" />
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex justify-between items-baseline">
                <h4 class="text-xs font-bold truncate">{{ chat.name }}</h4>
                <span class="text-[10px] text-slate-400">{{ chat.time }}</span>
              </div>
              <p class="text-xs text-slate-500 dark:text-slate-400 truncate mt-0.5">{{ chat.lastMessage }}</p>
              <div class="mt-1 flex items-center justify-between">
                <span class="text-[10px] px-1.5 py-0.5 bg-slate-100 dark:bg-slate-700 rounded text-slate-600 dark:text-slate-300">{{ chat.tag }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Khung chat chi tiết bên phải (Chat Detail Area) -->
      <main class="flex-1 flex flex-col bg-slate-50 dark:bg-slate-900 overflow-hidden">
        
        <!-- Header thông tin khách hàng & phân công nhân viên -->
        <div class="bg-white dark:bg-slate-800 px-4 py-3 border-b border-slate-200 dark:border-slate-700 flex items-center justify-between shrink-0">
          <div class="flex items-center space-x-3">
            <div class="w-9 h-9 rounded-full bg-emerald-500 text-white font-bold flex items-center justify-center text-sm">H</div>
            <div>
              <h3 class="text-sm font-bold">Hà Minh Trường</h3>
              <div class="flex items-center space-x-2 mt-0.5">
                <button class="text-[11px] px-2 py-0.5 bg-slate-100 dark:bg-slate-700 rounded border border-slate-200 dark:border-slate-600 text-slate-600 dark:text-slate-300 flex items-center space-x-1">
                  <span>Chỉ định nhân viên xử lý</span> <span>▼</span>
                </button>
                <button class="text-[11px] px-2 py-0.5 bg-slate-100 dark:bg-slate-700 rounded border border-slate-200 dark:border-slate-600 text-slate-600 dark:text-slate-300 flex items-center space-x-1">
                  <span>Gắn nhãn quy trình</span> <span>▼</span>
                </button>
              </div>
            </div>
          </div>

          <!-- Các công cụ góc phải -->
          <div class="flex items-center space-x-3 text-slate-400">
            <button class="hover:text-slate-600">📞</button>
            <button class="hover:text-slate-600">🔍</button>
            <button class="hover:text-slate-600">⋯</button>
          </div>
        </div>

        <!-- Khung hiển thị nội dung tin nhắn -->
        <div class="flex-1 p-4 overflow-y-auto space-y-4">
          <!-- Mốc thời gian -->
          <div class="flex justify-center">
            <span class="text-[11px] bg-slate-200 dark:bg-slate-700 text-slate-600 dark:text-slate-300 px-3 py-1 rounded-full">
              09:10 Thứ năm, 30 tháng 5 năm 2024
            </span>
          </div>

          <!-- Tin nhắn mẫu từ OA -->
          <div class="flex justify-end">
            <div class="flex items-end space-x-2 max-w-lg">
              <div class="bg-blue-500 text-white text-xs px-4 py-2.5 rounded-2xl rounded-tr-sm shadow-sm">
                Chào mừng bạn đến với Official Account Abaha Global trên Zalo
              </div>
              <span class="text-[10px] text-slate-400 shrink-0">09:10</span>
            </div>
          </div>
        </div>

        <!-- Thanh cảnh báo hạn mức tương tác 7 ngày -->
        <div class="bg-amber-50 dark:bg-amber-950/40 border-t border-amber-100 dark:border-amber-900/50 px-4 py-2 text-center text-xs text-amber-700 dark:text-amber-400 shrink-0">
          ⚠️ OA không thể gửi tin Tư vấn cho Người dùng không phát sinh tương tác với OA trong vòng 7 ngày gần nhất
        </div>

        <!-- Footer khung soạn thảo tin nhắn -->
        <div class="bg-white dark:bg-slate-800 border-t border-slate-200 dark:border-slate-700 p-3 shrink-0 flex items-center space-x-3">
          <div class="flex items-center space-x-2 text-slate-400">
            <button class="hover:text-slate-600 p-1">📎</button>
            <button class="hover:text-slate-600 p-1">🖼️</button>
            <button class="hover:text-slate-600 p-1">⚡</button>
          </div>
          <div class="flex-1">
            <input type="text" placeholder="Nhập tin nhắn..." class="w-full text-xs bg-slate-100 dark:bg-slate-700 border-none rounded-lg px-3 py-2 outline-none" />
          </div>
          <button class="bg-blue-600 text-white text-xs px-4 py-2 rounded-lg font-medium hover:bg-blue-700 transition">Gửi</button>
        </div>

      </main>

    </div>
  </div>
</template>

<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss';
import { ref } from 'vue'

const navItems = [
  { name: 'Trang chủ', href: '#', active: false },
  { name: 'Chat', href: '#', active: true },
  { name: 'Chatbot', href: '#', active: false },
  { name: 'Nội dung', href: '#', active: false },
  { name: 'Thống kê', href: '#', active: false },
  { name: 'Quản lý', href: '#', active: false },
  { name: 'Quảng cáo', href: '#', active: false },
  { name: 'Khác', href: '#', active: false },
]

// Mock dữ liệu danh sách chat bên trái
const chatList = ref([
  {
    name: 'Trang Nhung Abaha',
    avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100',
    time: '11:10',
    lastMessage: 'OA: Chào mừng bạn đến v...',
    tag: 'Admin',
    active: true
  },
  {
    name: 'Bọc Xốp Bình Dương',
    avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100',
    time: 'Hôm qua',
    lastMessage: 'OA: Dạ vâng ạ. Mai...',
    tag: 'Chưa quan tâm',
    active: false
  },
  {
    name: 'le tháng',
    avatar: 'https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=100',
    time: 'Hôm qua',
    lastMessage: 'OA: C cần hỗ trợ gì a',
    tag: 'Chưa quan tâm',
    active: false
  },
  {
    name: 'Văn Phạm',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100',
    time: 'Hôm qua',
    lastMessage: 'OA: Dạ chuyên viên tư v...',
    tag: 'Chưa quan tâm',
    active: false
  },
  {
    name: 'Đức Tiên Haravan',
    avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100',
    time: 'Thứ 2',
    lastMessage: 'OA: Anh vui lòng check ti...',
    tag: 'Chưa quan tâm',
    active: false
  }
])
</script>