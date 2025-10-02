import { useRegisterSW } from "virtual:pwa-register/react"

export default function PWAUpdater() {
    const {
        offlineReady: [offlineReady],
        needRefresh: [needRefresh],
        updateServiceWorker,
    } = useRegisterSW()

    return (
        <>
            {offlineReady && (
                <div className="fixed bottom-4 right-4 bg-green-600 text-white px-4 py-2 rounded-lg shadow-lg">
                    App lista para funcionar offline ✅
                </div>
            )}

            {needRefresh && (
                <div className="fixed bottom-4 right-4 bg-blue-600 text-white px-4 py-2 rounded-lg shadow-lg flex gap-2">
                    <span>Nueva versión disponible</span>
                    <button
                        className="underline font-bold"
                        onClick={() => updateServiceWorker(true)}
                    >
                        Actualizar
                    </button>
                </div>
            )}
        </>
    )
}