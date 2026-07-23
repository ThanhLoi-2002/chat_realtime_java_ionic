<template>
    <!-- MAIN CONTENT -->
    <main class="h-full bg-gray-50 text-gray-800 overflow-y-auto p-6">
        <!-- Page Header -->
        <div class="mb-6">
            <h1 class="text-xl font-bold text-gray-900">Thống kê người quan tâm</h1>
            <p class="text-xs text-gray-500 mt-0.5">
                Theo dõi hoạt động quan tâm/bỏ quan tâm và thông tin người dùng đang quan tâm OA của bạn
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

            <!-- Grid các thẻ chỉ số (3 cột) -->
            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">

                <!-- Card 1: Tổng số người dùng đang quan tâm OA -->
                <div class="border-r border-gray-100 last:border-none pr-4">
                    <div class="flex items-center text-xs text-gray-500 mb-1">
                        <span>Tổng số người dùng đang quan tâm OA</span>
                        <i class="fas fa-circle-question ml-1 text-gray-400"></i>
                    </div>
                    <div class="text-2xl font-bold text-gray-900 mb-1">162</div>
                    <div class="flex items-center text-xs text-green-600 font-medium">
                        <i class="fas fa-arrow-up mr-1 text-[10px]"></i>
                        <span>11,72% So với 7 ngày trước</span>
                    </div>
                </div>

                <!-- Card 2: Số người dùng quan tâm -->
                <div class="border-r border-gray-100 last:border-none pr-4">
                    <div class="flex items-center text-xs text-gray-500 mb-1">
                        <span>Số người dùng quan tâm</span>
                        <i class="fas fa-circle-question ml-1 text-gray-400"></i>
                    </div>
                    <div class="text-2xl font-bold text-gray-900 mb-1">28</div>
                    <div class="flex items-center text-xs text-green-600 font-medium">
                        <i class="fas fa-arrow-up mr-1 text-[10px]"></i>
                        <span>833,33% So với 7 ngày trước</span>
                    </div>
                </div>

                <!-- Card 3: Số người dùng bỏ quan tâm -->
                <div>
                    <div class="flex items-center text-xs text-gray-500 mb-1">
                        <span>Số người dùng bỏ quan tâm</span>
                        <i class="fas fa-circle-question ml-1 text-gray-400"></i>
                    </div>
                    <div class="text-2xl font-bold text-gray-900 mb-1">11</div>
                    <div class="flex items-center text-xs text-green-600 font-medium">
                        <i class="fas fa-arrow-up mr-1 text-[10px]"></i>
                        <span>175,00% So với 7 ngày trước</span>
                    </div>
                </div>

            </div>
        </div>

        <!-- SECTION 2: Thống kê người quan tâm theo ngày -->
        <div class="bg-white border border-gray-200 rounded-lg p-5 shadow-sm">
            <div class="flex justify-between items-center mb-6">
                <h2 class="text-sm font-bold text-gray-900">Thống kê người quan tâm theo ngày</h2>
                <button class="text-gray-400 hover:text-gray-600">
                    <i class="fas fa-download text-sm"></i>
                </button>
            </div>

            <!-- Khung chứa biểu đồ giả lập (Trục Y 2 bên giống mẫu) -->
            <div class="relative h-64 w-full flex items-end border-b border-l border-r border-gray-200 pb-2 px-2">

                <!-- Trục Y indicators bên trái (Số lượng quan tâm/bỏ quan tâm) -->
                <div
                    class="absolute inset-y-0 left-2 right-2 flex flex-col justify-between pointer-events-none opacity-40">
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">30</div>
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">25</div>
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">20</div>
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">15</div>
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">10</div>
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">5</div>
                    <div class="border-b border-gray-200 w-full text-[10px] text-gray-400 pl-1">0</div>
                </div>

                <!-- Trục Y indicators bên phải (Tổng số người đang quan tâm OA) -->
                <div
                    class="absolute inset-y-0 right-2 flex flex-col justify-between pointer-events-none opacity-40 text-right pr-1">
                    <div class="text-[10px] text-gray-400">166</div>
                    <div class="text-[10px] text-gray-400">165</div>
                    <div class="text-[10px] text-gray-400">164</div>
                    <div class="text-[10px] text-gray-400">163</div>
                    <div class="text-[10px] text-gray-400">162</div>
                    <div class="text-[10px] text-gray-400">161</div>
                    <div class="text-[10px] text-gray-400">160</div>
                </div>

                <!-- SVG vẽ biểu đồ đường & cột minh họa -->
                <svg class="absolute inset-x-6 inset-y-6 w-[91%] h-[80%] overflow-visible" preserveAspectRatio="none"
                    viewBox="0 0 100 100">
                    <!-- Cột ngày 30/05: Xanh lá (quan tâm nhiều) và Hồng (bỏ quan tâm ít) -->
                    <rect x="4" y="15" width="4" height="85" fill="#22C55E" rx="0.5" />
                    <rect x="9" y="80" width="4" height="20" fill="#FCA5A5" rx="0.5" />

                    <!-- Cột ngày 31/05 -->
                    <rect x="22" y="97" width="4" height="3" fill="#FCA5A5" rx="0.5" />

                    <!-- Cột ngày 02/06 -->
                    <rect x="58" y="90" width="4" height="10" fill="#FCA5A5" rx="0.5" />

                    <!-- Cột ngày 03/06 -->
                    <rect x="70" y="93" width="4" height="7" fill="#22C55E" rx="0.5" />
                    <rect x="75" y="95" width="4" height="5" fill="#FCA5A5" rx="0.5" />

                    <!-- Cột ngày 05/06 -->
                    <rect x="94" y="95" width="4" height="5" fill="#22C55E" rx="0.5" />
                    <rect x="99" y="95" width="4" height="5" fill="#FCA5A5" rx="0.5" />

                    <!-- Đường biểu đồ màu xanh dương (Tổng số người đang quan tâm OA) -->
                    <path d="M 6,33 L 30,50 L 54,50 L 78,70 L 90,70 L 102,70" fill="none" stroke="#2563EB"
                        stroke-width="2" />
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
            <div class="mt-10 flex flex-wrap items-center gap-6 text-xs text-gray-600">
                <div class="flex items-center space-x-1.5">
                    <span class="w-3 h-3 bg-green-500 rounded-sm inline-block"></span>
                    <span>Số người dùng quan tâm</span>
                </div>
                <div class="flex items-center space-x-1.5">
                    <span class="w-3 h-3 bg-red-300 rounded-sm inline-block"></span>
                    <span>Số người dùng bỏ quan tâm</span>
                </div>
                <div class="flex items-center space-x-1.5">
                    <span class="w-3 h-3 bg-blue-600 rounded-full inline-block"></span>
                    <span>Tổng số người dùng quan tâm OA</span>
                </div>
            </div>

        </div>
    </main>
</template>

<script setup lang="ts">
// Giao diện tĩnh trực quan, có thể truyền props hoặc gọi API khi cần thay đổi dữ liệu động.
</script>