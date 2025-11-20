import { useEffect, useState } from 'react';

/**
 * Detecta si la app está en modo PWA instalada en iOS.
 */
export function useIsIosStandalone(): boolean {
    const [isIosStandalone, setIsIosStandalone] = useState(false);

    useEffect(() => {
        const check = () => {
            const isIos = /iphone|ipad|ipod/i.test(navigator.userAgent);
            const isStandalone =
                (window.navigator as any).standalone ||
                window.matchMedia('(display-mode: standalone)').matches;
            setIsIosStandalone(isIos && isStandalone);
        };

        check();
        window.addEventListener('resize', check);
        return () => window.removeEventListener('resize', check);
    }, []);

    return isIosStandalone;
}