<template>
  <div :class="[oaStyle.bg.primary, oaStyle.text.primary]" class="transition-colors duration-300">

    <!-- Main Content Layout -->
    <main class="mx-auto p-3 grid grid-cols-1 lg:grid-cols-4 gap-3">

      <!-- Left Sidebar (Width: 1 Col) -->
      <aside class="space-y-4 lg:col-span-1">
        <!-- Promo Banner -->
        <div
          class="bg-linear-to-r from-orange-500 to-amber-500 rounded-xl p-4 text-white shadow-sm relative overflow-hidden">
          <div class="relative z-10">
            <span class="bg-white/20 text-xs px-2 py-0.5 rounded">ABAHA GIẢI PHÁP</span>
            <h2 class="font-medium text-lg mt-1">ZALO MINI APP BÁN HÀNG</h2>
            <p class="text-xs text-orange-100 mt-1">Nền tảng chuyển đổi số toàn diện</p>
            <button
              class="mt-3 bg-white text-orange-600 font-medium text-xs px-3 py-1.5 rounded-lg shadow hover:bg-orange-50 transition">Tìm
              hiểu thêm</button>
          </div>
        </div>

        <!-- OA Info Card -->
        <div :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'rounded-xl p-4 shadow-sm border']">
          <div class="flex items-center space-x-3">
            <div
              :class="['w-12 h-12 bg-blue-100 dark:bg-blue-900 rounded-lg flex items-center justify-center text-blue-600 dark:text-blue-300 font-medium text-xl']">
              A</div>
            <div>
              <span class="font-medium text-lg">Abaha Global</span>
              <p :class="[oaStyle.text.secondary, 'text-xs']">162 người quan tâm</p>
            </div>
          </div>
        </div>

        <!-- Setup Account Progress -->
        <div :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'rounded-xl p-4 shadow-sm border']">
          <div class="flex justify-between items-center mb-2">
            <span :class="[oaStyle.text.primary, 'text-md font-medium']">Thiết lập tài khoản</span>
            <span class="text-xs text-emerald-600">Đã hoàn thành 100%</span>
          </div>
          <div class="w-full h-2 rounded-full overflow-hidden mb-4">
            <div class="bg-emerald-500 h-full w-full"></div>
          </div>
          <ul class="space-y-2 text-xs">
            <li v-for="step in setupSteps" :key="step.text"
              :class="[oaStyle.text.secondary, 'flex items-center space-x-2']">
              <span
                :class="[oaStyle.border.secondary, 'w-4 h-4 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center text-[10px] border font-medium']">✓</span>
              <span>{{ step.text }}</span>
            </li>
          </ul>
        </div>

        <!-- Notifications Box -->
        <div :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'rounded-xl p-4 shadow-sm border']">
          <div :class="[oaStyle.text.primary, 'text-md font-medium mb-3']">Thông báo</div>
          <div :class="[oaStyle.text.secondary, 'space-y-2 text-sm']">
            <p>• Bắt đầu với Zalo Official Account - Doanh nghiệp</p>
            <p>• Làm thế nào để thu hút người dùng quan tâm OA</p>
          </div>
          <a href="#" class="block text-xs text-blue-600 dark:text-blue-400 font-medium mt-3 hover:underline">Xem các
            thông báo khác &gt;</a>
        </div>
      </aside>

      <!-- Right Main Content Area (Width: 3 Cols) -->
      <div class="space-y-4 lg:col-span-3">

        <!-- Section 1: 30 Minutes Overview -->
        <div :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'rounded-xl p-4 shadow-sm border']">
          <div class="flex justify-between items-center mb-4">
            <div :class="[oaStyle.text.primary, 'text-md font-medium uppercase tracking-wider']">Hôm nay</div>
            <span :class="[oaStyle.text.secondary, 'text-xs font-medium']">30 PHÚT VỪA QUA</span>
          </div>

          <!-- Sub-metrics Grid -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
            <div :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'p-3 rounded-lg border']">
              <span :class="[oaStyle.text.primary, 'text-xs flex items-center justify-between']">
                Lượt xem bài viết <span class="cursor-help text-slate-400">ⓘ</span>
              </span>
              <span class="text-xl font-medium mt-1 block">0</span>
            </div>
            <div :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'p-3 rounded-lg border']">
              <span :class="[oaStyle.text.primary, 'text-xs flex items-center justify-between']">
                Lượt chia sẻ bài viết <span class="cursor-help text-slate-400">ⓘ</span>
              </span>
              <span class="text-xl font-medium mt-1 block">0</span>
            </div>
            <div :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'p-3 rounded-lg border']">
              <span :class="[oaStyle.text.primary, 'text-xs flex items-center justify-between']">
                Lượt quan tâm OA <span class="cursor-help text-slate-400">ⓘ</span>
              </span>
              <span class="text-xl font-medium mt-1 block">1</span>
            </div>
          </div>

          <!-- Chart Tabs & Visual Mockup -->
          <div :class="[oaStyle.border.secondary, 'border-b flex space-x-6 text-xs mb-4']">
            <button
              class="pb-2 text-blue-600 dark:text-blue-400 border-b-2 border-blue-600 dark:border-blue-400 cursor-pointer">Lượt
              xem bài viết</button>
            <button :class="[oaStyle.text.hover, oaStyle.text.secondary, 'pb-2 cursor-pointer']">Lượt chia sẻ bài
              viết</button>
            <button :class="[oaStyle.text.hover, oaStyle.text.secondary, 'pb-2 cursor-pointer']">Lượt quan tâm
              OA</button>
          </div>

          <!-- Simple Bar Chart Simulation -->
          <div
            :class="[oaStyle.border.secondary, 'h-40 flex items-end justify-between px-2 pt-6 pb-2 border-b relative']">
            <div class="absolute inset-0 flex flex-col justify-between pointer-events-none opacity-20">
              <div :class="[oaStyle.border.secondary, 'border-b w-full text-xs']">2</div>
              <div :class="[oaStyle.border.secondary, 'border-b w-full text-xs']">1</div>
              <div :class="[oaStyle.border.secondary, 'border-b w-full text-xs']">0</div>
            </div>
            <!-- Mock Chart Bars -->
            <div v-for="day in 31" :key="day" class="flex flex-col items-center z-10 flex-1">
              <div v-if="day === 27" class="w-4 bg-blue-400 dark:bg-blue-500 rounded-t h-12"></div>
              <span class="text-[10px] text-slate-400 mt-2">{{ 32 - day }}</span>
            </div>
          </div>
          <div class="flex justify-end mt-2">
            <button class="text-xs text-blue-600 dark:text-blue-400 font-medium hover:underline">Xem thêm chi tiết theo
              ngày &gt;</button>
          </div>
        </div>

        <!-- Section 2: Interaction Statistics -->
        <div :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'rounded-xl p-4 shadow-sm border']">
          <div class="flex justify-between items-center mb-4">
            <div :class="[oaStyle.text.primary, 'text-md font-medium uppercase tracking-wider']">THỐNG KÊ TƯƠNG TÁC
            </div>
            <select :class="[oaStyle.bg.secondary, oaStyle.border.secondary, 'text-xs rounded px-2 py-1 outline-none']">
              <option>7 ngày trước</option>
            </select>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <!-- Left Data Metrics -->
            <div :class="[oaStyle.border.secondary, 'space-y-4 border-r pr-4']">
              <div>
                <span :class="[oaStyle.text.secondary, 'text-xs justify-between']">Lượt xem bài viết
                  <span>ⓘ</span></span>
                <div class="flex items-baseline justify-between mt-1">
                  <span class="text-lg font-medium">5</span>
                  <span class="text-xs text-rose-500">↓ -37%</span>
                </div>
              </div>
              <hr :class="[oaStyle.border.secondary]" />
              <div>
                <span :class="[oaStyle.text.secondary, 'text-xs justify-between']">Lượt chia sẻ bài viết
                  <span>ⓘ</span></span>
                <div class="flex items-baseline justify-between mt-1">
                  <span class="text-lg font-medium">0</span>
                  <span class="text-xs text-rose-500">↓ -100%</span>
                </div>
              </div>
              <hr :class="[oaStyle.border.secondary]" />
              <div>
                <span :class="[oaStyle.text.secondary, 'text-xs justify-between']">Người quan tâm OA
                  <span>ⓘ</span></span>
                <div class="flex items-baseline justify-between mt-1">
                  <span class="text-lg font-medium">28</span>
                  <span class="text-xs text-emerald-500">↑ 833%</span>
                </div>
              </div>
            </div>

            <!-- Right Interactive Articles Table -->
            <div class="md:col-span-2">
              <div :class="[oaStyle.text.primary, 'text-md font-medium mb-3']">Bài viết nhiều tương tác</div>
              <div class="overflow-x-auto">
                <table class="w-full text-xs text-left">
                  <thead>
                    <tr :class="[oaStyle.border.secondary, oaStyle.text.secondary, 'border-b']">
                      <th class="pb-2 font-medium">Tiêu đề</th>
                      <th class="pb-2 font-medium text-right">Lượt xem</th>
                      <th class="pb-2 font-medium text-right">Chia sẻ</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr :class="[oaStyle.border.secondary, 'border-b']">
                      <td :class="[oaStyle.text.secondary, 'py-3 font-medium']">Tạo Cửa hàng Online trên Zalo Miễn
                        Phí!</td>
                      <td class="py-3 text-right">5</td>
                      <td class="py-3 text-right">0</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>

      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { oaStyle } from '@/assets/tailwindcss'

// Các bước thiết lập tài khoản
const setupSteps = [
  { text: 'Địa chỉ' },
  { text: 'Bài viết' },
  { text: 'Lời chào' },
  { text: 'Thanh menu' },
]
</script>