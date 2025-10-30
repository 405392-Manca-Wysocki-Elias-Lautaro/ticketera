"use client";

import { ReactNode } from "react";
import { AuthProvider } from './providers/AuthProvider';

interface BaseLayoutProps {
    children: ReactNode;
    publicRoutes?: string[];
}

export function BaseLayout({ children, publicRoutes }: BaseLayoutProps) {
    return (
        <AuthProvider publicRoutes={publicRoutes}>
            {/* Toaster global para notificaciones */}
            <main className="min-h-screen bg-background text-foreground">
                {children}
            </main>
        </AuthProvider>
    );
}
