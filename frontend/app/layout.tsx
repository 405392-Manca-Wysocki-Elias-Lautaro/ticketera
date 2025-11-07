import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import ReactQueryProvider from '@/components/providers/QueryClientProvider';
import { Toaster } from 'sonner';
import { TooltipProvider } from '@/components/ui/tooltip';
import { showBrandConsoleMessage } from '@/utils/showBrandConsoleMessage';

const geist = Geist({ subsets: ["latin"] });
const geistMono = Geist_Mono({ subsets: ["latin"] });

export const metadata: Metadata = {
    title: "Ticketera - Plataforma de Eventos",
    description: "Compra y vende entradas de forma segura y rápida",
    manifest: "/manifest.json",
};

export function generateViewport() {
    return {
        themeColor: "#ED1C24"
    }
}

export default function RootLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    if (process.env.NODE_ENV !== "production") showBrandConsoleMessage();

    return (
        <html lang="es">
            <body className={`${geist.className} antialiased bg-background text-foreground overflow-hidden`}>
                <Toaster position="top-center" richColors />
                <ReactQueryProvider>
                    <TooltipProvider>
                        {children}
                    </TooltipProvider>
                </ReactQueryProvider>
            </body>
        </html>
    );
}
