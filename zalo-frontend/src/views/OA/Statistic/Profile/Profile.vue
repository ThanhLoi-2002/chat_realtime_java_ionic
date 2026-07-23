<template>
    <!-- MAIN CONTENT -->
    <main class="h-full bg-gray-50 text-gray-800 overflow-y-auto p-6">
        <!-- Page Header -->
        <div class="mb-6">
            <h1 class="text-xl font-bold text-gray-900">Trang thông tin OA</h1>
            <p class="text-xs text-gray-500 mt-0.5">
                Theo dõi lượng truy cập và tương tác tại trang thông tin OA
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
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-2xl">

                <!-- Card 1: Số người dùng xem trang -->
                <div class="border-r border-gray-100 pr-4">
                    <div class="flex items-center text-xs text-gray-500 mb-1">
                        <span>Số người dùng xem trang</span>
                        <i class="fas fa-circle-question ml-1 text-gray-400"></i>
                    </div>
                    <div class="text-2xl font-bold text-gray-900 mb-1">51</div>
                    <div class="flex items-center text-xs text-green-600 font-medium">
                        <i class="fas fa-arrow-up mr-1 text-[10px]"></i>
                        <span>96,15% So với 7 ngày trước</span>
                    </div>
                </div>

                <!-- Card 2: Lượt xem trang -->
                <div>
                    <div class="flex items-center text-xs text-gray-500 mb-1">
                        <span>Lượt xem trang</span>
                        <i class="fas fa-circle-question ml-1 text-gray-400"></i>
                    </div>
                    <div class="text-2xl font-bold text-gray-900 mb-1">80</div>
                    <div class="flex items-center text-xs text-green-600 font-medium">
                        <i class="fas fa-arrow-up mr-1 text-[10px]"></i>
                        <span>110,53% So với 7 ngày trước</span>
                    </div>
                </div>

            </div>
        </div>

        <!-- SECTION 2: Thống kê tương tác trang thông tin OA theo ngày -->
        <div class="bg-white border border-gray-200 rounded-lg p-5 shadow-sm">
            <div class="flex justify-between items-center mb-6">
                <h2 class="text-sm font-bold text-gray-900">Thống kê tương tác trang thông tin OA theo ngày</h2>
                <button class="text-gray-400 hover:text-gray-600">
                    <i class="fas fa-download text-sm"></i>
                </button>
            </div>

            <!-- Khung chứa biểu đồ giả lập -->
            <div class="relative h-64 w-full flex items-end border-b border-l border-r border-gray-200 pb-2 px-2">
                <!-- Trục Y indicators bên trái -->
                <div
                    class="absolute inset-y-0 left-2 right-2 flex flex-col justify-between pointer-events-none opacity-40">
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">70</div>
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">60</div>
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">50</div>
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">40</div>
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">30</div>
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">20</div>
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">10</div>
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">0</div>
                </div>

                <!-- SVG vẽ biểu đồ đường & cột minh họa -->
                <svg class="absolute inset-x-6 inset-y-6 w-[93%] h-[80%] overflow-visible" preserveAspectRatio="none"
                    viewBox="0 0 100 100">
                    <!-- Cột thống kê ngày 30/05 (Màu xanh nhạt) -->
                    <rect x="5" y="50" width="8" height="50" fill="#E0F2FE" rx="1" />

                    <!-- Đường biểu đồ màu xanh dương -->
                    <path d="M 9,15 L 35,90 L 60,92 L 85,88" fill="none" stroke="#2563EB" stroke-width="2" />
                    <!-- Đường biểu đồ màu vàng/cam -->
                    <path d="M 9,65 L 35,95 L 60,94 L 85,92" fill="none" stroke="#CA8A04" stroke-width="2" />
                </svg>

                <!-- Trục X (Ngày tháng) -->
                <div class="absolute -bottom-6 inset-x-4 flex justify-between text-[11px] text-gray-500">
                    <span>30/05</span>
                    <span>31/05</span>
                    <span>01/06</span>
                    <span>02/06</span>
                    <span>03/06</span>
                    <span>04/06</span>
                    <span>05/06</span>
                </div>
            </div>

            <!-- Chú thích (Legend) ở dưới biểu đồ -->
            <div class="mt-10 flex flex-wrap items-center gap-4 text-xs text-gray-600">
                <div class="flex items-center space-x-1.5">
                    <span class="w-3 h-3 bg-blue-100 border border-blue-300 rounded-sm inline-block"></span>
                    <span>Số người dùng xem trang</span>
                </div>
                <div class="flex items-center space-x-1.5">
                    <span class="w-3 h-3 bg-blue-600 rounded-full inline-block"></span>
                    <span>Lượt truy cập</span>
                </div>
                <div class="flex items-center space-x-1.5">
                    <span class="w-3 h-3 bg-green-500 rounded-full inline-block"></span>
                    <span>Lượt nhắn nút Nhắn tin</span>
                </div>
                <div class="flex items-center space-x-1.5">
                    <span class="w-3 h-3 bg-yellow-500 rounded-full inline-block"></span>
                    <span>Lượt nhắn nút Quan tâm</span>
                </div>
                <div class="flex items-center space-x-1.5">
                    <span class="w-3 h-3 bg-orange-500 rounded-full inline-block"></span>
                    <span>Lượt nhắn nút Tương tác nhanh</span>
                </div>
                <div class="flex items-center space-x-1.5">
                    <span class="w-3 h-3 bg-purple-500 rounded-full inline-block"></span>
                    <span>Lượt nhắn nút Dịch vụ</span>
                </div>
                <div class="flex items-center space-x-1.5">
                    <span class="w-3 h-3 bg-red-500 rounded-full inline-block"></span>
                    <span>Lượt nhắn xem bài viết</span>
                </div>
            </div>

        </div>
    </main>
</template>

<script setup lang="ts">
// Giao diện tĩnh trực quan, có thể truyền props hoặc gọi API khi cần thay đổi dữ liệu động.
</script>