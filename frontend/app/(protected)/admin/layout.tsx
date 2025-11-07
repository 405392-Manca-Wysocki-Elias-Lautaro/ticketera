import { AdminSidebar } from '@/components/AdminSidebar';

export default function AdminLayout({ children }: { children: React.ReactNode; }) {
    return (

        <div className="flex h-screen overflow-hidden">
            <AdminSidebar />
            <div className="flex-1 flex flex-col h-screen pb-5">
                {children}
            </div>
        </div>
    )
}