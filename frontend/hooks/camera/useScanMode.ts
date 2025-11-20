export function useScanMode({
    mode,
    closeOnDetect,
    stopAll,
    restart
}: {
    mode: "single" | "continuous";
    closeOnDetect: boolean;
    stopAll: () => void;
    restart: () => void;
}) {
    return (code: string) => {
        if (mode === "single") {
            stopAll();
            return;
        }

        if (closeOnDetect) {
            stopAll();
            return;
        }

        restart();
    };
}