"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";

export function useBlockBackButton(onBack: () => void) {
    const router = useRouter();

    useEffect(() => {
        // Evita SSR — se ejecuta solo en cliente
        if (typeof window === "undefined") return;

        // Inserta un state artificial para bloquear atrás
        window.history.pushState({ nextBlock: true }, "");

        const handlePopState = (event: PopStateEvent) => {
            // Si volvemos a nuestro state "protegido"
            if (event.state?.nextBlock) {
                onBack();

                // Reinsertamos el state para bloquear futuros "back"
                window.history.pushState({ nextBlock: true }, "");
            }
        };

        window.addEventListener("popstate", handlePopState);

        return () => {
            window.removeEventListener("popstate", handlePopState);
        };
    }, [onBack, router]);
}
