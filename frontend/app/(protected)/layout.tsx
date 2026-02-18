"use client";

import { BaseLayout } from '@/components/BaseLayout';
import { AuthProvider } from '@/components/providers/AuthProvider';


import { PWAInstallButton } from '@/components/pwa/PWAInstallButton';

export default function ProtectedLayout({ children }: { children: React.ReactNode; }) {
  return (
    <AuthProvider>
      <BaseLayout children={children} />
      <PWAInstallButton />
    </AuthProvider>
  );
} 
