// vite.config.ts
import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import { VitePWA } from 'vite-plugin-pwa'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  return {
    plugins: [
      react(),
      VitePWA({
        registerType: 'autoUpdate',
        includeAssets: [
          'favicon.svg',
          'robots.txt',
          'apple-touch-icon.png',
        ],
        manifest: {
          name: 'Ticketera',
          short_name: 'Ticketera',
          description: 'Compra y gestiona tus entradas fácil y rápido',
          theme_color: '#2563eb',
          background_color: '#ffffff',
          display: 'standalone',
          orientation: 'portrait',
          start_url: '/',
          icons: [
            {
              src: 'pwa-192x192.png',
              sizes: '192x192',
              type: 'image/png',
            },
            {
              src: 'pwa-512x512.png',
              sizes: '512x512',
              type: 'image/png',
            },
            {
              src: 'pwa-512x512.png',
              sizes: '512x512',
              type: 'image/png',
              purpose: 'any maskable',
            },
          ],
        },
        workbox: {
          globPatterns: ['**/*.{js,css,html,ico,png,svg}'],
          runtimeCaching: [
            {
              urlPattern: new RegExp(`^${env.VITE_APP_URL}/.*\\.(?:js|css|png|jpg|svg)$`),
              handler: 'CacheFirst',
              options: {
                cacheName: 'static-resources',
              },
            },
            {
              urlPattern: new RegExp(`^${env.VITE_API_URL}/`),
              handler: 'NetworkFirst',
              options: {
                cacheName: 'api-calls',
              },
            },
          ],
        },
      }),
    ],
    resolve: {
      alias: {
        '@': '/src',
      },
    },
  }
})
