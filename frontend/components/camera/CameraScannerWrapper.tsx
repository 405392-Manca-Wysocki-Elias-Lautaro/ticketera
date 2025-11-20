import { X } from "lucide-react";
import { Button } from "@/components/ui/button";
import { BarcodeFormat } from '@zxing/library';
import { useBlockBackButton } from '@/hooks/camera/useBlockBackButton';
import { CameraScanner } from './CameraScanner';

interface CameraScannerWrapperProps {
    title?: string;
    onClose: () => void;
    onDetected: (code: string) => void;
    closeOnDetect?: boolean;
    mode?: "single" | "continuous";
    formats?: BarcodeFormat[];
    resolution?: { width?: number; height?: number };
}

export function CameraScannerWrapper({
    title = "",
    onClose,
    onDetected,
    closeOnDetect = false,
    mode = "single",
    formats = [BarcodeFormat.QR_CODE],
    resolution = { width: 1280, height: 720 }
}: CameraScannerWrapperProps) {

    useBlockBackButton(onClose);

    return (
        <div className="fixed inset-0 bg-black z-[9999] flex flex-col">

            {/* HEADER */}
            <div className="
                absolute top-0 left-0
                w-full px-4 py-3 flex justify-between items-center
                bg-black/40 backdrop-blur-md border-b border-white/10
                z-[10000]
            ">
                <h2 className="text-white text-lg font-semibold">{title}</h2>

                <Button
                    variant="ghost"
                    className="
                        top-6 right-6 w-12 h-12
                        rounded-full bg-white/50 text-white
                        flex items-center justify-center
                        hover:bg-white/30 transition"
                    onClick={onClose}>
                    <X />
                </Button>
            </div>

            {/* SCANNER */}
            <div className="flex-1 relative">
                <CameraScanner
                    onDetected={onDetected}
                    closeOnDetect={closeOnDetect}
                    mode={mode}
                    formats={formats}
                    resolution={resolution}
                />
            </div>
        </div>
    );
}