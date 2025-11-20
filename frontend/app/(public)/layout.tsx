import { BaseLayout } from '@/components/BaseLayout';
import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Ticketly - Entradas y Eventos",
  description: "Descubrí y comprá entradas a los mejores eventos",
};

export default function PublicLayout({ children, }: { children: React.ReactNode; }) {
  return (
    <BaseLayout children={children} />
  );
}
