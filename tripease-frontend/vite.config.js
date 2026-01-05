import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 5173,  // TripEase frontend on port 5173
    proxy: {
      '/auth': {
        target: 'http://localhost:8081',  // TripEase backend on port 8081
        changeOrigin: true,
      },
      '/customer': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/driver': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/booking': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/fare': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/ride': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
    },
  },
})

