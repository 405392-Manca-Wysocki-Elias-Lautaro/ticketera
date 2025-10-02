/// <reference types="vite/client" />

declare module 'virtual:pwa-register/react' {
    export function useRegisterSW(): {
        offlineReady: [boolean, (ready: boolean) => void]
        needRefresh: [boolean, (refresh: boolean) => void]
        updateServiceWorker: (reloadPage?: boolean) => Promise<void>
    }
}
