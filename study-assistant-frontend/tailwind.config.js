/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        ink: { DEFAULT: '#1C1F26', light: '#3A3F4B' },
        charcoal: { DEFAULT: '#14171C', light: '#1D2128', lighter: '#262B33' },
        paper: { DEFAULT: '#F7F3E8', dark: '#EDE7D6', line: '#DDD4BC' },
        highlighter: { DEFAULT: '#FFB020', soft: '#FFD37A', dark: '#E09400' },
        study: { DEFAULT: '#5B8DEF', soft: '#8FB0F5' },
        good: '#4ADE80',
        bad: '#F87171',
      },
      fontFamily: {
        display: ['"Fraunces"', 'serif'],
        body: ['"Inter"', 'sans-serif'],
        mono: ['"JetBrains Mono"', 'monospace'],
      },
      backgroundImage: {
        'ruled-paper':
          'repeating-linear-gradient(to bottom, transparent, transparent 27px, #DDD4BC 28px)',
      },
      typography: {
        DEFAULT: {
          css: {
            color: '#1C1F26',
            fontFamily: 'Inter, sans-serif',
            h1: { fontFamily: 'Fraunces, serif' },
            h2: { fontFamily: 'Fraunces, serif' },
            h3: { fontFamily: 'Fraunces, serif' },
            strong: { color: '#1C1F26' },
            code: { fontFamily: 'JetBrains Mono, monospace' },
          },
        },
      },
    },
  },
  plugins: [require('@tailwindcss/typography')],
}