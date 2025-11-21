// useDeviceContext.ts
import { useEffect, useState } from "react";

const MOBILE_BREAKPOINT = 768;

interface DeviceContext {
    isMobile: boolean;
    isStandalone: boolean;
    isDesktop: boolean;

    isMobilePWA: boolean;
    isDesktopPWA: boolean;
    isAnyPWA: boolean;
    isMobileBrowser: boolean;
    isDesktopBrowser: boolean;
    isAnyBrowser: boolean;
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
        let timeout: any;

        const check = () => {
            clearTimeout(timeout);
            timeout = setTimeout(() => {
                const isMobile = window.innerWidth < MOBILE_BREAKPOINT;
                const isDesktop = !isMobile;

                const isStandalone =
                    window.matchMedia("(display-mode: standalone)").matches ||
                    (window.navigator as any).standalone;

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
            }, 80);
        };

        check();

        const mql = window.matchMedia("(display-mode: standalone)");
        window.addEventListener("resize", check);
        mql.addEventListener("change", check);

        return () => {
            window.removeEventListener("resize", check);
            mql.removeEventListener("change", check);
            clearTimeout(timeout);
        };
    }, []);

    return device;
}
