'use client'

import { Navbar } from '@/components/Navbar';
import { AdminStaffSidebar } from '@/components/sidebars/AdminStaffSidebar';
import { SidebarInset, SidebarTrigger } from '@/components/ui/sidebar';
import { useAuthStore } from '@/lib/store';

export default function StaffLayout({ children }: { children: React.ReactNode }) {

    const user = useAuthStore();

    return (
        <div className="flex h-screen w-screen overflow-hidden">
            <AdminStaffSidebar user={user} />

            <SidebarInset className="flex flex-col w-full min-h-screen">

                <Navbar
                    leftSlot={
                        <div className="md:hidden">
                            <SidebarTrigger />
                        </div>
                    }
                    hideSearchOn={[
                        "/staff",
                        "/staff/events",
                    ]}

                />

                <div className="flex-1 w-full flex flex-col items-center justify-start px-4">
                    {children}
                </div>

            </SidebarInset>
        </div>
    )
}
