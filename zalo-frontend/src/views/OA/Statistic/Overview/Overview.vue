<template>
    <!-- MAIN CONTENT -->
    <main class="bg-gray-50 text-gray-800 overflow-y-auto p-6">
        <!-- Page Header -->
        <div class="mb-6">
            <h1 class="text-xl font-bold text-gray-900">Tổng quan hoạt động</h1>
            <p class="text-xs text-gray-500 mt-0.5">
                Theo dõi lượng truy cập và tương tác của người dùng đối với OA
            </p>
        </div>

        <!-- Date Range Filter -->
        <div class="mb-6">
            <div
                class="inline-flex items-center bg-white border border-gray-300 rounded-lg px-3 py-1.5 text-xs text-gray-700 cursor-pointer hover:bg-gray-50 shadow-sm">
                <span class="font-medium">30/05/2024 - 05/06/2024</span>
                <i class="fas fa-calendar ml-2 text-gray-400"></i>
            </div>
        </div>

        <!-- SECTION 1: Chỉ số chính -->
        <div class="bg-white border border-gray-200 rounded-lg p-5 mb-6 shadow-sm">
            <h2 class="text-sm font-bold text-gray-900 mb-4">Chỉ số chính</h2>

            <!-- Grid các thẻ chỉ số -->
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">

                <!-- Card 1 -->
                <div class="border-r border-gray-100 last:border-none pr-4">
                    <div class="flex items-center text-xs text-gray-500 mb-1">
                        <span>Số người dùng xem trang thông tin OA</span>
                        <i class="fas fa-circle-question ml-1 text-gray-400"></i>
                    </div>
                    <div class="text-2xl font-bold text-gray-900 mb-1">51</div>
                    <div class="flex items-center text-xs text-green-600 font-medium">
                        <i class="fas fa-arrow-up mr-1 text-[10px]"></i>
                        <span>96,15% So với 7 ngày trước</span>
                    </div>
                </div>

                <!-- Card 2 -->
                <div class="border-r border-gray-100 last:border-none pr-4">
                    <div class="flex items-center text-xs text-gray-500 mb-1">
                        <span>Số người dùng gửi tin nhắn đến OA</span>
                        <i class="fas fa-circle-question ml-1 text-gray-400"></i>
                    </div>
                    <div class="text-2xl font-bold text-gray-900 mb-1">7</div>
                    <div class="flex items-center text-xs text-red-600 font-medium">
                        <i class="fas fa-arrow-down mr-1 text-[10px]"></i>
                        <span>100,00% So với 7 ngày trước</span>
                    </div>
                </div>

                <!-- Card 3 -->
                <div class="border-r border-gray-100 last:border-none pr-4">
                    <div class="flex items-center text-xs text-gray-500 mb-1">
                        <span>Số người dùng tương tác thanh menu</span>
                        <i class="fas fa-circle-question ml-1 text-gray-400"></i>
                    </div>
                    <div class="text-2xl font-bold text-gray-900 mb-1">14</div>
                    <div class="flex items-center text-xs text-red-600 font-medium">
                        <i class="fas fa-arrow-down mr-1 text-[10px]"></i>
                        <span>171,43% So với 7 ngày trước</span>
                    </div>
                </div>

                <!-- Card 4 -->
                <div>
                    <div class="flex items-center text-xs text-gray-500 mb-1">
                        <span>Số người quan tâm mới</span>
                        <i class="fas fa-circle-question ml-1 text-gray-400"></i>
                    </div>
                    <div class="text-2xl font-bold text-gray-900 mb-1">28</div>
                    <div class="flex items-center text-xs text-green-600 font-medium">
                        <i class="fas fa-arrow-up mr-1 text-[10px]"></i>
                        <span>833,33% So với 7 ngày trước</span>
                    </div>
                </div>

            </div>

            <!-- Dòng thứ 2 của chỉ số chính (nếu có) -->
            <div class="mt-6 pt-6 border-t border-gray-100 grid grid-cols-1 md:grid-cols-4 gap-6">
                <div>
                    <div class="flex items-center text-xs text-gray-500 mb-1">
                        <span>Số người dùng xem nội dung</span>
                        <i class="fas fa-circle-question ml-1 text-gray-400"></i>
                    </div>
                    <div class="text-2xl font-bold text-gray-900 mb-1">3</div>
                    <div class="flex items-center text-xs text-red-600 font-medium">
                        <i class="fas fa-arrow-down mr-1 text-[10px]"></i>
                        <span>133,33% So với 7 ngày trước</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- SECTION 2: Thống kê theo ngày (Biểu đồ giả lập) -->
        <div class="bg-white border border-gray-200 rounded-lg p-5 shadow-sm">
            <div class="flex justify-between items-center mb-6">
                <h2 class="text-sm font-bold text-gray-900">Thống kê theo ngày</h2>
                <button class="text-gray-400 hover:text-gray-600">
                    <i class="fas fa-download text-sm"></i>
                </button>
            </div>

            <!-- Khung chứa biểu đồ line chart minh họa -->
            <div class="relative h-64 w-full flex items-end border-b border-l border-gray-200 pb-2 pl-2">
                <!-- Trục Y indicators (gạch ngang mờ) -->
                <div class="absolute inset-0 flex flex-col justify-between pointer-events-none opacity-40">
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">40</div>
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">30</div>
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">20</div>
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">10</div>
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">0</div>
                </div>

                <!-- SVG vẽ đường biểu đồ mẫu tương ứng màu sắc như hình -->
                <svg class="absolute inset-x-4 inset-y-6 w-[95%] h-[80%] overflow-visible" preserveAspectRatio="none"
                    viewBox="0 0 100 100">
                    <!-- Đường màu xanh dương -->
                    <path d="M 5,20 L 35,90 L 65,95 L 95,90" fill="none" stroke="#2563EB" stroke-width="2" />
                    <!-- Đường màu tím/hồng -->
                    <path d="M 5,40 L 35,92 L 65,96 L 95,95" fill="none" stroke="#9333EA" stroke-width="2" />
                    <!-- Đường màu cam -->
                    <path d="M 5,90 L 35,95 L 65,93 L 95,85" fill="none" stroke="#EA580C" stroke-width="2" />
                </svg>

                <!-- Trục X (Ngày tháng) -->
                <div class="absolute -bottom-6 inset-x-0 flex justify-between text-[11px] text-gray-500 px-2">
                    <span>30/05</span>
                    <span>31/05</span>
                    <span>01/06</span>
                    <span>02/06</span>
                    <span>03/06</span>
                    <span>04/06</span>
                    <span>05/06</span>
                </div>
            </div>

            <!-- Khoảng trống phụ để chữ ngày không bị tràn khung -->
            <div class="mt-8"></div>
        </div>
    </main>
</template>

<script setup lang="ts">
// Không cần logic phức tạp cho giao diện tĩnh này, có thể mở rộng state nếu cần gọi API thống kê.
</script>