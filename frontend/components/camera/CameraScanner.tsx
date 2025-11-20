import { useCamera } from '@/hooks/camera/useCamera';
import { useScanMode } from '@/hooks/camera/useScanMode';
import { useZXingReader } from '@/hooks/camera/useZXingReader';
import type { BarcodeFormat } from '@zxing/library';
import { Flashlight, FlashlightOff } from 'lucide-react';
import { useEffect } from "react";

interface CameraScannerProps {
    onDetected: (code: string) => void;
    mode?: "single" | "continuous";
    closeOnDetect?: boolean;
    formats?: BarcodeFormat[];
    resolution?: { width?: number; height?: number };
}

export function CameraScanner({
    onDetected,
    mode = "single",
    closeOnDetect = false,
    formats,
    resolution
}: CameraScannerProps) {
    const {
        videoRef,
        startCamera,
        stopCamera,
        torchAvailable,
        toggleTorch,
        torchOn
    } = useCamera({ resolution });

    const handleScanResult = useScanMode({
        mode,
        closeOnDetect,
        stopAll: () => {
            stopDecode();
            stopCamera();
        },
        restart: () => {
            stopDecode();
            startDecode();
        }
    });

    const fireDetected = (code: string) => {
        handleScanResult(code);
        onDetected?.(code);
    };

    const { startDecode, stopDecode } = useZXingReader({
        videoRef,
        onDetected: fireDetected,
        formats
    });

    // iniciar cámara + zxing
    useEffect(() => {
        startCamera().then(() => startDecode());

        return () => {
            stopDecode();
            stopCamera();
        };
    }, []);

    return (
        <div className="relative w-full h-[100dvh] bg-black">
            <video
                ref={videoRef}
                className="w-full h-full object-cover"
                muted
                playsInline
            />

            {/* Marco verde */}
            <div
                className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2
                border-4 border-green-400 rounded-xl animate-pulse-glow"
                style={{
                    width: "70%",
                    height: "35%",
                }}
            />

            <p className="absolute top-[70%] left-1/2 -translate-x-1/2 text-white text-base font-medium drop-shadow-md">
                Apuntá el código dentro del recuadro
            </p>


            {torchAvailable && (
                <button
                    onClick={toggleTorch}
                    className={`
                        absolute bottom-5 right-6 w-14 h-14 rounded-full
                        shadow-lg border border-white/20
                        flex items-center justify-center transition
                        ${torchOn ? "bg-yellow-400/80 text-black" : "bg-emerald-500/80 text-white"}
                    `}
                >
                    {torchOn ? <FlashlightOff /> : <Flashlight />}
                </button>
            )}

        </div>
    );
}