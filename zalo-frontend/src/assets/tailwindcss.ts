export const style = {
  text: {
    primary: "text-gray-900 dark:text-gray-100",
    secondary: "text-gray-600 dark:text-gray-300",
    muted: "text-gray-400 dark:text-gray-500",
    inverse: "text-white dark:text-black",
  },

  bg: {
    primary: "bg-white dark:bg-[#0f172a]",
    secondary: "bg-gray-100 dark:bg-slate-800",
    hover: "hover:bg-gray-100 dark:hover:bg-slate-700",
    softHover: "hover:bg-gray-50 dark:hover:bg-slate-800",
  },

  border: {
    primary: "border-gray-200 dark:border-slate-700",
    secondary: "border-gray-100 dark:border-slate-800",
  },

  icon: {
    default: "text-gray-400 dark:text-gray-400",
    hover: "hover:text-gray-700 dark:hover:text-gray-200",
    active: "text-blue-500",
  },

  button: {
    base: `
      bg-gray-200 hover:bg-gray-300 
      dark:bg-slate-700 dark:hover:bg-slate-600 
      text-gray-800 dark:text-gray-200
      transition
    `,
  },

  item: {
    base: `
      cursor-pointer transition
      text-gray-700 dark:text-gray-200
    `,
    hover: `
      hover:bg-gray-50 dark:hover:bg-slate-800
    `,
    active: `
      bg-gray-100 dark:bg-slate-700
    `
  }
}