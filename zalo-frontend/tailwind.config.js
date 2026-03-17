/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: "class",
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
    // Nếu dùng Ionic components thì thêm
    "./src/components/**/*.{vue,js,ts}",
    "./src/views/**/*.{vue,js,ts}",
  ],
  theme: {
    extend: {
      
    }
  },
  plugins: [],
}