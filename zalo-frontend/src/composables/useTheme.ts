import { ref } from 'vue';

export type ThemeMode = 'light' | 'dark';

const themeMode = ref<ThemeMode>(
    (localStorage.getItem('theme') as ThemeMode) || 'light',
);

function applyTheme(mode: ThemeMode) {
    document.documentElement.classList.toggle(
        'ion-palette-dark',
        mode === 'dark',
    );

    document.documentElement.style.colorScheme = mode;

    localStorage.setItem('theme', mode);
}

// Áp dụng theme khi khởi tạo
applyTheme(themeMode.value);

export function useTheme() {
    function setTheme(mode?: ThemeMode) {
        if (mode) {
            themeMode.value = mode;
            applyTheme(mode);
        }else{
            applyTheme(themeMode.value)
        }
    }

    function toggleTheme() {
        setTheme(themeMode.value === 'dark' ? 'light' : 'dark');
    }

    return {
        themeMode,
        setTheme,
        toggleTheme,
    };
}