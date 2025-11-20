import { useRef } from "react";
import { BarcodeFormat, BrowserMultiFormatReader } from "@zxing/browser";
import { DecodeHintType } from '@zxing/library';

export function useZXingReader({
    videoRef,
    onDetected,
    formats
}: {
    videoRef: React.RefObject<HTMLVideoElement | null>;
    onDetected: (code: string) => void;
    formats?: BarcodeFormat[];
}) {
    const readerRef = useRef<BrowserMultiFormatReader | null>(null);
    const cancelScanRef = useRef(false);
    const shuttingDownRef = useRef(false);

    // --------------------------------------------------------------------
    // ▶ INICIAR DECODE LOOP
    // --------------------------------------------------------------------
    const startDecode = () => {

        if (!videoRef.current) return;

        cancelScanRef.current = false;
        shuttingDownRef.current = false;

        const hints = new Map();

        if (formats && formats.length > 0) {
            hints.set(DecodeHintType.POSSIBLE_FORMATS, formats);
        }

        const reader = new BrowserMultiFormatReader(hints);
        readerRef.current = reader;

        reader.decodeFromVideoDevice(
            undefined,
            videoRef.current!,
            (result) => {
                if (cancelScanRef.current) return;
                if (!result) return;

                cancelScanRef.current = true;

                onDetected(result.getText());
            }
        );
    };

    // --------------------------------------------------------------------
    // ▶ DETENER LECTURA ZXING
    // --------------------------------------------------------------------
    const stopDecode = () => {
        if (shuttingDownRef.current) return;
        shuttingDownRef.current = true;

        cancelScanRef.current = true;

        if (readerRef.current) {
            try {
                readerRef.current.stopContinuousDecode?.();
                readerRef.current.reset?.();
            } catch { return; }

            readerRef.current = null;
        }

        setTimeout(() => {
            shuttingDownRef.current = false;
        }, 300);
    };

    return {
        startDecode,
        stopDecode
    };
}