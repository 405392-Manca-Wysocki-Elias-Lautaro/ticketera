"use client";

import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Download, Share, PlusSquare } from "lucide-react";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import { toast } from "sonner";

export function PWAInstallButton() {
    const [deferredPrompt, setDeferredPrompt] = useState<any>(null);
    const [isIOS, setIsIOS] = useState(false);
    const [showButton, setShowButton] = useState(false);
    const [showIOSDialog, setShowIOSDialog] = useState(false);
    const [isStandalone, setIsStandalone] = useState(false);

    useEffect(() => {
        // Check if running in browser (not standalone)
        const standalone = window.matchMedia("(display-mode: standalone)").matches || (window.navigator as any).standalone;
        setIsStandalone(standalone);
        if (standalone) return;

        // Detect User Agent
        const userAgent = window.navigator.userAgent.toLowerCase();
        const isMobile = /iphone|ipad|ipod|android/.test(userAgent);
        const isIosDevice = /iphone|ipad|ipod/.test(userAgent);

        setIsIOS(isIosDevice);

        if (isMobile) {
            if (isIosDevice) {
                setShowButton(true);
            }
        }

        // Check for deferred prompt from global script (pwa-install.js)
        const prompt = (window as any)._deferredInstallPrompt;
        if (prompt) {
            setDeferredPrompt(prompt);
            setShowButton(true);
        }

        const handleBeforeInstallPrompt = (e: any) => {
            e.preventDefault();
            setDeferredPrompt(e);
            setShowButton(true);
            (window as any)._deferredInstallPrompt = e;
        };

        window.addEventListener("beforeinstallprompt", handleBeforeInstallPrompt);

        // Polling for late capture
        const interval = setInterval(() => {
            const latePrompt = (window as any)._deferredInstallPrompt;
            if (latePrompt && !deferredPrompt) {
                setDeferredPrompt(latePrompt);
                setShowButton(true);
            }
        }, 1000);

        return () => {
            window.removeEventListener("beforeinstallprompt", handleBeforeInstallPrompt);
            clearInterval(interval);
        };
    }, [deferredPrompt]);

    const handleInstallClick = async () => {
        if (isIOS) {
            setShowIOSDialog(true);
        } else {
            console.log("Intentando instalar...", deferredPrompt);
            if (!deferredPrompt) {
                // Fallback or verify if pure android check is needed
                if (/android/.test(navigator.userAgent.toLowerCase())) {
                    toast.error("No se pudo iniciar la instalación automática. Intenta desde el menú del navegador.");
                }
                return;
            }
            deferredPrompt.prompt();
            const { outcome } = await deferredPrompt.userChoice;
            if (outcome === "accepted") {
                setDeferredPrompt(null);
                setShowButton(false);
                (window as any)._deferredInstallPrompt = null;
            }
        }
    };

    if (!showButton || isStandalone) return null;

    return (
        <>
            <Button
                onClick={handleInstallClick}
                className="fixed bottom-20 right-6 z-50 rounded-full shadow-lg h-14 w-14 p-0 animate-in fade-in zoom-in duration-300 gradient-brand"
                size="icon"
                variant="default"
            >
                <Download className="h-6 w-6" />
                <span className="sr-only">Instalar App</span>
            </Button>

            <Dialog open={showIOSDialog} onOpenChange={setShowIOSDialog}>
                <DialogContent className="sm:max-w-md">
                    <DialogHeader>
                        <DialogTitle>Instalar Ticketly</DialogTitle>
                        <DialogDescription>
                            Instala esta aplicación en tu pantalla de inicio para una experiencia mejorada.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="flex flex-col gap-4 py-4">
                        <div className="flex items-center gap-4">
                            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-muted">
                                <Share className="h-5 w-5 text-foreground" />
                            </div>
                            <div className="flex-1">
                                <p className="text-sm font-medium">1. Toca el botón compartir</p>
                                <p className="text-xs text-muted-foreground">
                                    Busca el icono <Share className="inline h-3 w-3" /> en la barra inferior de Safari.
                                </p>
                            </div>
                        </div>
                        <div className="flex items-center gap-4">
                            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-muted">
                                <PlusSquare className="h-5 w-5 text-foreground" />
                            </div>
                            <div className="flex-1">
                                <p className="text-sm font-medium">2. Agregar a Inicio</p>
                                <p className="text-xs text-muted-foreground">
                                    Selecciona "Agregar a Inicio" en el menú de opciones.
                                </p>
                            </div>
                        </div>
                    </div>
                </DialogContent>
            </Dialog>
        </>
    );
}
