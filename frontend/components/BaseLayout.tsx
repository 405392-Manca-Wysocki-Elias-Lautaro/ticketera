"use client";

import { ReactNode } from "react";
import { AuthProvider } from './providers/AuthProvider';

interface BaseLayoutProps {
    children: ReactNode;
}

export function BaseLayout({ children }: BaseLayoutProps) {
    return (
        <AuthProvider>
            {/* Toaster global para notificaciones */}
            <main className="min-h-screen bg-background text-foreground">
                {children}
            </main>
        </AuthProvider>
    );
}
