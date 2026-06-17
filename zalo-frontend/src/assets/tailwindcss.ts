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
    hover: "hover:bg-gray-200 dark:hover:bg-slate-700",
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
    primary: ""
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

export const oaStyle = {
  text: {
    hoverBlue: `
      hover:text-blue-600/90 
      dark:hover:text-blue-400
    `,

    active: `
      text-blue-600
      dark:text-blue-400
    `,
    primary: `
      text-slate-900
      dark:text-slate-100
    `,
    secondary: `
      text-slate-600
      dark:text-slate-400
    `,
    muted: `
      text-slate-400
      dark:text-slate-500
    `,
    inverse: `
      text-white
      dark:text-slate-900
    `,
    hover: `
      hover:text-slate-900
      dark:hover:text-slate-200
    `,
  },

  bg: {
    active: `
      bg-blue-500
      dark:bg-blue-600
    `,
    primary: `
      bg-white
      dark:bg-slate-950
    `,

    secondary: `
      bg-slate-50
      dark:bg-slate-900
    `,

    tertiary: `
      bg-slate-100
      dark:bg-slate-800
    `,

    hover: `
      hover:bg-slate-100
      dark:hover:bg-slate-800
    `,

    softHover: `
      hover:bg-slate-50
      dark:hover:bg-slate-900
    `,

    hoverBlue: `
      hover:bg-blue-200/60 
      dark:hover:bg-blue-700
    `,

    hoverActive: `
      hover:bg-blue-600
      dark:hover:bg-blue-700
    `,
  },

  border: {
    active: `
      border-blue-600
      dark:border-blue-400
    `,

    primary: `
      border-slate-200
      dark:border-slate-800
    `,

    secondary: `
      border-slate-100
      dark:border-slate-900
    `,
  },

  icon: {
    default: `
      text-slate-500
      dark:text-slate-400
    `,

    hover: `
      hover:text-slate-700
      dark:hover:text-slate-200
    `,

    active: `
      text-blue-600
      dark:text-blue-400
    `,
  },

  button: {
    base: `
      bg-slate-100
      hover:bg-slate-200

      dark:bg-slate-800
      dark:hover:bg-slate-700

      text-slate-700
      dark:text-slate-200

      transition-colors duration-200
    `,

    primary: `
      bg-blue-600
      hover:bg-blue-700

      dark:bg-blue-500
      dark:hover:bg-blue-600

      text-white

      transition-colors duration-200
    `,
  },

  item: {
    base: `
      cursor-pointer
      transition-colors duration-200

      text-slate-700
      dark:text-slate-200
    `,

    hover: `
      hover:bg-slate-100
      dark:hover:bg-slate-800
    `,

    active: `
      bg-slate-200
      dark:bg-slate-700
    `,
  },

  card: `
    bg-white
    dark:bg-slate-900

    border border-slate-200
    dark:border-slate-800

    shadow-sm
  `,
};