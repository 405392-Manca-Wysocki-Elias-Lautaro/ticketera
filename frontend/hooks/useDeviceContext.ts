import { useEffect, useState } from "react";

const MOBILE_BREAKPOINT = 768;

interface DeviceContext {
    // Base
    isMobile: boolean;            // Pantalla chica (<768px)
    isStandalone: boolean;        // PWA instalada (standalone)
    isDesktop: boolean;           // Pantalla grande (>=768px)

    // Combinaciones específicas
    isMobilePWA: boolean;         // PWA instalada en un dispositivo móvil
    isDesktopPWA: boolean;        // PWA instalada en escritorio
    isAnyPWA: boolean;            // PWA instalada (mobile o desktop)
    isMobileBrowser: boolean;     // Navegador en móvil (no PWA)
    isDesktopBrowser: boolean;    // Navegador en escritorio (no PWA)
    isAnyBrowser: boolean;        // Navegador (mobile o desktop)
}

export function useDeviceContext(): DeviceContext {
    const [device, setDevice] = useState<DeviceContext>({
        isMobile: false,
        isStandalone: false,
        isDesktop: false,
        isMobilePWA: false,
        isDesktopPWA: false,
        isAnyPWA: false,
        isMobileBrowser: false,
        isDesktopBrowser: false,
        isAnyBrowser: false,
    });

    useEffect(() => {
        const check = () => {
            // 🔹 Base
            const isMobile = window.innerWidth < MOBILE_BREAKPOINT;
            const isDesktop = !isMobile;

            // 🔹 Detectar modo standalone (PWA instalada)
            const isStandalone =
                window.matchMedia("(display-mode: standalone)").matches ||
                (window.navigator as any).standalone; // soporte iOS Safari

            // 🔹 Combinaciones
            const isMobilePWA = isMobile && isStandalone;
            const isDesktopPWA = isDesktop && isStandalone;
            const isAnyPWA = isStandalone;

            const isMobileBrowser = isMobile && !isStandalone;
            const isDesktopBrowser = isDesktop && !isStandalone;
            const isAnyBrowser = !isStandalone;

            setDevice({
                isMobile,
                isStandalone,
                isDesktop,
                isMobilePWA,
                isDesktopPWA,
                isAnyPWA,
                isMobileBrowser,
                isDesktopBrowser,
                isAnyBrowser,
            });
        };

        // Ejecutar al montar
        check();

        // Escuchar cambios de tamaño y modo standalone
        const mql = window.matchMedia("(display-mode: standalone)");
        window.addEventListener("resize", check);
        mql.addEventListener("change", check);

        return () => {
            window.removeEventListener("resize", check);
            mql.removeEventListener("change", check);
        };
    }, []);

    return device;
}
