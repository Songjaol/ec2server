/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        accentblue: "var(--accentblue)",
        backgroundlight: "var(--backgroundlight)",
        ctaorange: "var(--ctaorange)",
        primarytext: "var(--primarytext)",
        secondarygreen: "var(--secondarygreen)",
      },

      /* ⭐ 여기에 flip-card 기능 추가됨 ⭐ */
      transformStyle: { 'preserve-3d': 'preserve-3d' },
      rotate: { 'y-180': 'rotateY(180deg)' },
      backfaceVisibility: { 'hidden': 'hidden' },
    },
  },
  plugins: [],
};
