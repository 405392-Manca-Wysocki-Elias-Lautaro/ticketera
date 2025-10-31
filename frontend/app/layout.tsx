import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css"; // estilos globales SSR-safe
import ReactQueryProvider from '@/components/providers/QueryClientProvider';
import { Toaster } from 'sonner';

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
  return (
    <html lang="es">
      <body className={`${geist.className} antialiased bg-background text-foreground`}>
        <Toaster position="top-right" richColors />
        <ReactQueryProvider>
          {children}
        </ReactQueryProvider>
      </body>
    </html>
  );
}
