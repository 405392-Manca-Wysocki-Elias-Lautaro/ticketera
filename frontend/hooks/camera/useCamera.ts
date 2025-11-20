import { useRef, useState } from "react";

export function useCamera({
    resolution
}: {
    resolution?: { width?: number; height?: number };
} = {}) {
    const videoRef = useRef<HTMLVideoElement | null>(null);
    const streamRef = useRef<MediaStream | null>(null);

    const [torchAvailable, setTorchAvailable] = useState(false);
    const [torchOn, setTorchOn] = useState(false);

    // --------------------------------------------------------------------
    // ▶ INICIAR CÁMARA
    // --------------------------------------------------------------------
    const startCamera = async () => {
        const stream = await navigator.mediaDevices.getUserMedia({
            video: {
                facingMode: "environment",
                width: resolution?.width ?? { ideal: 1920 },
                height: resolution?.height ?? { ideal: 1080 }
            }
        });

        streamRef.current = stream;

        if (videoRef.current) {
            videoRef.current.srcObject = stream;
            await videoRef.current.play();
        }

        // detectar torch
        const track = stream.getVideoTracks()[0];
        const capabilities = track.getCapabilities() as any;
        if ("torch" in capabilities) setTorchAvailable(true);
    };

    // --------------------------------------------------------------------
    // ▶ DETENER CÁMARA
    // --------------------------------------------------------------------
    const stopCamera = () => {
        if (streamRef.current) {
            streamRef.current.getTracks().forEach((t) => t.stop());
            streamRef.current = null;
            console.log("Cámara apagada");
        }

        if (videoRef.current) {
            videoRef.current.srcObject = null;
            console.log("Cámara apagada");
        }
    };

    // --------------------------------------------------------------------
    // ▶ TORCH
    // --------------------------------------------------------------------
    const toggleTorch = async () => {
        if (!streamRef.current) return;

        const track = streamRef.current.getVideoTracks()[0];
        await track.applyConstraints({
            advanced: [{ torch: !torchOn }] as any
        });

        setTorchOn((prev) => !prev);
    };

    return {
        videoRef,
        startCamera,
        stopCamera,
        torchAvailable,
        torchOn,
        toggleTorch
    };
}