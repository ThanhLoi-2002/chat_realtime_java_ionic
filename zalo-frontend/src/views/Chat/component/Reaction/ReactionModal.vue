<script setup lang="ts">
import { ref, computed } from 'vue';
import { style } from '@/assets/tailwindcss';
import CircleAvatar from '@/components/Avatar/CircleAvatar.vue';
import { useTranslate } from '@/composables/useTranslate';
import { ReactionType } from '@/types/entities';
import { EMOJI_MAP, ReactionKey } from '@/utils/constant';

const props = defineProps<{
    reactions: ReactionType[]
}>()

const { t } = useTranslate()
const activeTab = ref<'ALL' | ReactionKey>('ALL');

/**
 * 1. Gom nhóm dữ liệu theo User
 */
const userGroupedList = computed(() => {
    const groups: Record<string, {
        user: any,
        emojiTypes: Record<ReactionKey, number>,
        totalClicks: number
    }> = {};

    props.reactions.forEach(r => {
        const userId = r.createdBy?.id.toString();
        if (!userId) return;

        if (!groups[userId]) {
            groups[userId] = {
                user: r.createdBy,
                emojiTypes: Object.keys(EMOJI_MAP).reduce((acc, key) => {
                    acc[key as ReactionKey] = 0;
                    return acc;
                }, {} as Record<ReactionKey, number>),
                totalClicks: 0
            };
        }
        const type = r.type as ReactionKey;
        if (groups[userId].emojiTypes[type] !== undefined) {
            groups[userId].emojiTypes[type] += (r.count || 1);
        }

        groups[userId].totalClicks += (r.count || 1);
    });

    return Object.values(groups);
});

/**
 * 2. Xác định Top User
 */
const topUserId = computed(() => {
    const list = filteredList.value;
    if (list.length === 0) return null;

    // Lấy giá trị cao nhất trong danh sách hiện tại
    const currentTab = activeTab.value;
    let maxVal = 0;

    if (currentTab === 'ALL') {
        maxVal = list[0].totalClicks;
        // Trả về ID của người (hoặc những người) có giá trị bằng maxVal
        return list.filter(g => g.totalClicks === maxVal).map(g => g.user.id);
    } else {
        maxVal = list[0].emojiTypes[currentTab as ReactionKey];
        return list.filter(g => g.emojiTypes[currentTab as ReactionKey] === maxVal).map(g => g.user.id);
    }
});

/**
 * 3. Đếm số lượng Sidebar
 */
const counts = computed(() => {
    const tally = props.reactions.reduce((acc, r) => {
        const type = r.type as ReactionKey;
        if (acc[type] !== undefined) acc[type] += (r.count || 1);
        return acc;
    }, {
        ...Object.keys(EMOJI_MAP).reduce((a, k) => ({ ...a, [k]: 0 }), {} as any)
    });

    const totalAll = Object.values(tally).reduce((a: any, b: any) => a + b, 0);
    const filteredTally = Object.fromEntries(
        Object.entries(tally).filter(([_, val]) => (val as number) > 0)
    );

    return { all: totalAll, ...filteredTally };
});

/**
 * 4. Lọc danh sách
 */
const filteredList = computed(() => {
    let list = [...userGroupedList.value];

    if (activeTab.value !== 'ALL') {
        const tab = activeTab.value as ReactionKey;
        // Chỉ lấy những user có reaction của tab này
        list = list.filter(group => group.emojiTypes[tab] > 0);

        // Sắp xếp theo số lượng của RIÊNG tab đó
        return list.sort((a, b) => b.emojiTypes[tab] - a.emojiTypes[tab]);
    }

    // Nếu là tab ALL, sắp xếp theo tổng clicks
    return list.sort((a, b) => b.totalClicks - a.totalClicks);
});
console.log(filteredList)
</script>

<template>
    <div class="flex flex-1 h-[90%] bg-white dark:bg-[#242526] rounded-lg transition-colors duration-300">
        <div
            class="w-28 sm:w-32 bg-gray-50 dark:bg-[#1c1d1e] py-2 flex flex-col border-r border-gray-200 dark:border-gray-700 rounded-tl-lg rounded-bl-lg h-full">
            <button @click="activeTab = 'ALL'" :class="[
                'flex items-center justify-between px-4 py-2 transition-all',
                activeTab === 'ALL' ? 'bg-gray-200 dark:bg-[#3a3b3c] border-l-4 border-blue-500' : 'hover:bg-gray-100 dark:hover:bg-gray-800'
            ]">
                <span class="text-sm font-semibold text-gray-700 dark:text-gray-200">{{ t("all") }}</span>
                <span class="text-xs text-gray-400">{{ counts.all }}</span>
            </button>

            <template v-for="(count, key) in counts" :key="key">
                <button v-if="key !== 'all'" @click="activeTab = key as ReactionKey" :class="[
                    'flex items-center justify-between px-4 py-2 transition-all',
                    activeTab === key ? 'bg-gray-200 dark:bg-[#3a3b3c] border-l-4 border-blue-500' : 'hover:bg-gray-100 dark:hover:bg-gray-800'
                ]">
                    <span class="text-lg">{{ EMOJI_MAP[key as ReactionKey] }}</span>
                    <span class="text-xs font-medium text-gray-700 dark:text-gray-200">{{ count }}</span>
                </button>
            </template>
        </div>

        <div class="flex-1 overflow-y-auto p-2 custom-scrollbar">
            <div v-for="(group, index) in filteredList" :key="group.user.id"
                class="flex items-center justify-between p-2 hover:bg-gray-100 dark:hover:bg-[#3a3b3c] rounded-lg cursor-pointer transition mb-1 group">

                <div class="flex items-center gap-3">
                    <div class="relative">
                        <CircleAvatar :user="group.user" />
                        <div v-if="topUserId?.includes(group.user.id) && index == 0"
                            class="absolute flex items-center justify-center translate-y-[50%] bottom-1 right-0.5 bg-yellow-500 rounded-full w-4 h-4 border border-white dark:border-[#242526] shadow-md z-10">
                            <i class="fas fa-crown text-[8px] text-black text-center"></i>
                        </div>
                    </div>

                    <span class="font-bold text-sm text-gray-800 dark:text-gray-100">
                        {{ group.user?.username }}
                    </span>
                </div>

                <div class="flex items-center gap-1.5 px-2 py-1 rounded-full transition">
                    <div v-if="activeTab === 'ALL'" class="flex">
                        <div v-for="(count, type) in group.emojiTypes" :key="type">
                            <span v-if="count > 0" class="text-lg drop-shadow-sm">
                                {{ EMOJI_MAP[type as ReactionKey] }}
                            </span>
                        </div>
                    </div>
                    <template v-else>
                        <span class="text-lg drop-shadow-sm">{{ EMOJI_MAP[activeTab as ReactionKey] }}</span>
                    </template>

                    <span class="text-xs ml-1 font-bold" :class="[style.text.primary]">
                        {{ activeTab === 'ALL' ? group.totalClicks : group.emojiTypes[activeTab as ReactionKey] }}
                    </span>
                </div>
            </div>

            <div v-if="filteredList.length === 0"
                class="flex flex-col items-center justify-center h-40 opacity-30 dark:text-white">
                <i class="fas fa-face-meh text-3xl mb-2"></i>
                <p class="text-xs">{{ t('noData') }}</p>
            </div>
        </div>
    </div>
</template>